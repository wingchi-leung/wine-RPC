package wingchi.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class UserDo
{
    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String status;
    private String role;
    private String addTime;
    private String updatedTime;
}
