package com.wjj.mmall.controller.common;

import com.wjj.mmall.common.Const;
import com.wjj.mmall.pojo.User;
import com.wjj.mmall.util.CookieUtil;
import com.wjj.mmall.util.JsonUtil;
import com.wjj.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ComponentScan;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("*.do")
public class SessionExpireFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)){
            //判断logintoken是否为空或者"";
            //如果不为空的话，符合条件，继续拿user信息
            String userJsonStr = RedisPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr, User.class);
            if (user!=null){
                //如果user不会空，则重置session的时间，即调用expire命令
                RedisPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }

        super.doFilter(request, response, chain);
    }
}
