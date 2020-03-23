package com.luoben.warehouse.sys.mapper;

import com.luoben.warehouse.sys.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
@Repository
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
    List<Integer> queryRolePermissionIdsByRid(Integer roleId);

    /**
     * 保存角色和菜单权限之间的关系
     * @param rid
     * @param pid
     */
    void saveRolePermission(@Param("rid") Integer rid, @Param("pid") Integer pid);

    /**
     * 根据用户id删除用户角色中间表的数据
     * @param id
     */
    void deleteRoleUserByUid(@Param("id") Serializable id);

    /**
     * 查询当前用户拥有的角色ID集合
     * @param id
     * @return
     */
    List<Integer> queryUserRoleIdsByUid(@Param("id") Integer id);

    /**
     * 保存用户和角色的关系
     * @param uid
     * @param rid
     */
    void insertUserRole(@Param("uid") Integer uid,@Param("rid") Integer rid);
}
