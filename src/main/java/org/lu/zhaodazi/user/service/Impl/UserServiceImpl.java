package org.lu.zhaodazi.user.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.common.util.RedisUtil;
import org.lu.zhaodazi.user.dao.UserMapper;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public boolean save(User user) {
        //TODO 保存用户，并获取id
        userMapper.save(user);
        return true;
    }

    @Override
    public User loadUserByUserId(Long userId) throws UsernameNotFoundException {
        User user = RedisUtil.get("USER_" + userId,User.class);
        if(user==null){
            user = userMapper.loadUserByUid(userId);
            addCache(user);
        }
        return user;
    }

    @Override
    public User loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userMapper.loadUserByEmail(email);
        if(user==null){
            user=new User();
            user.setEmail(email);
            save(user);
            user=loadUserByUserId(user.getId());
        }
        addCache(user);
        return user;
    }

    @Override
    public User loadUserByOpenId(String openid) {
        User user=userMapper.loadUserByOpenId(openid);
        if(user==null){
            user=new User();
            user.setOpenId(openid);
            save(user);
            user=loadUserByUserId(user.getId());
        }
        addCache(user);
        return user;
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
        User user1 = userMapper.loadUserByUid(user.getId());
        addCache(user1);

    }

    public void addCache(User user){
        if(user!=null){
            log.info("存入缓存："+user.getId());
            RedisUtil.set("USER_"+user.getId(),user,3600);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
