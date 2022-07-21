package demo.rpc.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    REQUEST((byte) 1),
    RESPONSE((byte)2),
    HEARTBEAT((byte)3);
    private final byte value ;


}
