package wingchi.account.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 下面三个注解是lombok的减负注解，减少一些结构性的编码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {
    private static final long serialVersionUID = 451834802206432869L;
    /**
     * 1：成功,其他失败
     */
    private int code;
    /**
     * message提示内容，当接口异常时提示异常信息
     */
    private String message;
    /**
     * 携带真正需要返回的数据
     */
    private Object data;

    public static Result success(Object obj) {
        return new Result(1, null, obj);
    }
}
