package wingchi.payment.dto;

import lombok.Getter;
import wingchi.payment.componet.PayChannel;
import wingchi.payment.entity.PaymentDo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentDto {
    private Long payeeId;

    private Long payerId;

    private String payeeName;

    private String payerName;

    private BigDecimal payeeNo;

    private BigDecimal payerNo;

    private BigDecimal amount;

    private PayChannel payChannel;

    public PaymentDo toDo() {
        return PaymentDo.builder()
                .amount(this.amount)
                .payeeId(payeeId)
                .payerId(payerId)
                .payChannel(payChannel)
                .payDate(LocalDateTime.now())
                .build();
    }
}
