package demo.rpc.commonapi.service;

import demo.rpc.commonapi.dto.AccountVo;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
     void addBalance(Long accountId, BigDecimal amount);

     void deductBalance(Long accountId, BigDecimal amount) throws Exception;

    List<AccountVo> getAccountList(Long userId);

    AccountVo getAccount(Long accountId);

    void createAccount(AccountVo accountVo);

    void updateAccount(AccountVo accountVo);

    void deleteAccount(Long accountId);

    void deposit(AccountVo accountVo);

    void withdraw(AccountVo accountVo) throws Exception;

    List<AccountVo> login(AccountVo accountVo);
}
