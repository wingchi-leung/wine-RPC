package wingchi.account.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import wingchi.account.component.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@TableName("account")
@Data
public class AccountDo {
    @TableId(type = IdType.AUTO)
    private Long accountId;

    private String name;

    private BigDecimal balance;

    private AccountStatus status;

    private String phone;

    private LocalDateTime addTime;


    private LocalDateTime updateTime;



}
