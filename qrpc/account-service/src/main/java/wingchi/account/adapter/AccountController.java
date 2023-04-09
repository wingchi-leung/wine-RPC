package wingchi.account.adapter;

import demo.rpc.commonapi.dto.AccountVo;
import demo.rpc.commonapi.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/account")
    public AccountVo getAccount(@RequestParam("accountId") Long accountId) {
        return accountService.getAccount(accountId);
    }

    //create account
    @PostMapping("/addAccount")
    public void createAccount(@RequestBody AccountVo accountVo) {
        accountService.createAccount(accountVo);
    }

    @PostMapping("/updateAccount")
    public void updateAccount(@RequestBody AccountVo accountVo) {
        accountService.updateAccount(accountVo);
    }

    @DeleteMapping("/deleteAccount")
    @CrossOrigin
    public void deleteAccount(@RequestParam("accountId") Long accountId) {
        log.info("删除账户:{}", accountId);
        accountService.deleteAccount(accountId);
    }

    @GetMapping("/accountList")
    public List<AccountVo> getAccountList(@RequestParam(required = false) Long userId) {
        return accountService.getAccountList(userId);
    }



    @PostMapping("/deposit")
    public void deposit(@RequestBody AccountVo accountVo) {
        accountService.deposit(accountVo.getAccountId(), accountVo.getAmount());
    }


    @PostMapping("/withdraw")
    public void withdraw(@RequestBody AccountVo accountVo) {
        accountService.withdraw(accountVo.getAccountId(), accountVo.getAmount());
    }
}
