package com.wjj.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.wjj.mmall.common.ResponseCode;
import com.wjj.mmall.common.ServerResponse;
import com.wjj.mmall.pojo.User;
import com.wjj.mmall.service.IOrderService;
import com.wjj.mmall.service.IUserService;
import com.wjj.mmall.util.CookieUtil;
import com.wjj.mmall.util.JsonUtil;
import com.wjj.mmall.util.RedisShardedPoolUtil;
import com.wjj.mmall.vo.OrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
//        String token = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(token)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(token);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //填充业务逻辑
//            return iOrderService.manageList(pageNum,pageSize);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }

        return iOrderService.manageList(pageNum,pageSize);
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderList(HttpServletRequest request, Long orderNo){
//        String token = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(token)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(token);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //填充业务逻辑
//            return iOrderService.manageDetail(orderNo);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }

        return iOrderService.manageDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpServletRequest request, Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
//        String token = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(token)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(token);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //填充业务逻辑
//            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }

        return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpServletRequest request, Long orderNo){
//        String token = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(token)){
//            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(token);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if (user==null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()){
//            //填充业务逻辑
//            return iOrderService.sendGoods(orderNo);
//        }else {
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }

        return iOrderService.sendGoods(orderNo);
    }
}
