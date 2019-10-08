package com.wjj.mmall.controller.backend;

import com.wjj.mmall.common.Const;
import com.wjj.mmall.common.ServerResponse;
import com.wjj.mmall.pojo.User;
import com.wjj.mmall.service.IUserService;
import com.wjj.mmall.util.CookieUtil;
import com.wjj.mmall.util.JsonUtil;
import com.wjj.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpServletResponse httpServletResponse,HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            User user = response.getData();
            if(user.getRole()== Const.Role.ROLE_ADMIN){
                //说明是管理员
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

                return response;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }

}
