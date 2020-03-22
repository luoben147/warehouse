package com.luoben.warehouse.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luoben.warehouse.sys.domain.Dept;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author luoben
 * @since 2020-03-20
 */
@Repository
public interface DeptMapper extends BaseMapper<Dept> {

    Dept getLastOneDept();
}
