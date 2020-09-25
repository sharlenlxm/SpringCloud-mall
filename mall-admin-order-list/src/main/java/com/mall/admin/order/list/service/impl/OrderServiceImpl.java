package com.mall.admin.order.list.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.admin.order.list.dao.OrderDAO;
import com.mall.admin.order.list.service.OrderService;
import com.mall.common.pojo.Address;
import com.mall.common.pojo.MemberUser;
import com.mall.common.pojo.Order;
import com.mall.common.pojo.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * OrderServiceImpl
 *
 * @Author BessCroft
 * @Date 2020/9/24 17:39
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDAO orderDAO;

    @Override
    public String getCompanyId(int id) {
        return orderDAO.getCompanyId(id);
    }

    @Override
    public PageInfo listOrderByCompanyId(int pageNum, int pageSize, String companyId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderDAO.listOrderByCompanyId(companyId);
        PageInfo pageInfo = new PageInfo(orders);
        return pageInfo;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRED)
    public boolean deleteOrderById(String orderId) {
        return orderDAO.deleteOrderById(orderId) > 0;
    }

    @Override
    public Address getAddressByAddressId(String addressId) {
        return orderDAO.getAddressByAddressId(addressId);
    }

    @Override
    public MemberUser getMemberUserByMemberId(String memberId) {
        return orderDAO.getMemberUserByMemberId(memberId);
    }
}
