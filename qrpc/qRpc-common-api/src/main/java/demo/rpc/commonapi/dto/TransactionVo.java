package demo.rpc.commonapi.dto;

import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionVo {
    private Long fromAccountId;
    private Long toAccountId;
    private String fromAccountName;
    private String toAccountName;
    private BigDecimal amount;
    private LocalDateTime createTime;
}
