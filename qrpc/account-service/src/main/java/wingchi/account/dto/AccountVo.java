package wingchi.account.dto;

import lombok.Data;
import wingchi.account.entity.AccountDo;

import java.math.BigDecimal;

@Data
public class AccountVo {
    private String name;
    private Long AccountId;
    private BigDecimal balance;

    public static AccountVo fromDo(AccountDo account) {
        AccountVo accountVo = new AccountVo();
        accountVo.setAccountId(account.getAccountId());
        accountVo.setName(account.getName());
        accountVo.setBalance(account.getBalance());
        return accountVo;
    }
}

