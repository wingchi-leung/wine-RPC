package wingchi.account.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author fanxb
 * @date 2021-09-24-下午4:37
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandle {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) throws Exception {
        log.error("捕获到错误：{}", e.getMessage(), e);
        throw  new Exception(e.getMessage());
    }
}
