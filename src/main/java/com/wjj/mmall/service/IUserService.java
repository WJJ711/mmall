package com.wjj.mmall.service;

import com.wjj.mmall.common.ServerResponse;
import com.wjj.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuesion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse<User>  updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);
}
