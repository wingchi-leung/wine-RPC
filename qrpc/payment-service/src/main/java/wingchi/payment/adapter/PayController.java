package wingchi.payment.adapter;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wingchi.payment.dto.PaymentDto;
import wingchi.payment.service.PayService;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class PayController {


    private final PayService payService;

    @PostMapping("/pay")
    public void pay(@RequestBody  PaymentDto paymentDto){
        payService.pay(paymentDto);
    }
}
