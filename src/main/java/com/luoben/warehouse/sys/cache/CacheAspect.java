package com.luoben.warehouse.sys.cache;

import com.luoben.warehouse.sys.domain.Dept;
import com.luoben.warehouse.sys.domain.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class CacheAspect {

    private final Logger logger=LoggerFactory.getLogger(this.getClass());

    //声明缓存容器
    private Map<String, Object> CACHE_CONTAINER = new HashMap<>();

    /*================================部门切入开始====================================*/

    private static final String CACHE_DEPT_PROFIX="dept:";

    //定义部门切入点
    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.getById(..))")
    public void pointcutDeptGet() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.save(..))")
    public void pointcutDeptSave() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.updateById(..))")
    public void pointcutDeptUpdateById() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.removeById(..))")
    public void pointcutDeptRemove() {
    }



    /**
     * 添加部门切入
     * @param joinPoint
     * @return
     */
    @Around("pointcutDeptSave()")
    public Object cacheDeptAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------部门新增切入");
        //取出第一个参数
        Dept object = (Dept) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res){
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX + object.getId(),object);
        }
        return res;
    }

    /**
     * 查询切入
     */
    @Around("pointcutDeptGet()")
    public Object cacheDeptGet(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------部门查询切入");
        //取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        //从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_DEPT_PROFIX + object);
        if (res1!=null){
            logger.info("已从缓存里面找到部门对象"+CACHE_DEPT_PROFIX + object);
            return res1;
        }else {
            logger.info("未从缓存里面找到部门对象，从数据库中查询并放入缓存");
            Dept res2 =(Dept) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX+res2.getId(),res2);
            return res2;
        }
    }

    /**
     * 修改切入
     */
    @Around("pointcutDeptUpdateById()")
    public Object cacheDeptUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------部门修改切入");
        //取出第一个参数
        Dept deptVo = (Dept) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess){
            //查询缓存里有没有
            Dept dept =(Dept) CACHE_CONTAINER.get(CACHE_DEPT_PROFIX + deptVo.getId());
            if (null==dept){
                dept=new Dept();
            }
            BeanUtils.copyProperties(deptVo,dept);
            logger.info("部门对象缓存已更新"+CACHE_DEPT_PROFIX + deptVo.getId());
            CACHE_CONTAINER.put(CACHE_DEPT_PROFIX+dept.getId(),dept);
        }
        return isSuccess;
    }


    /**
     * 删除部门切入
     * @param joinPoint
     * @return
     */
    @Around("pointcutDeptRemove()")
    public Object cacheDeptDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------删除部门切入");
        //取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess){
            //删除缓存
            CACHE_CONTAINER.remove(CACHE_DEPT_PROFIX+id);
        }
        return isSuccess;
    }

    /*================================部门切入结束====================================*/

    /*================================用户切入开始====================================*/

    private static final String CACHE_USER_PROFIX="user:";

    //定义用户切入点
    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.UserServiceImpl.getById(..))")
    public void pointcutUserGet() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.UserServiceImpl.save(..))")
    public void pointcutUserSave() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.UserServiceImpl.updateById(..))")
    public void pointcutUserUpdateById() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.UserServiceImpl.removeById(..))")
    public void pointcutUserRemove() {
    }


    /**
     * 添加用户切入
     * @param joinPoint
     * @return
     */
    @Around("pointcutUserSave()")
    public Object cacheUserAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------用户新增切入");
        //取出第一个参数
        User object = (User) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res){
            CACHE_CONTAINER.put(CACHE_USER_PROFIX + object.getId(),object);
        }
        return res;
    }

    /**
     * 查询用户切入
     */
    @Around("pointcutUserGet()")
    public Object cacheUserGet(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------用户查询切入");
        //取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        //从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_USER_PROFIX + object);
        if (res1!=null){
            logger.info("已从缓存里面找到用户对象"+CACHE_USER_PROFIX + object);
            return res1;
        }else {
            logger.info("未从缓存里面找到用户对象，从数据库中查询并放入缓存");
            User res2 =(User) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_USER_PROFIX+res2.getId(),res2);
            return res2;
        }
    }

    /**
     * 修改用户切入
     */
    @Around("pointcutUserUpdateById()")
    public Object cacheUserUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------用户修改切入");
        //取出第一个参数
        User userVo = (User) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess){
            //查询缓存里有没有
            User user =(User) CACHE_CONTAINER.get(CACHE_USER_PROFIX + userVo.getId());
            if (null==user){
                user=new User();
            }
            BeanUtils.copyProperties(userVo,user);
            logger.info("用户对象缓存已更新"+CACHE_USER_PROFIX + userVo.getId());
            CACHE_CONTAINER.put(CACHE_USER_PROFIX+user.getId(),user);
        }
        return isSuccess;
    }


    /**
     * 删除用户切入
     * @param joinPoint
     * @return
     */
    @Around("pointcutUserRemove()")
    public Object cacheUserDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("CacheAspect--------删除用户切入");
        //取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess){
            //删除缓存
            CACHE_CONTAINER.remove(CACHE_USER_PROFIX+id);
        }
        return isSuccess;
    }




}
