package wingchi.transfer.service;

import demo.rpc.commonapi.dto.TransactionVo;
import wingchi.transfer.dto.CreateDto;

import java.util.List;

public interface TransactionService {
    List<TransactionVo> getTransactionList();

    void createTransaction(CreateDto createDto);
}
