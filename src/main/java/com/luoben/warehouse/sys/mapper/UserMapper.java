package com.luoben.warehouse.sys.mapper;

import com.luoben.warehouse.sys.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoben-
 * @since 2020-03-18
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 加载最大的排序码
     * @return
     */
    Integer getMaxOrderNum();
}
