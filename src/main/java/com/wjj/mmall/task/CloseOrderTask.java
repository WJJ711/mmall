package com.wjj.mmall.task;

import com.wjj.mmall.common.Const;
import com.wjj.mmall.service.IOrderService;
import com.wjj.mmall.util.PropertiesUtil;
import com.wjj.mmall.util.RedisShardedPoolUtil;
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
   // @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrderTaskV1(){
        log.info("关闭订单定时任务启动");
        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
      //  iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务关闭");
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrderTaskV2(){
        log.info("关闭订单定时任务启动");
        //从配置文件中获取过期时间，测试时用50s,上线时用5s
        long lockTimeout = Long.parseLong(
                PropertiesUtil.getProperty("lock.timeout", "5000"));
        //在redis里设置过期时间
        //如果空闲，则可以获取锁，如果已经有值，即锁被别的进程占用，则不执行
        //基于redis的setnx完成分布式锁
        Long setnx = RedisShardedPoolUtil.setnx(
                Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnx!=null&&setnx.intValue()==1){
            //如果返回值是1，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
        }else {
            log.info("没有获取分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
        }
        log.info("关闭订单定时任务关闭");
    }

    private void closeOrder(String lockName){
        //设置redis中有效期为5s，防止死锁，在测试中设为50s
        //如果不设置过期时间，如果占有锁的服务器挂了，则可能会死锁
        RedisShardedPoolUtil.expire(lockName,50);
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //关闭订单，在测试时，先注释掉
        //iOrderService.closeOrder(hour);
        //如果已经关闭了，就释放锁
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
        log.info("释放{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
        log.info("===============================");
    }



}
