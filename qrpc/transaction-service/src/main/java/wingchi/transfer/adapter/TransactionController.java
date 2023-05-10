package wingchi.transfer.adapter;

import demo.rpc.commonapi.dto.TransactionDto;
import demo.rpc.commonapi.dto.TransactionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wingchi.transfer.dto.ResultData;
import wingchi.transfer.dto.TransactionQuery;
import wingchi.transfer.service.TransactionServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionServiceImpl transactionService;

    @PostMapping("/")
    public ResultData<List<TransactionVo>> getTransactionList(@RequestBody TransactionQuery query){
        return transactionService.getTransactionList(query);
    }

    @PostMapping("/create")
    public void createTransfer(TransactionDto createDto){
         transactionService.createTransaction(createDto);
    }

}
