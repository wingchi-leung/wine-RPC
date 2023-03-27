package wingchi.transfer.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.rpc.common.annotation.RpcService;
import demo.rpc.commonapi.dto.TransactionVo;
import org.springframework.stereotype.Service;
import wingchi.transfer.dto.CreateDto;
import wingchi.transfer.entity.TransactionDo;
import wingchi.transfer.mapper.TransactionMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RpcService(value = TransactionService.class, version = "1.0")
public class TransactionServiceImpl  extends ServiceImpl<TransactionMapper, TransactionDo> implements TransactionService {
    @Override
    public List<TransactionVo> getTransactionList() {
        List<TransactionDo> list = list();
        return list.stream().map(TransactionServiceImpl::fromDo).collect(Collectors.toList());
    }

    @Override
    public void createTransaction(CreateDto createDto) {
        TransactionDo transactionDo = TransactionDo.fromCreateDto(createDto);
        save(transactionDo);
    }

    public static TransactionVo fromDo(TransactionDo transactionDo) {
        TransactionVo transactionVo = new TransactionVo();
        transactionVo.setFromAccountId(transactionDo.getFromAccountId());
        transactionVo.setToAccountId(transactionDo.getToAccountId());
//        transactionVo.setFromAccountName(transactionDo.getFromAccountName());
//        transactionVo.setToAccountName(transactionDo.getToAccountName());
        transactionVo.setAmount(transactionDo.getAmount());
        transactionVo.setCreateTime(transactionDo.getAddTime());
        return transactionVo;
    }

}
