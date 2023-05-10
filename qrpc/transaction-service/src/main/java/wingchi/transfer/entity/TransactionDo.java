package wingchi.transfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import demo.rpc.commonapi.dto.TransactionDto;
import lombok.Data;
import demo.rpc.commonapi.dto.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("transaction")
public class TransactionDo {
    @TableId(type= IdType.AUTO)
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private String fromAccountName;
    private String toAccountName;
    private BigDecimal amount;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;
    private TransactionType transactionType;
    private BigDecimal accountNo;

    public static TransactionDo fromCreateDto(TransactionDto createDto){
        TransactionDo transactionDo = new TransactionDo();
        transactionDo.setAmount(createDto.getAmount());
        transactionDo.setFromAccountId(createDto.getFromAccountId());
        transactionDo.setToAccountId(createDto.getToAccountId());
        transactionDo.setTransactionType(createDto.getTransactionType());
        transactionDo.setFromAccountName(createDto.getFromAccountName());
        transactionDo.setToAccountName(createDto.getToAccountName());
        transactionDo.setAccountNo(createDto.getCardNo());
        return transactionDo;

    }
}
