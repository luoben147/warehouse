package com.luoben.warehouse.sys.vo.act;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 流程部署转换类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ActDeploymentEntityImpl {

    private String id;
    private String name;
    private String category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date deploymentTime;
}
