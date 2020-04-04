package com.example.calendly.interceptor;

import com.example.calendly.utils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SessionUtil sessionUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("login interceptor called call started at "+new Date().toString());
        if(!sessionUtil.isLoggedIn(request)){
            System.out.println("user not logged in");
            response.sendRedirect("/login");
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("login interceptor called completed at "+new Date().toString());
        super.postHandle(request, response, handler, modelAndView);
    }
}
