package com.luoben.warehouse.bus.cache;

import com.luoben.warehouse.bus.domain.Customer;
import com.luoben.warehouse.bus.domain.Goods;
import com.luoben.warehouse.bus.domain.Provider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
public class BusinessCacheAspect {

    /**
     * 日志出处
     */
    private final Logger logger=LoggerFactory.getLogger(this.getClass());

    // 声明一个缓存容器
    private static Map<String, Object> CACHE_CONTAINER = new HashMap<>();

    public static Map<String, Object> getCACHE_CONTAINER() {
        return CACHE_CONTAINER;
    }

    // 声明切面表达式
    private static final String CACHE_CUSTOMER_PROFIX = "customer:";

    //定义客户管理 切入点
    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.CustomerServiceImpl.getById(..))")
    public void pointcutCustomerGet() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.CustomerServiceImpl.save(..))")
    public void pointcutCustomerSave() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.CustomerServiceImpl.updateById(..))")
    public void pointcutCustomerUpdateById() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.CustomerServiceImpl.removeById(..))")
    public void pointcutCustomerRemove() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.CustomerServiceImpl.removeByIds(..))")
    public void pointcutCustomerRemoveByIds() {
    }


    /**
     * 客户添加切入
     *
     * @throws Throwable
     */
    @Around("pointcutCustomerSave()")
    public Object cacheCustomerAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Customer object = (Customer) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + object.getId(), object);
        }
        return res;
    }

    /**
     * 查询切入
     *
     * @throws Throwable
     */
    @Around("pointcutCustomerGet()")
    public Object cacheCustomerGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_CUSTOMER_PROFIX + object);
        if (res1 != null) {
            logger.info("已从缓存里面找到客户对象" + CACHE_CUSTOMER_PROFIX + object);
            return res1;
        } else {
            Customer res2 = (Customer) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + res2.getId(), res2);
            logger.info("未从缓存里面找到客户对象，去数据库查询并放到缓存" + CACHE_CUSTOMER_PROFIX + res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入
     *
     * @throws Throwable
     */
    @Around("pointcutCustomerUpdateById()")
    public Object cacheCustomerUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Customer deptVo = (Customer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            Customer dept = (Customer) CACHE_CONTAINER.get(CACHE_CUSTOMER_PROFIX + deptVo.getId());
            if (null == dept) {
                dept = new Customer();
            }
            BeanUtils.copyProperties(deptVo, dept);
            logger.info("客户对象缓存已更新" + CACHE_CUSTOMER_PROFIX + deptVo.getId());
            CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + dept.getId(), dept);
        }
        return isSuccess;
    }

    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around("pointcutCustomerRemove()")
    public Object cacheCustomerDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_CUSTOMER_PROFIX + id);
            logger.info("客户对象缓存已删除" + CACHE_CUSTOMER_PROFIX + id);
        }
        return isSuccess;
    }

    /**
     * 批量删除切入
     *
     * @throws Throwable
     */
    @Around("pointcutCustomerRemoveByIds()")
    public Object cacheCustomerBatchDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        @SuppressWarnings("unchecked")
        Collection<Serializable> idList = (Collection<Serializable>) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            for (Serializable id : idList) {
                // 删除缓存
                CACHE_CONTAINER.remove(CACHE_CUSTOMER_PROFIX + id);
                logger.info("客户对象缓存已删除" + CACHE_CUSTOMER_PROFIX + id);
            }
        }
        return isSuccess;
    }

    // 声明切面表达式

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.ProviderServiceImpl.getById(..))")
    public void pointcutProviderGet() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.ProviderServiceImpl.save(..))")
    public void pointcutProviderSave() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.ProviderServiceImpl.updateById(..))")
    public void pointcutProviderUpdateById() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.ProviderServiceImpl.removeById(..))")
    public void pointcutProviderRemove() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.ProviderServiceImpl.removeByIds(..))")
    public void pointcutProviderRemoveByIds() {
    }

    private static final String CACHE_PROVIDER_PROFIX = "provider:";


    /**
     * 供应商添加切入
     *
     * @throws Throwable
     */
    @Around("pointcutProviderSave()")
    public Object cacheProviderAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Provider object = (Provider) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + object.getId(), object);
        }
        return res;
    }

    /**
     * 查询切入
     *
     * @throws Throwable
     */
    @Around("pointcutProviderGet()")
    public Object cacheProviderGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_PROVIDER_PROFIX + object);
        if (res1 != null) {
            logger.info("已从缓存里面找到供应商对象" + CACHE_PROVIDER_PROFIX + object);
            return res1;
        } else {
            Provider res2 = (Provider) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + res2.getId(), res2);
            logger.info("未从缓存里面找到供应商对象，去数据库查询并放到缓存" + CACHE_PROVIDER_PROFIX + res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入
     *
     * @throws Throwable
     */
    @Around("pointcutProviderUpdateById()")
    public Object cacheProviderUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Provider deptVo = (Provider) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            Provider dept = (Provider) CACHE_CONTAINER.get(CACHE_PROVIDER_PROFIX + deptVo.getId());
            if (null == dept) {
                dept = new Provider();
            }
            BeanUtils.copyProperties(deptVo, dept);
            logger.info("供应商对象缓存已更新" + CACHE_PROVIDER_PROFIX + deptVo.getId());
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + dept.getId(), dept);
        }
        return isSuccess;
    }

    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around("pointcutProviderRemove()")
    public Object cacheProviderDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_PROVIDER_PROFIX + id);
            logger.info("供应商对象缓存已删除" + CACHE_PROVIDER_PROFIX + id);
        }
        return isSuccess;
    }

    /**
     * 批量删除切入
     *
     * @throws Throwable
     */
    @Around("pointcutProviderRemoveByIds()")
    public Object cacheProviderBatchDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        @SuppressWarnings("unchecked")
        Collection<Serializable> idList = (Collection<Serializable>) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            for (Serializable id : idList) {
                // 删除缓存
                CACHE_CONTAINER.remove(CACHE_PROVIDER_PROFIX + id);
                logger.info("供应商对象缓存已删除" + CACHE_PROVIDER_PROFIX + id);
            }
        }
        return isSuccess;
    }

    //商品数据的缓存 声明切面表达式

    private static final String CACHE_GOODS_PROFIX = "goods:";

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.GoodsServiceImpl.getById(..))")
    public void pointcutGoodsGet() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.GoodsServiceImpl.save(..))")
    public void pointcutGoodsSave() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.GoodsServiceImpl.updateById(..))")
    public void pointcutGoodsUpdateById() {
    }

    @Pointcut("execution(* com.luoben.warehouse.bus.service.impl.GoodsServiceImpl.removeById(..))")
    public void pointcutGoodsRemove() {
    }

    
    /**
     * 商品添加切入
     *
     * @throws Throwable
     */
    @Around("pointcutGoodsSave()")
    public Object cacheGoodsAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Goods object = (Goods) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + object.getId(), object);
        }
        return res;
    }

    /**
     * 查询切入
     *
     * @throws Throwable
     */
    @Around("pointcutGoodsGet()")
    public Object cacheGoodsGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_GOODS_PROFIX + object);
        if (res1 != null) {
            logger.info("已从缓存里面找到商品对象" + CACHE_GOODS_PROFIX + object);
            return res1;
        } else {
            Goods res2 = (Goods) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + res2.getId(), res2);
            logger.info("未从缓存里面找到商品对象，去数据库查询并放到缓存" + CACHE_GOODS_PROFIX + res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入
     *
     * @throws Throwable
     */
    @Around("pointcutGoodsUpdateById()")
    public Object cacheGoodsUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Goods deptVo = (Goods) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            Goods dept = (Goods) CACHE_CONTAINER.get(CACHE_GOODS_PROFIX + deptVo.getId());
            if (null == dept) {
                dept = new Goods();
            }
            BeanUtils.copyProperties(deptVo, dept);
            logger.info("商品对象缓存已更新" + CACHE_GOODS_PROFIX + deptVo.getId());
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + dept.getId(), dept);
        }
        return isSuccess;
    }

    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around("pointcutGoodsRemove()")
    public Object cacheGoodsDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_GOODS_PROFIX + id);
            logger.info("商品对象缓存已删除" + CACHE_GOODS_PROFIX + id);
        }
        return isSuccess;
    }

}
