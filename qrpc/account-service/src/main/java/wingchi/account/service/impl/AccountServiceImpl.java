package wingchi.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.rpc.common.annotation.RpcAutowire;
import demo.rpc.common.annotation.RpcService;
import demo.rpc.commonapi.dto.AccountVo;
import demo.rpc.commonapi.dto.TransactionDto;
import demo.rpc.commonapi.dto.TransactionType;
import demo.rpc.commonapi.service.AccountService;
import demo.rpc.commonapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wingchi.account.component.AccountStatus;
import wingchi.account.entity.AccountDo;
import wingchi.account.entity.UserDo;
import wingchi.account.mapper.AccountMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RpcService(value = AccountService.class, version = "1.0")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDo> implements AccountService {

    @RpcAutowire(version="1.0")
    private TransactionService transactionService;

    @Autowired
    private UserServiceImpl userService;


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
        accountDo.setUserId(accountVo.getUserId());
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

    @Override
    public void deposit(AccountVo accountVo) {
        addBalance(accountVo.getAccountId(),accountVo.getAmount());
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setFromAccountId(accountVo.getAccountId());
        transactionDto.setAmount(accountVo.getAmount());
        transactionDto.setTransactionType(TransactionType.DEPOSIT);
        transactionDto.setFromAccountName(accountVo.getFullName());
        transactionService.createTransaction(transactionDto);
    }



    @Transactional
    public void deductBalance(Long accountId, BigDecimal amount) {
        AccountDo account = this.getById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        this.updateById(account);
    }

    @Transactional
    public void addBalance(Long accountId, BigDecimal amount) {
        AccountDo account = this.getById(accountId);
        if(account==null){
            throw new RuntimeException("客户不存在！");
        }
        account.setBalance(account.getBalance().add(amount));
        this.updateById(account);
    }


    @Override
    public void withdraw(AccountVo accountVo) {
        deductBalance(accountVo.getAccountId(),accountVo.getAmount());
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setToAccountId(accountVo.getToAccountId());
        transactionDto.setAmount(accountVo.getAmount());
        transactionDto.setTransactionType(TransactionType.WITHDRAW);
        transactionService.createTransaction(transactionDto);
    }



    public List<AccountVo> getAccountList(Long userId) {
        UserDo userDo = null;
        if(userId != null) {
            userDo =userService.getById(userId);
        }
        LambdaQueryWrapper<AccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDo::getStatus, AccountStatus.INUSE.name());
        if(userDo != null) {
            queryWrapper.eq(AccountDo::getUserId, userId);
        }
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
        accountVo.setIsAdmin(Boolean.FALSE);
        accountVo.setPhone(account.getPhone());
        accountVo.setTransactions(new ArrayList<>()); ;
        accountVo.setAmount(BigDecimal.ZERO);
        return accountVo;
    }

}
