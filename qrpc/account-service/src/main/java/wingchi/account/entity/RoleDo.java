package wingchi.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role")
public class RoleDo {
    private Long id;
    private String name;
    private String description;
    private String addTime;
    private String updatedTime;
}
