package demo.rpc.commonapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AccountVo {
    private Long accountId;
    private Long toAccountId;
    private String fullName;
    private BigDecimal number;
    private BigDecimal balance;
    private BigDecimal amount;
    private String email;
    private Boolean isAdmin;
    private String password;
    private String phone;
    private TransactionType transactionType;
    private List<TransactionVo> transactions;
}

