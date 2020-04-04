package com.example.calendly.controller;

import com.example.calendly.service.CalenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthController {

    @Autowired
    private CalenderService calenderService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public RedirectView login(HttpServletRequest request) throws Exception {
        return new RedirectView(calenderService.authorize());
    }

    @RequestMapping(value = "/login/google", method = RequestMethod.GET)
    public void loginCallback(@RequestParam(value = "code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        calenderService.loginCallback(request, code);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", "/home");
    }
}
