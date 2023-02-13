package demo.rpc.client.netty;

import demo.rpc.common.constant.CompressType;
import demo.rpc.common.constant.MessageType;
import demo.rpc.common.constant.SerializeType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.protocol.RpcResponse;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        try {
            log.info("client receive msg : [{}]", msg);
            if (msg.getMessageType() == MessageType.RESPONSE.getValue()) {
                RpcResponse<?> rpcResponse = (RpcResponse<?>) msg.getData();
                //本地缓存的消息表
                UnProcessMessage.complete(rpcResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setSerializeType(SerializeType.PROTOSTUFF.getValue());
                rpcMessage.setMessageType(MessageType.HEARTBEAT.getValue());
                rpcMessage.setCompressorType(CompressType.DUMMY.getValue());
                //失败则关闭
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception: ", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
