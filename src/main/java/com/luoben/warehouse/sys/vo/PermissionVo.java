package com.luoben.warehouse.sys.vo;

import com.luoben.warehouse.sys.domain.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * value object 值对象 / view object 表现层对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionVo extends Permission {
    private static final long serialVersionUID = 1L;

    private Integer page = 1;
    private Integer limit = 10;

}