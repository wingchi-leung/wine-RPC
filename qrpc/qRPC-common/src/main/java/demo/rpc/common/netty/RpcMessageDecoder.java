package demo.rpc.common.netty;

import cn.hutool.socket.protocol.Protocol;
import demo.rpc.common.constant.MessageType;
import demo.rpc.common.constant.ProtocolConstants;
import demo.rpc.common.constant.SerializeType;
import demo.rpc.common.protocol.RpcMessage;
import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.protocol.RpcResponse;
import demo.rpc.common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

import static demo.rpc.common.constant.ProtocolConstants.*;

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
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        super(
                MAX_FRAME_LENTH,
                MAGIC_LENGTH + VERSION_LENGTH,
                FULL_LENGTH_LENGTH,
                //长度修正：
                -(MAGIC_LENGTH + VERSION_LENGTH + FULL_LENGTH_LENGTH),
                0,
                true
        );
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decode;
            try {
                return decodeFrame(frame);
            } catch (Exception ex) {
                log.error("Decoder frame error...", ex);
            } finally {
                frame.release();
            }

        }
        return decode;
    }

    private Object decodeFrame(ByteBuf frame) throws IllegalAccessException {
        byte b0 = frame.readByte();
        byte b1 = frame.readByte();
        if (MAGIC_CODE_BYTES[0] != b0 || MAGIC_CODE_BYTES[1] != b1) {
            throw new IllegalAccessException("Unknown magic code : " + b0 + " ," + b1);
        }
        // TODO 版本兼容性
        byte version = frame.readByte();
        int fullLength = frame.readInt();
        byte message = frame.readByte();
        byte serialize = frame.readByte();
        byte compress = frame.readByte();
        long requestId = frame.readLong();
        RpcMessage rpcMessage = RpcMessage.builder().messageType(message).serializeType(serialize)
                .compressorType(compress).requestId(requestId).build();
        if (message == MessageType.HEARTBEAT.getValue()) {
            //心跳包
            return rpcMessage;
        }
        int bodyLen = fullLength - HEADER_LENGTH;
        if(bodyLen==0)
            return rpcMessage;

        byte []data = new byte[bodyLen];
        frame.readBytes(data);

        SerializeType serializeType = SerializeType.fromValue(serialize);
        if(serializeType==null){
            throw new IllegalAccessException("Unknown codec Type : ");
        }
        // NOTE 先解压再序列化， 因为encode的时候就没有压缩，所以这里省略了。

        ServiceLoader<Serializer> serializers = ServiceLoader.load(Serializer.class);
        Serializer serializer = serializers.iterator().next();
        //根据消息类型获得class信息。
        Class<?> clazz = message == MessageType.REQUEST.getValue()? RpcRequest.class : RpcResponse.class;
        Object o = serializer.deSerialize(data, clazz);

        rpcMessage.setData(o);
        return rpcMessage;

    }
}
