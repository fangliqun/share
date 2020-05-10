package make.money.share.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import make.money.share.mapper.UserMapper;
import make.money.share.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    public void send(String msg){
        QueryWrapper<User> queryWrapper  = new QueryWrapper<>();
        List<User> listUser = userMapper.selectList(queryWrapper);
        for(User user : listUser){
            if(user.getMail()!=null){
                sendMail(msg,user.getMail());
            }
            if(user.getPhone()!=null){

            }
        }
    }

    private void sendMail(String msg,String to){
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom("337539699@qq.com");
        //邮件接收人
        message.setTo(to);
        //邮件主题
        message.setSubject("走向人生巅峰，迎娶白富美");
        //邮件内容
        message.setText(msg);
        //发送邮件
        mailSender.send(message);
    }

}
