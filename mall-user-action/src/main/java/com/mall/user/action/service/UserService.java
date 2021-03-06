package com.mall.user.action.service;

import com.mall.common.pojo.AdminUser;

import java.util.List;

public interface UserService {
    public int delectUser(int id);
    public int addUser(AdminUser adminUser);
    public String getCompanyId(String adminId);
    public int grantRole(int id,int rid);
    public int switchUser( int id,int status);
    public int updateUser(AdminUser adminUser);
}
