package com.luoben.warehouse.sys.service.impl;

import com.luoben.warehouse.sys.domain.Permission;
import com.luoben.warehouse.sys.mapper.PermissionMapper;
import com.luoben.warehouse.sys.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoben
 * @since 2020-03-19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public Integer getMaxOrderNum() {
        return getBaseMapper().getMaxOrderNum();
    }

    @Override
    public boolean removeById(Serializable id) {
        PermissionMapper permissionMapper = this.getBaseMapper();
        //根据权限或菜单ID删除权限表各和角色的关系表里面的数据
        permissionMapper.deleteRolePermissionByPid(id);
        //删除 权限表的数据
        return super.removeById(id);
    }
}
