package wingchi.account.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wingchi.account.dto.AccountVo;
import wingchi.account.service.AccountService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/account")
    public AccountVo getAccount(@RequestParam("accountId") Long accountId) {
        return accountService.getAccount(accountId);
    }


    public List<AccountVo> getAccountList() {
        return accountService.getAccountList();
    }
}
