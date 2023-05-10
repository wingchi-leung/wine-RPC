package demo.rpc.client.reflect;

import demo.rpc.client.compoment.PrometheusMetric;
import demo.rpc.client.compoment.SpringBeanContext;
import demo.rpc.client.discovery.ZkDiscovery;
import demo.rpc.client.dto.AsyncResult;
import demo.rpc.client.dto.RpcResult;
import demo.rpc.client.loadbalance.LoadBalance;
import demo.rpc.client.loadbalance.RoundRobin;
import demo.rpc.client.netty.NettyClient;
import demo.rpc.client.netty.UnProcessMessage;
import demo.rpc.common.constant.CompressType;
import demo.rpc.common.constant.MessageType;
import demo.rpc.common.constant.ProtocolConstants;
import demo.rpc.common.constant.SerializeType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.protocol.RpcResponse;
import demo.rpc.common.registry.Discovery;
import demo.rpc.common.registry.URL;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j

public class NettyInvoker {
    private final LoadBalance loadBalance = new RoundRobin();

    private final Discovery discovery = ZkDiscovery.getInstance();

    private final NettyClient nettyClient = NettyClient.getInstance();

    /**
     * 调用RPC方法
     *
     * @param request
     * @return
     */
    public RpcResult invoke(RpcRequest request) {
        int retry = 0;
        //构建URL信息
        URL url = URL.builder().interfaceName(request.getInterfaceName())
                .version(request.getVersion()).build();
        //查找注册中心中符合的服务
        List<URL> urlList = discovery.discover(url);
        URL select = loadBalance.select(urlList, request);
        PrometheusMetric prometheusMetric = SpringBeanContext.getContext().getBean(PrometheusMetric.class);
        while(retry<3){
            try {
                return doInvoke(request, select);
            } catch (Exception e) {
                log.error("invoke error:{}", e.getMessage());
                retry++;
                log.warn("超时重试retry:{}", retry);
                if(retry>1){
                    prometheusMetric.getRpcRetryCounter().increment();
                }
            }
        }
        throw new RuntimeException("发生未知错误！");

    }

    private RpcResult doInvoke(RpcRequest request, URL select) {
        InetSocketAddress socketAddress = new InetSocketAddress(select.getHost(), select.getPort());
        Channel channel = nettyClient.getChannel(socketAddress);
        if (!channel.isActive()) {
            throw new RuntimeException("channel is not active. address = " + socketAddress);
        }
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        RpcMessage rpcMessage = buildRpcMessage(request);
        UnProcessMessage.put(rpcMessage.getRequestId(), resultFuture);
        channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
            // TODO void方法，返回值为空，这里就阻塞住了（拿不到结果）
            if (future.isSuccess()) {
                log.info("client send message :[{}]", rpcMessage);
            } else {
                future.channel().close();
                resultFuture.completeExceptionally(future.cause());
                log.error("client send failed: ", future.cause());
            }
        });
        return new AsyncResult(resultFuture);

    }

    private RpcMessage buildRpcMessage(RpcRequest request) {

        return RpcMessage.builder()
                .data(request)
                .messageType(MessageType.REQUEST.getValue())
                .serializeType(SerializeType.PROTOSTUFF.getValue())
                .compressorType(CompressType.DUMMY.getValue())
                .requestId(ProtocolConstants.REQUEST_ID.getAndIncrement())
                .build();
    }
}
