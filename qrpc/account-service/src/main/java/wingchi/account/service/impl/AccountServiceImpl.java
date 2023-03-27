package wingchi.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.rpc.common.annotation.RpcService;
import demo.rpc.commonapi.dto.AccountVo;
import demo.rpc.commonapi.service.AccountService;
import org.springframework.stereotype.Service;
import wingchi.account.component.AccountStatus;
import wingchi.account.entity.AccountDo;
import wingchi.account.mapper.AccountMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RpcService(value = AccountService.class, version = "1.0")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDo> implements AccountService {

    public AccountVo getAccount(Long accountId) {
        AccountDo account = this.getById(accountId);
        return fromDo(account);
    }

    @Override
    public void createAccount(AccountVo accountVo) {
        AccountDo accountDo = new AccountDo();
        accountDo.setNumber(accountVo.getNumber());
        accountDo.setFullName(accountVo.getFullName());
        accountDo.setBalance(accountVo.getBalance());
        accountDo.setEmail(accountVo.getEmail());
        accountDo.setPassword(accountVo.getPassword());
        accountDo.setStatus(AccountStatus.INUSE);
        accountDo.setPhone(accountVo.getPhone());
        this.save(accountDo);
    }

    @Override
    public void updateAccount(AccountVo accountVo) {
        AccountDo Do = this.getById(accountVo.getAccountId());
        Do.setFullName(accountVo.getFullName());
        Do.setPhone(accountVo.getPhone());
        this.updateById(Do);
    }

    @Override
    public void deleteAccount(Long accountId) {
        AccountDo account = this.getById(accountId);
        account.setStatus(AccountStatus.CLOSE);
        this.updateById(account);
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


    public List<AccountVo> getAccountList() {
        LambdaQueryWrapper<AccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDo::getStatus, AccountStatus.INUSE.name());
        List<AccountDo> accountDos = baseMapper.selectList(queryWrapper);
        return accountDos.stream().map(AccountServiceImpl::fromDo).collect(Collectors.toList());
    }

    public static AccountVo fromDo(AccountDo account) {
        AccountVo accountVo = new AccountVo();
        accountVo.setAccountId(account.getAccountId());
        accountVo.setFullName(account.getFullName());
        accountVo.setBalance(account.getBalance());
        accountVo.setEmail(account.getEmail());
        accountVo.setNumber(account.getNumber());
        accountVo.setPassword(account.getPassword());
        accountVo.setIsAdmin(Boolean.TRUE);
        accountVo.setPhone(account.getPhone());
        accountVo.setTransactions(new ArrayList<>()); ;
        accountVo.setAmount(BigDecimal.ZERO);
        return accountVo;
    }

}
