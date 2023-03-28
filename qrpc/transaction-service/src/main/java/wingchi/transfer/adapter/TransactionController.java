package wingchi.transfer.adapter;

import demo.rpc.commonapi.dto.TransactionDto;
import demo.rpc.commonapi.dto.TransactionVo;
import demo.rpc.commonapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public List<TransactionVo> getTransactionList() {
        return transactionService.getTransactionList();
    }

    @PostMapping("/")
    public void createTransfer(TransactionDto createDto){
         transactionService.createTransaction(createDto);
    }

}
