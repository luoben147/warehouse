package com.luoben.warehouse.sys.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luoben.warehouse.sys.common.ActiverUser;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Realm : shiro连接数据的桥梁
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname",token.getPrincipal().toString());
        User user = userService.getOne(queryWrapper);
        if(null!=user){
            ActiverUser activerUser=new ActiverUser();
            activerUser.setUser(user);
            //生成盐
            ByteSource credentialsSalt=ByteSource.Util.bytes(user.getSalt());
            /**
             * 参数1： 用户对象。 里面可以存放任意对象，
             *      这个对象存入后如果登陆成功，会调用doGetAuthorizationInfo授权方法去授权
             *      在授权方法中principals.getPrimaryPrincipal() 可以获取它
             * 参数2：凭证（加密后的密码）
             * 参数3：混淆字符串（盐）
             * 参数4：realmName
             */
            SimpleAuthenticationInfo info=new SimpleAuthenticationInfo(activerUser,user.getPwd(),credentialsSalt,this.getName());
            return info;
        }
        return null;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        ActiverUser activerUser = (ActiverUser) principals.getPrimaryPrincipal();
//        User user = activerUser.getUser();
//        // *:* 通配  表示所有资源都可访问
//        List<String> superPermission = new ArrayList<>();
//        superPermission.add("*:*");
//        List<String> permissions = activerUser.getPermissions();
//        if (user.getType().equals(Constast.USER_TYPE_SUPER)){   //超级管理员
//            authorizationInfo.addStringPermissions(superPermission);
//        }else {
//            if (null!=permissions&&permissions.size()>0){
//                authorizationInfo.addStringPermissions(permissions);
//            }
//        }
//        return authorizationInfo;
        return null;
    }


}
