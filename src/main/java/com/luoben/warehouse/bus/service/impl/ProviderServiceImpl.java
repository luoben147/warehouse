package com.luoben.warehouse.bus.service.impl;

import com.luoben.warehouse.bus.domain.Provider;
import com.luoben.warehouse.bus.mapper.ProviderMapper;
import com.luoben.warehouse.bus.service.ProviderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoben
 * @since 2020-03-24
 */
@Service
@Transactional
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements ProviderService {

    @Override
    public boolean save(Provider entity) {
        return super.save(entity);
    }

    @Override
    public Provider getById(Serializable id) {
        return super.getById(id);
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
