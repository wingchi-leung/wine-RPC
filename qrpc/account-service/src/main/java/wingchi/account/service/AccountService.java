package wingchi.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wingchi.account.dto.AccountVo;
import wingchi.account.entity.AccountDo;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService extends IService<AccountDo> {
    AccountVo getAccount(Long accountId);

    void deductBalance(Long accountId, BigDecimal amount);

    void addBalance(Long accountId, BigDecimal amount);

    List<AccountVo> getAccountList();
}
