package com.luoben.warehouse.sys.mapper;

import com.luoben.warehouse.sys.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoben
 * @since 2020-03-22
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色id删除 sys_role_permission
     * @param id
     */
    void deleteRolePermissionByRid(Serializable id);

    /**
     * 根据角色id删除 sys_role_user
     * @param id
     */
    void deleteRoleUseByRid(Serializable id);

    /**
     * 根据角色ID查询当前角色拥有的权限或菜单Id
     * @param roleId
     * @return
     */
    List<Integer> queryRolePermissionIdsByRid(Long roleId);

    /**
     * 保存角色和菜单权限之间的关系
     * @param rid
     * @param pid
     */
    void saveRolePermission(@Param("rid") Integer rid, @Param("pid") Integer pid);
}
