package com.luoben.warehouse.sys.mapper;

import com.luoben.warehouse.sys.domain.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoben
 * @since 2020-03-19
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    Integer getMaxOrderNum();

    void deleteRolePermissionByPid(@Param("id") Serializable id);
}
