package com.luoben.warehouse.sys.vo.act;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程定义转换类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ActProcessDefinitionEntityImpl {

    private String id;
    private String name;
    private String key;
    private Integer version;
    private String deploymentId;
    private String resourceName;
    private String diagramResourceName;

}
