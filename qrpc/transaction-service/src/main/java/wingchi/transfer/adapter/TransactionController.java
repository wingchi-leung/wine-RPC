package wingchi.transfer.adapter;

import demo.rpc.commonapi.dto.TransactionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import wingchi.transfer.dto.CreateDto;
import wingchi.transfer.service.TransactionService;

import java.util.List;

@RestController
@CrossOrigin
public class TransactionController {


    @Autowired
    private TransactionService transactionService;

    public List<TransactionVo> getTransactionList() {
        return transactionService.getTransactionList();
    }

    public void createTransfer(CreateDto createDto){
         transactionService.createTransaction(createDto);
    }
}
