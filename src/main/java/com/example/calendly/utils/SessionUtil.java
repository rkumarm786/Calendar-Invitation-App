package com.example.calendly.utils;

import com.example.calendly.dto.UserDto;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class SessionUtil {

    private static Gson gson = new Gson();

    public void initializeSession(HttpServletRequest request, String user) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(3600);
        session.setAttribute("LOGGED_IN_USER", user);
    }

    public boolean isLoggedIn(HttpServletRequest request){
        HttpSession session = request.getSession();
        return !StringUtils.isEmpty(session.getAttribute("LOGGED_IN_USER"));
    }

    public UserDto getLoggedIn(HttpServletRequest request){
        HttpSession session = request.getSession();
        return gson.fromJson(session.getAttribute("LOGGED_IN_USER").toString(),UserDto.class);
    }

    public boolean removeSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (!session.isNew()) {
            session.invalidate();
            return true;
        } else {
            return false;
        }
    }
}
