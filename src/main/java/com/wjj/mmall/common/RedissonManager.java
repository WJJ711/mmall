package com.wjj.mmall.common;


import com.wjj.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {
    private Config config=new Config();
    private Redisson redisson=null;

    public Redisson getRedisson() {
        return redisson;
    }

    private static String redis_1_Ip= PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis_1_Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis_2_Ip= PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis_2_Port=Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));


    @PostConstruct
    private void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis_1_Ip).append(":").append(redis_1_Port).toString());
            redisson = (Redisson) Redisson.create(config);
            log.info("初始化Redisson结束");
        } catch (Exception e) {
            log.error("redisson init error",e);
        }


    }

}
