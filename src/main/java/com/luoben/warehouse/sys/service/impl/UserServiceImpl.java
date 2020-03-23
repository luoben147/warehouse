package com.luoben.warehouse.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.mapper.RoleMapper;
import com.luoben.warehouse.sys.mapper.UserMapper;
import com.luoben.warehouse.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoben-
 * @since 2020-03-18
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

    @Override
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean removeById(Serializable id) {
        //根据用户id删除用户角色中间表的数据
        roleMapper.deleteRoleUserByUid(id);
        //删除用户头像[如果是默认头像不删除，否则删除]
        return super.removeById(id);
    }

    /**
     * 加载最大的排序码
     * @return
     */
    @Override
    public Integer getMaxOrderNum() {
        return getBaseMapper().getMaxOrderNum();
    }

    /**
     * 保存用户和角色的关系
     * @param uid
     * @param ids
     */
    @Override
    public void saveUserRole(Integer uid, Integer[] ids) {
        //1.根据用户ID删除sys_user_role里面的数据
        roleMapper.deleteRoleUserByUid(uid);
        if (null!=ids&&ids.length>0){
            for (Integer rid : ids) {
                roleMapper.insertUserRole(uid,rid);
            }
        }
    }
}
