package com.mall.user.action.dao;

import com.mall.common.pojo.AdminUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDAO {
    public int delectUser(int id);
    public int addUser(AdminUser adminUser);//添加用户
    public String getCompanyId(String adminId);//通过登录的id拿到公司id
    public int grantRole(@Param("id") int id,@Param("rid")int rid);
    public int switchUser(@Param("id") int id,@Param("status")int status);
    public int updateUser(AdminUser adminUser);
}
