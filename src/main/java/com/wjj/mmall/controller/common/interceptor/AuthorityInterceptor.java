package com.wjj.mmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.wjj.mmall.common.Const;
import com.wjj.mmall.common.ServerResponse;
import com.wjj.mmall.pojo.User;
import com.wjj.mmall.util.CookieUtil;
import com.wjj.mmall.util.JsonUtil;
import com.wjj.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");
        //请求Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) o;

        //解析HandlerMethod

        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数,具体的参数key以及value是什么，我们打印日志
        StringBuilder requestParamBuffer = new StringBuilder();
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Iterator<String> it = parameterMap.keySet().iterator();
        while (it.hasNext()){
            String mapKey=it.next();
            String[] strs = parameterMap.get(mapKey);
            String mapValue= Arrays.toString(strs);
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        if (StringUtils.equals(className,"UserManageController")&&StringUtils.equals(methodName,"login")){
            //如果拦截到的是登录请求，不打印参数，因为参数里面有密码，防止日志泄露
            //如果是登录请求则放行，防止登录循环拦截
            log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            return true;
        }
        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,requestParamBuffer.toString());
        User user=null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user  = JsonUtil.string2Obj(userJsonStr, User.class);
        }
        if (user==null||user.getRole().intValue()!= Const.Role.ROLE_ADMIN){
            //返回flase，即不会调用controller里的方法
            //这里要reset，否则报异常，getWriter(),has already been called for this response
            httpServletResponse.reset();
            //这里要设置编码，否则会乱码
            httpServletResponse.setCharacterEncoding("UTF-8");
            //这里要设置返回值的类型，因为全部是json接口
            httpServletResponse.setContentType("application/json;charset=utf-8");

            //要给前端返回json
            PrintWriter out = httpServletResponse.getWriter();
            if (user==null){
                if (StringUtils.equals(className,"ProductManageController")&&StringUtils.equals(methodName,"richtextImgUpload")){
                    HashMap<Object, Object> resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {

                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }
            }else {
                if (StringUtils.equals(className,"ProductManageController")&&StringUtils.equals(methodName,"richtextImgUpload")){
                    HashMap<Object, Object> resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                }
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户无权限操作")));
            }

            out.flush();
            out.close();
            return false;
        }


        //只有返回值为true，才会进入对应的controller
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
