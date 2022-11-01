package demo.rpc.common.constant;

import java.util.concurrent.atomic.AtomicLong;

public interface ProtocolConstants {
    /**
     * 魔数
     */
    byte[] MAGIC_CODE_BYTES = {(byte) 0x3e, (byte) 0x2b};

    /**
     * 版本号
     */
    byte VERSION = 1;

    /**
     * 请求Id
     */
    AtomicLong REQUEST_ID = new AtomicLong(0);

    String PING_DATA = "ping";
    String PONG_DATA = "pong";


    /**
     * 魔数长度
     */
    int MAGIC_LENGTH = 2;

    /**
     * 版本号长度
     */
    int VERSION_LENGTH = 1;

    /**
     * 总长度字段的长度
     */
    int FULL_LENGTH_LENGTH = 4;

    /**
     * 最大帧长度
     */
    int MAX_FRAME_LENTH = 8 * 1024 * 1024;


    int MESSAGE_TYPE_LENGTH = 1;
    int CODEC_LENGTH = 1;
    int REQUEST_ID_LENGTH = 8;

    /**
     * 请求头长度
     */
    int HEADER_LENGTH = MAGIC_LENGTH + VERSION_LENGTH +
            FULL_LENGTH_LENGTH + MESSAGE_TYPE_LENGTH +
            CODEC_LENGTH + CODEC_LENGTH + REQUEST_ID_LENGTH;
}
