package com.yuchao.community.mapper;

import com.sun.javafx.image.impl.IntArgb;
import com.yuchao.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 蒙宇潮
 * @create 2022-09-20  10:50
 */
@Mapper
public interface UserMapper {

    User selectById(Integer id);

    User selectByName(String username);

    User selectByEmail(String email);

    Integer insertUser(User user);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int updateAvatarUrl(@Param("id") Integer id, @Param("url") String url);

    int updatePassword(@Param("id") Integer id, @Param("password") String password);


}
