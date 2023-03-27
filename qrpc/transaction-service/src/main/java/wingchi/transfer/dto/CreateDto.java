package wingchi.transfer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDto {
    private BigDecimal amount;
    private Long fromAccountId;
    private Long toAccountId;
}
