package demo.rpc.commonapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionVo {
    private Long fromAccountId;
    private Long toAccountId;
    private String fromAccountName;
    private String toAccountName;
    private BigDecimal amount;
    private String createTime;
    private TransactionType transactionType;
}
