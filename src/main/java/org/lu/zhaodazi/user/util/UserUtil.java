package org.lu.zhaodazi.user.util;

import org.lu.zhaodazi.user.domain.entity.User;

public class UserUtil {
    public static User clearUserInfo(User user){
        User user1=new User();
        user1.setId(user.getId());
        return user1;
    }
}
