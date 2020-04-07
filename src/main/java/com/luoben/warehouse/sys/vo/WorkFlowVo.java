package com.luoben.warehouse.sys.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class WorkFlowVo{
	
	//批量删除使用
	private String[] ids;
	
	private Integer page;
	private Integer limit;

	//模型ID
	private String modelId="";
	//模型名称
	private String modelName="";

	//流程部署名称
	private String deploymentName;
	//流程部署ID
	private String deploymentId;

	 //流程定义key
	private String processDefinitionKey;

	//请假单ID
	private Integer id;
	//任务ID
	private String taskId;
	//连接名称
	private String outcome;
	//批注信息
	private String comment;
	

}
