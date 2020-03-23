package com.luoben.warehouse.sys.service;

import com.luoben.warehouse.sys.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luoben-
 * @since 2020-03-18
 */
public interface UserService extends IService<User> {

    /**
     * 加载最大的排序码
     * @return
     */
    Integer getMaxOrderNum();

    /**
     * 保存用户和角色的关系
     * @param uid
     * @param ids
     */
    void saveUserRole(Integer uid, Integer[] ids);
}
