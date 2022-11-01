package demo.rpc.common.protocol;

import demo.rpc.common.constant.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse<T> {
    /**
     * 请求Id
     */
    private long requestId;
    /**
     * 响应码
     */
    private Integer code ;
    /**
     * 提示消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data ;


    public static <T> RpcResponse<T> success(T data,long requestId){
        return RpcResponse.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data).requestId(requestId)
                .build();
    }

    public static <T> RpcResponse<T> fail(){
        return RpcResponse.<T>builder()
                .code(ResponseCode.FAIL.getCode())
                .message(ResponseCode.FAIL.getMessage())
                .build();
    }

    public static <T> RpcResponse<T> fail(T data,long requestId){
        return RpcResponse.<T>builder()
                .code(ResponseCode.FAIL.getCode())
                .message(ResponseCode.FAIL.getMessage())
                .data(data).requestId(requestId)
                .build();
    }
}
