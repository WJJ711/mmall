package com.wjj.mmall.service;

import com.github.pagehelper.PageInfo;
import com.wjj.mmall.common.ServerResponse;
import com.wjj.mmall.pojo.Order;
import com.wjj.mmall.pojo.OrderItem;
import com.wjj.mmall.vo.OrderVo;

import java.util.List;
import java.util.Map;

public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse<String> cancel(Integer userId, Long orderNo);


    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> sendGoods(Long orderNo);

    void closeOrder(int hour);
}
