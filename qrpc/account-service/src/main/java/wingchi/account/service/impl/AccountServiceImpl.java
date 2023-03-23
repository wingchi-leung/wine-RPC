package wingchi.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.rpc.common.annotation.RpcService;
import org.springframework.stereotype.Service;
import wingchi.account.component.AccountStatus;
import wingchi.account.dto.AccountVo;
import wingchi.account.entity.AccountDo;
import wingchi.account.mapper.AccountMapper;
import wingchi.account.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RpcService(value = AccountService.class, version = "1.0")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDo> implements AccountService {

    public AccountVo getAccount(Long accountId) {
        AccountDo account = this.getById(accountId);
        return AccountVo.fromDo(account);
    }

    public void deductBalance(Long accountId, BigDecimal amount) {
        AccountDo account = this.getById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        this.updateById(account);
    }

    public void addBalance(Long accountId, BigDecimal amount) {
        AccountDo account = this.getById(accountId);
        account.setBalance(account.getBalance().add(amount));
        this.updateById(account);
    }

    @Override
    public List<AccountVo> getAccountList() {
        LambdaQueryWrapper<AccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDo::getStatus, AccountStatus.INUSE.name());
        List<AccountDo> accountDos = baseMapper.selectList(queryWrapper);
        return accountDos.stream().map(AccountVo::fromDo).collect(Collectors.toList());
    }

}
