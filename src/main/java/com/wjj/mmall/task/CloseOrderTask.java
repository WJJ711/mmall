package com.wjj.mmall.task;

import com.wjj.mmall.service.IOrderService;
import com.wjj.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 每隔一分钟检测，关闭2小时前的订单
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrderTaskV1(){
        log.info("关闭订单定时任务启动");
        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
      //  iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务关闭");
    }

}
