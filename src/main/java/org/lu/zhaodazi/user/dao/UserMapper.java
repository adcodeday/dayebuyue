package org.lu.zhaodazi.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.lu.zhaodazi.user.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

@Mapper
public interface UserMapper {

    void save(User user);
    @Select("select id,email,openid,role,province,sex,wantProvince,wantSex,codeWord from user where openId=#{openId}")
    User loadUserByOpenId(String openId);
    @Select("select id,email,openid,role,province,sex,wantProvince,wantSex,codeWord from user where email=#{email}")
    User loadUserByEmail(String email);
    @Select("select id,email,openid,role,province,sex,wantProvince,wantSex,codeWord from user where id=#{uid}")
    User loadUserByUid(Long uid);
    @Update({
            "<script>",
            "UPDATE user",
            "SET",
            "<trim suffixOverrides=\",\">",
            "<if test='sex!= null'>sex = #{sex},</if>",
            "<if test='province!= null'>province = #{province},</if>",
            "<if test='wantProvince!= null'>wantProvince = #{wantProvince},</if>",
            "<if test='wantSex!= null'>wantSex = #{wantSex},</if>",
            "</trim>",
            "WHERE id = #{id}",
            "</script>"
    })
    void update(User user);
}
