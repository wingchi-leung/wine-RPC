package demo.rpc.server.nettyHandler;

import demo.rpc.common.constant.MessageType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.protocol.RpcResponse;
import demo.rpc.server.server.RpcServiceCache;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.concurrent.*;

@Slf4j
@Component
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {


    private static final int MAX_TOKENS = 10;
    private static final int REFILL_INTERVAL = 1000 / MAX_TOKENS;
    private final BlockingQueue<Integer> tokensQueue = new LinkedBlockingQueue<>(MAX_TOKENS);
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    @Autowired
    private MeterRegistry registry;

    private Counter rpcRequestSuccess;
    private Counter rpcRequestFails;
    private Timer timer;
    private Counter errors;

    @PostConstruct
    public void init() {
        rpcRequestSuccess = registry.counter("rpc_requests_total", "method", "success");
        rpcRequestFails = registry.counter("rpc_requests_total", "method", "fails");
        errors = registry.counter("request_error_rate", "method", "error");
        timer = registry.timer("process_time", "method", "time");

        scheduler.scheduleAtFixedRate(() -> {
            tokensQueue.offer(1);
        }, 0, REFILL_INTERVAL, TimeUnit.MILLISECONDS);

    }

    /**
     * 操作：
     * 接受到客户端发来的消息
     * 1. 根据rpcMessage和里面的RpcRequest，获取需要的本地服务,方法名称,入参
     * 2. 调用本地方法
     * 3. 构造RpcMessage
     * 4. 判断channel的状态,将返回的消息发送给客户端
     *
     * @param ctx
     * @param rpcMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage rpcMessage) throws Exception {
        try {
            if (rpcMessage.getMessageType() != MessageType.REQUEST.getValue())
                return;
            //如果队列为空则阻塞等待
            tokensQueue.take();
            //获取请求体
            RpcRequest request = (RpcRequest) rpcMessage.getData();
            Object result;
            long startTime = System.nanoTime();
            try {
                /**
                 * 通过反射调用本地方法
                 */
                Object service = RpcServiceCache.getService(request.getRpcServiceForCache());
                Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
                result = method.invoke(service, request.getParams());
                log.info("service:[{}] successfully invoke method:[{}]. Result is :{}", request.getInterfaceName(), request.getMethodName(), result);
                rpcRequestSuccess.increment();
            } catch (Exception e) {
                rpcRequestFails.increment();
                result = e;
            } finally {
                long endTime = System.nanoTime();
                timer.record(endTime - startTime, TimeUnit.NANOSECONDS);
                getErrorRate();
            }
            /**
             * 构建响应并返回给客户端。
             */
            RpcMessage.RpcMessageBuilder rpcMessageBuilder = RpcMessage.builder()
                    .serializeType(rpcMessage.getSerializeType())
                    .compressorType(rpcMessage.getCompressorType())
                    .messageType(MessageType.RESPONSE.getValue())
                    .requestId(rpcMessage.getRequestId());
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                RpcResponse<Object> response = RpcResponse.success(result, rpcMessage.getRequestId());
                rpcMessageBuilder.data(response);
            } else {
                rpcMessageBuilder.data(RpcResponse.fail());
                log.error("channel can‘t write, message dropped!");
            }
            ctx.writeAndFlush(rpcMessageBuilder.build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } catch (Exception e){
            throw e;
        }
        finally{
            ReferenceCountUtil.release(rpcMessage);
        }
    }

    private void getErrorRate() {
        Double total = rpcRequestFails.count() + rpcRequestSuccess.count();
        if (total.equals(new Double(0))) {
            errors.increment(0.0);
        } else {
            double errorRate = rpcRequestFails.count() / total;
            errors.increment(errorRate);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen,closing the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception...", cause);
        ctx.close();
    }
}
