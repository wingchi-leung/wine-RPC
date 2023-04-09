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

    private Long userId;

    private String fullName;

    private BigDecimal balance;

    private AccountStatus status;

    private String phone;

    private String email;

    private BigDecimal number;

    private String password;

    private LocalDateTime addTime;

    private LocalDateTime updateTime;



}
