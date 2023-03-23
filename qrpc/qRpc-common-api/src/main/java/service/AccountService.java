package service;

import java.math.BigDecimal;

public interface AccountService {
    void deductBalance(Long accountId, BigDecimal amount);

    void addBalance(Long accountId, BigDecimal amount);
}
