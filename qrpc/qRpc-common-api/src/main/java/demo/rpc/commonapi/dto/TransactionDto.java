package demo.rpc.commonapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {
    private BigDecimal amount;
    private Long fromAccountId;
    private Long toAccountId;
    private String fromAccountName;
    private String toAccountName;
    private TransactionType transactionType;
    private BigDecimal cardNo;
}
