package com.luoben.warehouse.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.luoben.warehouse.sys.domain.Dept;
import com.luoben.warehouse.sys.mapper.DeptMapper;
import com.luoben.warehouse.sys.service.DeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoben
 * @since 2020-03-20
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public Dept getOne(Wrapper<Dept> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    public boolean update(Dept entity, Wrapper<Dept> updateWrapper) {
        return super.update(entity,updateWrapper);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public Dept getLastOneDept() {
        return deptMapper.getLastOneDept();
    }
}
