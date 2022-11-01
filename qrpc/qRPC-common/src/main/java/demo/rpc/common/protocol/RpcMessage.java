package demo.rpc.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC通信的通用消息体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMessage {
    /**
     * 序列化类型
     */
    private byte serializeType;
    /**
     * 请求Id
     */
    private long requestId;

    /**
     * 请求体
     */
    private Object data;

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 压缩类型
     */
    private byte compressorType;
}
