package com.luoben.warehouse.sys.vo.act;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class ActTaskEntityImpl {

    private String id;
    private String name;
    private String assignee;
    private String processInstanceId;
    private String executionId;
    private String processDefinitionId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
}
