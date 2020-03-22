package com.luoben.warehouse.sys.cache;

import com.luoben.warehouse.sys.domain.Dept;
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
    private static final String CACHE_DEPT_PROFIX="dept:";

    //定义切入点
    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.getById(..))")
    public void pointcutGet() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.save(..))")
    public void pointcutSave() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.updateById(..))")
    public void pointcutUpdateById() {
    }

    @Pointcut("execution(* com.luoben.warehouse.sys.service.impl.DeptServiceImpl.removeById(..))")
    public void pointcutRemove() {
    }



    /**
     * 添加部门切入
     * @param joinPoint
     * @return
     */
    @Around("pointcutSave()")
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
    @Around("pointcutGet()")
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
    @Around("pointcutUpdateById()")
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
    @Around("pointcutRemove()")
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

}
