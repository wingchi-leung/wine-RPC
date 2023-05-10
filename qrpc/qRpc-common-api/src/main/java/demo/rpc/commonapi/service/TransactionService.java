package demo.rpc.commonapi.service;

import demo.rpc.commonapi.dto.TransactionDto;

public interface TransactionService {
    void createTransaction(TransactionDto createDto);
}
