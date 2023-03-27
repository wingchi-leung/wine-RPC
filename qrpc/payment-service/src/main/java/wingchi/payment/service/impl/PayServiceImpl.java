package wingchi.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.rpc.common.annotation.RpcAutowire;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import demo.rpc.commonapi.service.AccountService;
import wingchi.payment.dto.PaymentDto;
import wingchi.payment.entity.PaymentDo;
import wingchi.payment.mapper.PayMapper;
import wingchi.payment.service.PayService;

@Service
public class PayServiceImpl extends ServiceImpl<PayMapper, PaymentDo> implements PayService {


    @RpcAutowire(version="1.0")
    private AccountService accountService;


    @Override
    @Transactional
    public void pay(PaymentDto paymentDto) {
        accountService.addBalance(paymentDto.getPayeeId(), paymentDto.getAmount());
        accountService.deductBalance(paymentDto.getPayerId(), paymentDto.getAmount());
        PaymentDo paymentDo = paymentDto.toDo();
        save(paymentDo);
    }
}
