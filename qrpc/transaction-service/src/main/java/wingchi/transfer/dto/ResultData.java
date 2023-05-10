package wingchi.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultData<T> {
    private Integer code;
    private String msg;
    private Long total;
    private Long pages;
    private T data;


    public static <T>  ResultData<T> success(T Data,long pages,long total){
        ResultData<T> resultData = new ResultData<>();
        resultData.data=Data;
        resultData.total=total;
        resultData.code=200;
        resultData.pages = pages;
        return resultData;
    }

}

