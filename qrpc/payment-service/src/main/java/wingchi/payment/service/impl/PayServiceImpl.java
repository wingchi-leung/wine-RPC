package wingchi.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.rpc.common.annotation.RpcAutowire;
import demo.rpc.commonapi.dto.TransactionDto;
import demo.rpc.commonapi.dto.TransactionType;
import demo.rpc.commonapi.service.AccountService;
import demo.rpc.commonapi.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wingchi.payment.dto.PaymentDto;
import wingchi.payment.entity.PaymentDo;
import wingchi.payment.mapper.PayMapper;
import wingchi.payment.service.PayService;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

@Service
public class PayServiceImpl extends ServiceImpl<PayMapper, PaymentDo> implements PayService {


    @RpcAutowire(version="1.0")
    private AccountService accountService;

    @RpcAutowire(version = "1.0")
    private TransactionService transactionService;

    @Override
    @Transactional
    public void pay(PaymentDto paymentDto) {
        try{
            if(paymentDto.getAmount().equals(BigDecimal.ZERO)){
                return;
            }
            accountService.addBalance(paymentDto.getPayeeId(), paymentDto.getAmount());
            accountService.deductBalance(paymentDto.getPayerId(), paymentDto.getAmount());
            PaymentDo paymentDo = paymentDto.toDo();
            saveTransaction(paymentDto);
            save(paymentDo);
        }catch (Exception e){
            if(e instanceof InvocationTargetException){
                String message = ((InvocationTargetException) e).getTargetException().getMessage();
                throw new RuntimeException(message,e);
            }
            else {
                throw new RuntimeException(e);
            }

        }
    }

    private void saveTransaction(PaymentDto paymentDto) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransactionType(TransactionType.TRANSFER);
        transactionDto.setFromAccountId(paymentDto.getPayerId());
        transactionDto.setToAccountId(paymentDto.getPayeeId());
        transactionDto.setAmount(paymentDto.getAmount());
        transactionDto.setFromAccountName(paymentDto.getPayerName());
        transactionDto.setToAccountName(paymentDto.getPayeeName());
        //资金流出的卡号
        transactionDto.setCardNo(paymentDto.getPayerNo());
        transactionService.createTransaction(transactionDto);
    }
}
