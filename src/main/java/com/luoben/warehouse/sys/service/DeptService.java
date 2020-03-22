package com.luoben.warehouse.sys.service;

import com.luoben.warehouse.sys.domain.Dept;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luoben
 * @since 2020-03-20
 */
public interface DeptService extends IService<Dept> {

    Dept getLastOneDept();

}
