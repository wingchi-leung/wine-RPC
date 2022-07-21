package demo.rpc.common.netty;

import demo.rpc.common.compress.DummyCompress;
import demo.rpc.common.constant.CompressType;
import demo.rpc.common.constant.MessageType;
import demo.rpc.common.constant.ProtocolConstants;
import demo.rpc.common.constant.SerializeType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * 0   1   2       3   4   5   6   7           8        9        10   11  12  13  14  15  16  17  18
 * +---+---+-------+---+---+---+---+-----------+---------+--------+---+---+---+---+---+---+---+---+
 * | magic |version|  full length  |messageType|serialize|compress|           RequestId           |
 * +---+---+-------+---+---+---+---+-----------+---------+--------+---+---+---+---+---+---+---+---+
 * |                                                                                              |
 * |                                         body                                                 |
 * |                                                                                              |
 * |                                        ... ...                                               |
 * +----------------------------------------------------------------------------------------------+
 * 2B-magic魔数  0x3B
 * 1B-version 版本号
 * 4B-总长度   int
 * 1B-消息类型 byte
 * 1B-序列化 byte
 * 1B-压缩   byte
 * 8B-请求Id long
 * 18-请求体 Object
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        //2B magic code (魔数)
        out.writeBytes(ProtocolConstants.MAGIC_CODE_BYTES);
        //1B version (版本)
        out.writeByte(ProtocolConstants.VERSION);
        // 4B full length,先跳过,后面再填。
        out.writerIndex(out.writerIndex()+ProtocolConstants.FULL_LENGTH_LENGTH);
        // 1B message type (消息类型)
        out.writeByte(rpcMessage.getMessageType());
        // 1B serialize (序列化)
        out.writeByte(rpcMessage.getSerializeType());
        // 1B compressor (压缩)
        out.writeByte(rpcMessage.getCompressorType());
        // 8B RequestId 请求Id
        out.writeLong(rpcMessage.getRequestId());
        //写body
        int bodyLen = writeBody(rpcMessage, out);
        int writerIndex = out.writerIndex();

        out.writerIndex(ProtocolConstants.MAGIC_LENGTH + ProtocolConstants.VERSION_LENGTH);
        //rpcMessage的总长度
        out.writeInt(ProtocolConstants.HEADER_LENGTH + bodyLen);
        // 复原写指针
        out.writerIndex(writerIndex);
        log.debug("编码完成:{}",out.toString());

    }

    /**
     * 写body
     * 1. 检查是否为心跳包
     * 2.序列化,压缩
     * 3.写入bytebuff，返回body的长度
     *
     * @param rpcMessage
     * @param out
     * @return
     */
    private int writeBody(RpcMessage rpcMessage, ByteBuf out) {
        byte messageType = rpcMessage.getMessageType();
        if (messageType == MessageType.HEARTBEAT.getValue()) {
//            如果是心跳包，没有body，长度为0
            return 0;
        }
        SerializeType serializeType = SerializeType.fromValue(rpcMessage.getSerializeType());
        if (serializeType == null) {
            throw new IllegalArgumentException("codec type not found");
        }
        //将对象序列化
        ServiceLoader<Serializer> serializers = ServiceLoader.load(Serializer.class);
        Serializer serializer = serializers.iterator().next();
        log.debug("找到序列化器：,{}", serializer.toString());
        byte[] data = serializer.serialize(rpcMessage.getData());

//        CompressType compressType = CompressType.fromValue(rpcMessage.getCompressorType());
        out.writeBytes(data);
        return data.length;
    }
}
