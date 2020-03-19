package com.luoben.warehouse.sys.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.luoben.warehouse.sys.common.ShiroPrpperties;
import com.luoben.warehouse.sys.realm.UserRealm;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private static final Logger LOGGER= LoggerFactory.getLogger(ShiroConfig.class.getSimpleName());

    private static final String SHIRO_DIALECT = "shiroDialect";
    private static final String SHIRO_FILTER = "shiroFilter";

    //yml中配置的shiro 拦截规则
    @Autowired
    private ShiroPrpperties shiroPrpperties;

    /**
     * 声明凭证匹配器（加解密器）
     */
    @Bean("credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(shiroPrpperties.getHashAlgorithmName());//设置加密方式
        credentialsMatcher.setHashIterations(shiroPrpperties.getHashIterations());//设置散列次数
        return credentialsMatcher;
    }

    /**
     * 声明userRealm
     */
    @Bean("userRealm")
    public UserRealm userRealm(@Qualifier("credentialsMatcher") CredentialsMatcher credentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        // 注入凭证匹配器
        userRealm.setCredentialsMatcher(credentialsMatcher);
        return userRealm;
    }


    /**
     * 配置SecurityManager
     */
    @Bean("securityManager")
    public SecurityManager securityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 注入userRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 配置shiro的过滤器
     */
    @Bean(SHIRO_FILTER)
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager){
        //LOGGER.info("shiro配置："+shiroPrpperties);
        ShiroFilterFactoryBean factoryBean=new ShiroFilterFactoryBean();
        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);
        // 设置未登陆的时要跳转的页面
        //当项目访问其他没有通过认证的URL时，会默认跳转到/login，如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        factoryBean.setLoginUrl(shiroPrpperties.getLoginUrl());
        //登录成功后要跳转的链接
        //factoryBean.setSuccessUrl("/index");
        //当用户访问没有权限的URL时，跳转到未授权界面
        //factoryBean.setUnauthorizedUrl("/403");


        /**
         * anon :无需认证（登陆） 可以访问
         * authc：必须认证才可以访问
         * user: 如果使用 rememberMe 功能可以直接访问
         * perms: 该资源必须得到资源权限才可以访问
         * role: 该资源必须得到角色权限才可以访问
         */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 设置放行的路径
        if (shiroPrpperties.getAnonUrls() != null && shiroPrpperties.getAnonUrls().length > 0) {
            for (String anon : shiroPrpperties.getAnonUrls()) {
                filterChainDefinitionMap.put(anon, "anon");
            }
        }
        // 设置登出的路径
        if (null != shiroPrpperties.getLogOutUrl()) {
            filterChainDefinitionMap.put(shiroPrpperties.getLogOutUrl(), "logout");
        }
        // 设置拦截的路径
        if (shiroPrpperties.getAuthcUlrs() != null && shiroPrpperties.getAuthcUlrs().length > 0) {
            for (String authc : shiroPrpperties.getAuthcUlrs()) {
                filterChainDefinitionMap.put(authc, "authc");
            }
        }
        /* 这里是前后端分离的配置 可在自定义的ShiroLoginFilter 中返回json给前端
            Map<String, Filter> filters=new HashMap<>();
            //filters.put("authc", new ShiroLoginFilter());
            factoryBean.setFilters(filters);
        */
        //配置过滤器
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }



    /**
     * 配置ShiroDialect，用于thymeleaf和shiro标签配合使用，上面两个方法必须添加，不然会报错
     */
    @Bean(name = SHIRO_DIALECT)
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

}
