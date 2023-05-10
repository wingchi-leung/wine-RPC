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
import wingchi.account.entity.RoleDo;
import wingchi.account.entity.UserDo;
import wingchi.account.mapper.AccountMapper;
import wingchi.account.mapper.UserMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RpcService(value = AccountService.class, version = "1.0")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDo> implements AccountService {

    @RpcAutowire(version = "1.0")
    private TransactionService transactionService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;


    public AccountVo getAccount(Long accountId) {
        AccountDo account = this.getById(accountId);
        return fromDo(account, false, null);
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
        AccountDo account = this.getById(accountVo.getAccountId());
        addBalance(accountVo.getAccountId(), accountVo.getAmount());
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setToAccountId(accountVo.getAccountId());
        transactionDto.setCardNo(account.getNumber());
        transactionDto.setAmount(accountVo.getAmount());
        transactionDto.setTransactionType(TransactionType.DEPOSIT);
        transactionDto.setToAccountName(account.getFullName());
        transactionService.createTransaction(transactionDto);
    }


    @Transactional
    public void deductBalance(Long accountId, BigDecimal amount) throws Exception {
        AccountDo account = this.getById(accountId);
        BigDecimal subtract = account.getBalance().subtract(amount);
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("余额不足！");
        }
        account.setBalance(subtract);
        this.updateById(account);
    }

    @Transactional
    public void addBalance(Long accountId, BigDecimal amount) {
        AccountDo account = this.getById(accountId);
        if (account == null) {
            throw new RuntimeException("客户不存在！");
        }
        account.setBalance(account.getBalance().add(amount));
        this.updateById(account);
    }


    @Override
    public void withdraw(AccountVo accountVo) throws Exception {
        AccountDo account = this.getById(accountVo.getAccountId());
        deductBalance(accountVo.getAccountId(), accountVo.getAmount());
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setFromAccountId(accountVo.getAccountId());
        transactionDto.setAmount(accountVo.getAmount());
        transactionDto.setCardNo(account.getNumber());
        transactionDto.setFromAccountName(account.getFullName());
        transactionDto.setTransactionType(TransactionType.WITHDRAW);
        transactionService.createTransaction(transactionDto);
    }

    @Override
    public List<AccountVo> login(AccountVo accountVo) {
        UserDo userDo = userService.getByPhoneAndPassord(accountVo.getPhone(), accountVo.getPassword());
        if (userDo == null) {
            throw new RuntimeException("用户电话或密码错误！");
        }
        return getAccountList(userDo.getId());
    }


    public List<AccountVo> getAccountList(Long userId) {
        LambdaQueryWrapper<AccountDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDo::getStatus, AccountStatus.INUSE.name());
        if (userId != null) {
            queryWrapper.eq(AccountDo::getUserId, userId);
        }
        List<AccountDo> accountDos = baseMapper.selectList(queryWrapper);
        if (accountDos.isEmpty()) {
            return new ArrayList<>();
        }
        AccountDo accountDo = accountDos.get(0);
        RoleDo roleDo = userMapper.selectRoleByUserId(accountDo.getUserId());
        UserDo userDo = userMapper.selectById(accountDo.getUserId());
        Boolean isAdmin = false;
        if (roleDo != null && roleDo.getName().equals("admin")) {
            isAdmin = Boolean.TRUE;
        }
        final Boolean isAdminfinal = isAdmin;
        return accountDos.stream().map(x -> fromDo(x, isAdminfinal, userDo.getId())).collect(Collectors.toList());
    }

    public static AccountVo fromDo(AccountDo account, Boolean isAdmin, Long userId) {
        AccountVo accountVo = new AccountVo();
        accountVo.setAccountId(account.getAccountId());
        accountVo.setFullName(account.getFullName());
        accountVo.setBalance(account.getBalance());
        accountVo.setEmail(account.getEmail());
        accountVo.setNumber(account.getNumber());
        accountVo.setPassword(account.getPassword());
        accountVo.setIsAdmin(isAdmin);
        accountVo.setUserId(userId);
        accountVo.setPhone(account.getPhone());
        accountVo.setTransactions(new ArrayList<>());

        accountVo.setAmount(BigDecimal.ZERO);
        return accountVo;
    }

}
