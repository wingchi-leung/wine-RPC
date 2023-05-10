package wingchi.transfer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import demo.rpc.common.annotation.RpcService;
import demo.rpc.commonapi.dto.TransactionDto;
import demo.rpc.commonapi.dto.TransactionVo;
import demo.rpc.commonapi.service.TransactionService;
import org.springframework.stereotype.Service;
import wingchi.transfer.dto.ResultData;
import wingchi.transfer.dto.TransactionQuery;
import wingchi.transfer.entity.TransactionDo;
import wingchi.transfer.mapper.TransactionMapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RpcService(value = TransactionService.class, version = "1.0")
public class TransactionServiceImpl  extends ServiceImpl<TransactionMapper, TransactionDo> implements TransactionService {

    public ResultData<List<TransactionVo>> getTransactionList(TransactionQuery query) {
        PageHelper.startPage(query.getPage(), 10);

        LambdaQueryWrapper<TransactionDo> wrapper=new LambdaQueryWrapper<>();
        List<TransactionDo> list = getBaseMapper().selectList(wrapper);
        PageInfo<TransactionDo> pageInfo = new PageInfo(list);

        return ResultData.success(pageInfo.getList().stream().map(x->fromDo(x)).collect(Collectors.toList()),
                pageInfo.getPages(), pageInfo.getTotal());
    }

    @Override
    public void createTransaction(TransactionDto createDto) {
        TransactionDo transactionDo = TransactionDo.fromCreateDto(createDto);
        save(transactionDo);
    }

    public static TransactionVo fromDo(TransactionDo transactionDo) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        TransactionVo transactionVo = new TransactionVo();
        transactionVo.setFromAccountId(transactionDo.getFromAccountId());
        transactionVo.setToAccountId(transactionDo.getToAccountId());
        transactionVo.setFromAccountName(transactionDo.getFromAccountName());
        transactionVo.setToAccountName(transactionDo.getToAccountName());
        transactionVo.setAmount(transactionDo.getTransactionType().formatAmount(transactionDo.getAmount()));
        transactionVo.setCreateTime(dtf2.format(transactionDo.getAddTime()));
        transactionVo.setType(transactionDo.getTransactionType().getName());
        transactionVo.setCardNo(transactionDo.getAccountNo().toString().substring(4));
        return transactionVo;
    }

}
