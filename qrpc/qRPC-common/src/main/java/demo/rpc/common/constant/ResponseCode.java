package demo.rpc.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200,"success"),
    FAIL(500,"fail");
    private final int code;
    private final String message;


}
