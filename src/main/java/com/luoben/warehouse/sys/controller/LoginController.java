package com.luoben.warehouse.sys.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.luoben.warehouse.sys.common.ActiverUser;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.utils.WebUtils;
import com.luoben.warehouse.sys.domain.Loginfo;
import com.luoben.warehouse.sys.service.LoginfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * 登陆前端控制器
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginfoService loginfoService;

    @RequestMapping("/login")
    public ResultObj login(String loginname, String pwd,String code,HttpSession session){
        Object codeSession = session.getAttribute("code");
        if(code==null || !code.equals(codeSession)){
            return ResultObj.LOGIN_ERROR_CODE;
        }

        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token=new UsernamePasswordToken(loginname,pwd);
        try {
            subject.login(token);
            ActiverUser activerUser= (ActiverUser) subject.getPrincipal();
            WebUtils.getSession().setAttribute("user",activerUser.getUser());

            //记录登陆日志
            Loginfo loginfo=new Loginfo();
            loginfo.setLoginname(activerUser.getUser().getName()+"-"+activerUser.getUser().getLoginname());
            loginfo.setLoginip(WebUtils.getRequest().getRemoteAddr());
            loginfo.setLogintime(new Date());
            loginfoService.save(loginfo);

            return ResultObj.LOGIN_SUCCESS;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return ResultObj.LOGIN_ERROR_PASS;
        }

    }

    @RequestMapping("/getCode")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(116, 36, 4, 5);
        //得到code
        String code = captcha.getCode();
        System.out.println("验证码=" +code);
        //放入session
        session.setAttribute("code",code);
        //输出浏览器
        ServletOutputStream out = response.getOutputStream();
        captcha.write(out);
        out.close();
    }

    /**
     * 退出
     */
    @RequestMapping("logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:login.html";
    }

}
