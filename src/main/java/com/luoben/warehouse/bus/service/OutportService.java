package com.luoben.warehouse.bus.service;

import com.luoben.warehouse.bus.domain.Outport;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luoben
 * @since 2020-03-24
 */
public interface OutportService extends IService<Outport> {

    void addOutPort(Integer id, Integer number, String remark);
}
