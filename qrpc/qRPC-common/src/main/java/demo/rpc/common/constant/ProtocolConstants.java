package demo.rpc.common.constant;

public interface ProtocolConstants {
    byte[] MAGIC_CODE_BYTES = {(byte) 0x3e, (byte) 0x2b};
    byte VERSION = 1;
    Long REQUEST_ID = 0L;
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

    int MAX_FRAME_LENTH = 8 * 1024 * 1024;
    int MESSAGE_TYPE_LENGTH = 1;
    int CODEC_LENGTH = 1;
    int REQUEST_ID_LENGTH = 8;

    int HEADER_LENGTH = MAGIC_LENGTH + VERSION_LENGTH +
            FULL_LENGTH_LENGTH + MESSAGE_TYPE_LENGTH +
            CODEC_LENGTH + CODEC_LENGTH + REQUEST_ID_LENGTH;
}
