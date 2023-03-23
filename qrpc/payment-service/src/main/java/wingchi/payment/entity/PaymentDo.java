package wingchi.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import wingchi.payment.componet.PayChannel;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@TableName("payment")
@Builder
public class PaymentDo {
    @TableId(type= IdType.AUTO)
    private Long id;

    @TableField("payer_id")
    private Long payerId;

    private Long payeeId;

    private BigDecimal amount;

    private LocalDateTime payDate;

    private PayChannel payChannel;
}
