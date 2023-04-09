package wingchi.account.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wingchi.account.entity.UserDo;
import wingchi.account.mapper.UserMapper;
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> {
}
