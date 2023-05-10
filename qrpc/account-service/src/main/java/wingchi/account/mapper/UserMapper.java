package wingchi.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import wingchi.account.entity.RoleDo;
import wingchi.account.entity.UserDo;

public interface UserMapper extends BaseMapper<UserDo> {
    public RoleDo selectRoleByUserId(@Param("userId") Long userId);
}
