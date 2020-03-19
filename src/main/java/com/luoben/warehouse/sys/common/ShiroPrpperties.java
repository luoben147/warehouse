package com.luoben.warehouse.sys.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *  yml中配置的shiro 拦截规则
 */
@Component
@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroPrpperties {
    // 加密方式
    private String hashAlgorithmName = "md5";
    // 散列次数
    private int hashIterations = 2;
    // 默认的登陆页面
    private String loginUrl = "/index.html";
    //放行的路径
    private String[] anonUrls;
    //注销路径
    private String logOutUrl;
    //认证路径
    private String[] authcUlrs;
}
