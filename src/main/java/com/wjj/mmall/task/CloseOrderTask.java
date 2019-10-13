package com.wjj.mmall.task;

import com.wjj.mmall.common.Const;
import com.wjj.mmall.common.RedissonManager;
import com.wjj.mmall.service.IOrderService;
import com.wjj.mmall.util.PropertiesUtil;
import com.wjj.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private RedissonManager redissonManager;

    //此时如果一个服务器在设置缓存过期时间之前挂了，则可能会造成死锁，因为他占有了锁，但是没释放就挂了
    //如果用tomcat的 shutdown则不存在这个问题，如果采用kill进程则gg
    @PreDestroy
    public void delLock(){
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
    }

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

   // @Scheduled(cron = "0 0/1 * * * ?")
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

    /**
     * 双重防死锁
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrderTaskV3(){
        log.info("关闭订单定时任务启动");
        //从配置文件中获取过期时间，测试时用50s,上线时用5s
        long lockTimeout = Long.parseLong(
                PropertiesUtil.getProperty("lock.timeout", "5000"));
        //在redis里设置过期时间
        //如果空闲，则可以获取锁，如果已经有值，即锁被别的进程占用，则不执行
        //基于redis的setnx完成分布式锁
        //setnx为原子性操作
        Long setnx = RedisShardedPoolUtil.setnx(
                Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnx!=null&&setnx.intValue()==1){
            //如果返回值是1，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
        }else {
            String lockValueA = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
            //如果lockValueA==null,则说明已经释放锁，不会产生死锁现象
            if (lockValueA!=null&&System.currentTimeMillis()>Long.parseLong(lockValueA)){
                String lockValueB = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,
                        String.valueOf(System.currentTimeMillis() + lockTimeout));
                //lockValueB==null说明锁已过期
                //lockValueA  equals lockValueB 说明这期间没有别的进程改动过，如果多进程同时getSet，则只有第一个getSet的集成能满足equal
                if (lockValueB==null|| StringUtils.equals(lockValueA,lockValueB)){
                    //获得锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
                }else {
                    //锁没过期，且有别的进程先getSet了
                    log.info("没有获取分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
                }
            }else {
                log.info("没有获取分布式锁：{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
            }
        }
        log.info("关闭订单定时任务关闭");
    }

    //@Scheduled(cron = "0 0/1 * * * ?")
    public void closeOrderTaskV4(){
        RLock lock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
        boolean getLock=false;
        try {
            //第一个waitTime 必须设置为0
            //第二个leaseTime
            if (getLock=lock.tryLock(0,50, TimeUnit.SECONDS)){
                log.info("Redisson获取到分布式锁:{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
                int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
                //iOrderService.closeOrder(hour);

            }else {
                log.info("Redisson没有获取到分布式锁:{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.info("Redisson没有获取到分布式锁：{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
        }finally {
            if (!getLock){
                return;
            }
            lock.unlock();
            log.info("Redisson分布式锁释放锁");
        }
    }

    private void closeOrder(String lockName){
        //设置redis中有效期为5s，防止死锁，在测试中设为50s
        //如果不设置过期时间，如果占有锁的服务器挂了，则可能会死锁
        //如果时间过短，业务流程还没结束，锁就失效了，则另一个进程则会抢到锁，造成并发修改问题
        //时间最好设置成，刚好完成任务的情况下再多一点
        RedisShardedPoolUtil.expire(lockName,50);
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //关闭订单，在测试时，先注释掉
        iOrderService.closeOrder(hour);
        //如果已经关闭了，就释放锁
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK);
        log.info("释放{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDED_TASK_LOCK,Thread.currentThread().getName());
        log.info("===============================");
    }



}
