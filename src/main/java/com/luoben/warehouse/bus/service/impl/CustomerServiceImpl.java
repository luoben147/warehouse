package com.luoben.warehouse.bus.service.impl;

import com.luoben.warehouse.bus.domain.Customer;
import com.luoben.warehouse.bus.mapper.CustomerMapper;
import com.luoben.warehouse.bus.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoben
 * @since 2020-03-23
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public boolean save(Customer entity) {
        return super.save(entity);
    }

    @Override
    public boolean updateById(Customer entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return super.removeByIds(idList);
    }
}
