package wingchi.transfer.dto;

import lombok.Data;

@Data
public class TransactionQuery {
    private String accountNo;
    private String transactionType;
    private String transactionStatus;
    private Long startTime;
    private Long endTime;
    private Integer page;
}
