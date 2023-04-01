package demo.rpc.server.nettyHandler;

import demo.rpc.common.constant.MessageType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.protocol.RpcResponse;
import demo.rpc.server.component.MyMetrics;
import demo.rpc.server.server.RpcServiceCache;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {

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
            //获取请求体
            RpcRequest request = (RpcRequest) rpcMessage.getData();
            Object result = new Object();
            long startTime = System.nanoTime();
            try {
                /**
                 * 通过反射调用本地方法
                 */
                Object service = RpcServiceCache.getService(request.getRpcServiceForCache());
                Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
                String methodName = method.getName();
                result = method.invoke(service, request.getParams());
                MyMetrics.rpcRequests.labels(methodName, "success").inc();
                log.info("service:[{}] successfully invoke method:[{}]. Result is :{}", request.getInterfaceName(), request.getMethodName(), result);
            } catch (Exception e) {
                MyMetrics.rpcRequests.labels(request.getMethodName(), "fail").inc();
                throw e;
            }finally {
                long endTime = System.nanoTime();
                MyMetrics.rpcRequestLatency.labels(request.getMethodName()).observe((endTime - startTime) / 1e9);
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
                log.error("not writable not, message droped!");
            }
            //LEARN
            ctx.writeAndFlush(rpcMessageBuilder.build()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } finally {

            ReferenceCountUtil.release(rpcMessage);
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
