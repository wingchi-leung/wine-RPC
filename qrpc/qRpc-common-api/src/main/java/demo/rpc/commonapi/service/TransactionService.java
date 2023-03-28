package demo.rpc.commonapi.service;

import demo.rpc.commonapi.dto.TransactionDto;
import demo.rpc.commonapi.dto.TransactionVo;

import java.util.List;

public interface TransactionService {
    List<TransactionVo> getTransactionList();

    void createTransaction(TransactionDto createDto);
}
