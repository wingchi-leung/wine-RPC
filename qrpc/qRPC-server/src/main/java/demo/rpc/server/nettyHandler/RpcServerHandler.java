package demo.rpc.server.nettyHandler;

import demo.rpc.common.constant.MessageType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.protocol.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage) throws Exception {
        try {
            if (rpcMessage.getMessageType() != MessageType.REQUEST.getValue()) return;
            RpcRequest request = (RpcRequest) rpcMessage.getData();
            Object result = new Object();
            try {
                String methodName = request.getMethodName();
                Object[] params = request.getParams();
                String interfaceName = request.getInterfaceName();
                Class<?>[] paramsTypes = request.getParamsTypes();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {

        }
    }
}
