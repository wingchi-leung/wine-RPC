package wingchi.account.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wingchi.account.entity.UserDo;
import wingchi.account.mapper.UserMapper;
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> {
    public UserDo getByPhoneAndPassord(String phone, String password) {
        return this.getOne(new LambdaQueryWrapper<UserDo>().eq(UserDo::getPhone, phone).eq(UserDo::getPassword, password));
    }
}
