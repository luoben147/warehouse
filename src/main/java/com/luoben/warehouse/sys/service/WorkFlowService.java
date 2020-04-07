package com.luoben.warehouse.sys.service;

import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.domain.LeaveBill;
import com.luoben.warehouse.sys.vo.WorkFlowVo;

import java.io.InputStream;
import java.util.List;

public interface WorkFlowService {

    /**
     * 查询流程部署信息
     *
     * @param workFlowVo
     * @return
     */
    DataGridView queryProcessDeploy(WorkFlowVo workFlowVo);

    /**
     * 查询所有的流程定义
     * @param workFlowVo
     * @return
     */
    DataGridView queryAllProcessDefinition(WorkFlowVo workFlowVo);

    /**
     * 添加流程部署
     * @param inputStream
     * @param deploymentName
     */
    void addWorkFlow(InputStream inputStream, String deploymentName);


    /**
     * 删除流程部署
     * @param id
     */
    void deleteProcessDeployById(Integer id);

    /**
     * 根据流程部署ID查询流程图
     * @param deploymentId
     * @return
     */
    InputStream queryProcessDeploymentImage(String deploymentId);

    /**
     * 启动流程
     * @param leaveBillId
     */
    void startProcess(Integer leaveBillId);

    /**
     * 查询当前登录用户的待办任务
     * @param vo
     * @return
     */
    DataGridView loadCurrentUserTask(WorkFlowVo vo);

    /**
     * 根据任务id查询请假单的信息
     * @param taskId
     * @return
     */
    LeaveBill queryLeaveBillByTaskId(String taskId);

    /**
     * 根据任务id查询连线信息
     * @param taskId
     * @return
     */
    List<String> queryOutComeByTaskId(String taskId);


    /**
     * 根据任务id查询连线信息
     * @param taskId
     * @return
     */
    List<String> queryOutComeByTaskId2(String taskId);


    /**
     * 根据任务ID查询批注信息
     * @param taskId
     * @return
     */
    DataGridView queryCommentsByTaskId(String taskId);

    /**
     * 完成任务
     * @param vo
     * @return
     */
    ResultObj completeTask(WorkFlowVo vo);

    /**
     * 根据任务ID查看流程图的高亮显示
     * @param taskId
     * @return
     */
    InputStream viewTaskProcessImageByTaskId(String taskId);

    /**
     * 根据请假单ID查询审批信息
     * @param id
     * @return
     */
    DataGridView queryCommentsByLeaveBillId(Integer id);

    /**
     * 查询当前用户的历史审批记录
     * @param workFlowVo
     * @return
     */
    DataGridView queryCurrentUserHistoryTask(WorkFlowVo workFlowVo);


    /**
     * 查询流程模型
     * @param vo
     * @return
     */
    DataGridView queryProcessModel(WorkFlowVo vo);



    /**
     * 发布模型为流程定义
     * @param id
     */
    ResultObj deployModel(String id);


    /**
     * 删除流程模型
     * @param id
     */
    void deleteProcessModelById(Integer id);


    /**
     * 在线设计模型
     * @return
     */
    DataGridView onlineModel();


}
