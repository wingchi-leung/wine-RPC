package wingchi.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import wingchi.payment.dto.PaymentDto;
import wingchi.payment.entity.PaymentDo;


public interface PayService extends IService<PaymentDo> {
    void pay(PaymentDto paymentDto);
}
