package com.yuchao.community.mapper;

import com.yuchao.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 蒙宇潮
 * @create 2022-09-26  21:11
 */

@Deprecated
public interface LoginTicketMapper {

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values (#{userId,},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    int updateLoginTicket(@Param("ticket") String ticket, @Param("status") Integer status);
}
