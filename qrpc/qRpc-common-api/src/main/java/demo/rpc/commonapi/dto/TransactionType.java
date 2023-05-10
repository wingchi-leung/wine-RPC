package demo.rpc.commonapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum TransactionType implements java.io.Serializable{
    TRANSFER("转账"),
    WITHDRAW("提现"),
    DEPOSIT("存款");


    private String name;

    public BigDecimal formatAmount(BigDecimal amount){
        if (this == WITHDRAW || this==TRANSFER){
            return amount.negate();
        }
        return amount;
    }
}
