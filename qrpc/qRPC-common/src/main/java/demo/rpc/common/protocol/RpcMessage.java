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
    private byte serializeType;
    private long requestId;
    private Object data;
    private byte messageType;
    private byte compressorType;
}
