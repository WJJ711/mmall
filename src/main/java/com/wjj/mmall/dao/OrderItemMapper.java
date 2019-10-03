package com.wjj.mmall.dao;

import com.wjj.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> getOrderNoUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    List<OrderItem> getOrderNo(@Param("orderNo") Long orderNo);

    //mybatis批量插入
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);


}
