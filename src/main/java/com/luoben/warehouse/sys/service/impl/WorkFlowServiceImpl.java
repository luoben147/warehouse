package com.luoben.warehouse.sys.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luoben.warehouse.sys.common.Constast;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.domain.LeaveBill;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.mapper.LeavebillMapper;
import com.luoben.warehouse.sys.service.WorkFlowService;
import com.luoben.warehouse.sys.utils.ActivitiProcessImageUtils;
import com.luoben.warehouse.sys.utils.WebUtils;
import com.luoben.warehouse.sys.vo.WorkFlowVo;
import com.luoben.warehouse.sys.vo.act.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private RepositoryService repositoryService;    //用于管理流程仓库，例如：部署，删除，读取流程资源
    @Autowired
    private RuntimeService runtimeService;  //可以处理所有正在运行状态的流程实例，任务等
    @Autowired
    private TaskService taskService;    //用于管理，查询任务，例如：签收，办理，指派等
    @Autowired
    private HistoryService historyService;  //可以查询所有历史数据，例如：流程实例，任务，活动，变量，附件等
    @Autowired
    private ManagementService managementService;    //和具体业务无关，主要是可以查询引擎配置，数据库，作业等
    @Autowired
    private LeavebillMapper leavebillMapper;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 查询流程部署信息
     */
    public DataGridView queryProcessDeploy(WorkFlowVo workFlowVo) {
        if (workFlowVo.getDeploymentName() == null) {
            workFlowVo.setDeploymentName("");
        }
        String name = workFlowVo.getDeploymentName();
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery(); // 创建流程部署查询
        //查询总条数
        long count = deploymentQuery.deploymentNameLike("%" + name + "%").count();
        //查询
        int firstResult = (workFlowVo.getPage() - 1) * workFlowVo.getLimit();
        int maxResult = workFlowVo.getLimit();
        List<Deployment> list = deploymentQuery.deploymentNameLike("%" + name + "%").listPage(firstResult, maxResult);
        List<ActDeploymentEntityImpl> data = new ArrayList<>();
        for (Deployment deployment : list) {
            ActDeploymentEntityImpl entity = new ActDeploymentEntityImpl();
            BeanUtils.copyProperties(deployment, entity);
            data.add(entity);
        }
        return new DataGridView(count, data);
    }

    /**
     * 查询流程定义
     *
     * @param workFlowVo
     * @return
     */
    @Override
    public DataGridView queryAllProcessDefinition(WorkFlowVo workFlowVo) {
        if (workFlowVo.getDeploymentName() == null) {
            workFlowVo.setDeploymentName("");
        }
        if (workFlowVo.getProcessDefinitionKey() == null) {
            workFlowVo.setProcessDefinitionKey("");
        }

        String deploymentName = workFlowVo.getDeploymentName();
        String pocessDefinitionKey = workFlowVo.getProcessDefinitionKey();
        //根据部署的名称模糊查询所有的部署ID
        List<Deployment> dlist = repositoryService.createDeploymentQuery()
                .processDefinitionKeyLike("%" +pocessDefinitionKey+"%")
                .deploymentNameLike("%" + deploymentName + "%").list();
        Set<String> deploymentIds = new HashSet<>();
        for (Deployment d : dlist) {
            deploymentIds.add(d.getId());
        }
        long count = 0;
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery(); //流程定义查询对象
        List<ActProcessDefinitionEntityImpl> data = new ArrayList<>();
        if (deploymentIds.size() > 0) {
            count = definitionQuery.deploymentIds(deploymentIds).count();
            //查询流程定义信息
            int firstResult = (workFlowVo.getPage() - 1) * workFlowVo.getLimit();
            int maxResult = workFlowVo.getLimit();
            List<ProcessDefinition> listPage = definitionQuery.deploymentIds(deploymentIds).listPage(firstResult, maxResult);
            for (ProcessDefinition pd : listPage) {
                ActProcessDefinitionEntityImpl entity = new ActProcessDefinitionEntityImpl();
                BeanUtils.copyProperties(pd, entity);
                data.add(entity);
            }
        }

        return new DataGridView(count, data);
    }

    /**
     * 部署流程
     *
     * @param inputStream
     * @param deploymentName
     */
    @Override
    public void addWorkFlow(InputStream inputStream, String deploymentName) {
        ZipInputStream stream = new ZipInputStream(inputStream);

        Deployment deploy = repositoryService.createDeployment().name(deploymentName)
                .addZipInputStream(stream).deploy();
        System.out.println("部署成功ID=" + deploy.getId());
        try {
            stream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deleteProcessDeployById(Integer id) {
        repositoryService.deleteDeployment(id + "", true);
    }

    /**
     * 根据流程部署ID查询流程图
     *
     * @param deploymentId
     * @return
     */
    @Override
    public InputStream queryProcessDeploymentImage(String deploymentId) {
        // 1,根据部署ID查询流程定义对象
        ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId).singleResult();
        // 2从流程定义对象里面得到图片的名称
        String resourceName = processDefinition.getDiagramResourceName();
        // 3使用部署ID和图片名称去查询图片流
        InputStream stream = this.repositoryService.getResourceAsStream(deploymentId, resourceName);
        return stream;
    }

    /**
     * 启动流程
     *
     * @param leaveBillId
     */
    @Transactional
    @Override
    public void startProcess(Integer leaveBillId) {
        //找到流程key
        String processDefinitionKey = LeaveBill.class.getSimpleName();//在bpmn20.xml中定义流程元素的id
        String businessKey = processDefinitionKey.concat(":").concat(leaveBillId + "");  //关联的业务主键 LeaveBill:1
        HashMap<String, Object> variables = new HashMap<>();    //流程变量
        User user = (User) WebUtils.getSession().getAttribute("user");
        //设置流程变量去设置下一个办理人
        variables.put("username", user.getName());
        //启动流程
        runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        //更新请假单状态
        LeaveBill leaveBill = leavebillMapper.selectById(leaveBillId);
        //设置状态为 审批中
        leaveBill.setState(Constast.LEAVEBILL_STATE_APPROVAL);
        leavebillMapper.updateById(leaveBill);
    }

    /**
     * 查询当前登录用户的待办任务
     *
     * @param vo
     * @return
     */
    @Override
    public DataGridView loadCurrentUserTask(WorkFlowVo vo) {
        int firstResult = (vo.getPage() - 1) * vo.getLimit();
        int maxResult = vo.getLimit();

        //1.得到办理人的信息
        User user = (User) WebUtils.getSession().getAttribute("user");
        String assignee = user.getName();
        //2.查询总数
        long count = taskService.createTaskQuery().taskAssignee(assignee).count();
        //3.查询集合
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).listPage(firstResult, maxResult);

        List<ActTaskEntityImpl> taskEntities = new ArrayList<>();
        if (tasks != null && tasks.size() > 0) {
            for (Task t : tasks) {
                ActTaskEntityImpl entity = new ActTaskEntityImpl();
                BeanUtils.copyProperties(t, entity);
                taskEntities.add(entity);
            }
        }

        return new DataGridView(count, taskEntities);
    }

    /**
     * 根据任务id查询请假单的信息
     *
     * @param taskId
     * @return
     */
    @Override
    public LeaveBill queryLeaveBillByTaskId(String taskId) {
        //1.根据任务ID查询任务实例
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        //2.从任务实例中取出流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //3.根据流程实例ID查询流程实例
        ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //4.取出business_key
        String businessKey = processInstance.getBusinessKey();
        //5.获取请假单ID
        String leaveBillId = "";
        if (StringUtils.isNotBlank(businessKey)) {
            leaveBillId = businessKey.split(":")[1];
        }
        return this.leavebillMapper.selectById(leaveBillId);
    }


    /**
     * 根据任务id查询连线信息
     *
     * @param taskId
     * @return
     */
    @Override
    public List<String> queryOutComeByTaskId(String taskId) {
        List<String> outcomes = new ArrayList<>();
        // 根据任务ID查询任务对象
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        // 取出当前活动的ID usertask1  //act_ru_task表的TASK_DEF_KEY_字段
        String taskDefinitionKey = task.getTaskDefinitionKey();
        // 找出流程定义的ID
        String processDefinitionId = task.getProcessDefinitionId();
        // 根据流程定义ID找到流程定义对象
        ProcessDefinition processDefinition = this.repositoryService.getProcessDefinition(processDefinitionId);
        // 取出流程定义的KEY   bpmn文件的id
        String key = processDefinition.getKey();
        // 通过流程定义ID找到BPM的对象
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinitionId);
        // 根据流程定义KEY找到从bpmnModel里面找出流程定义
        Process process = bpmnModel.getProcessById(key);
        // 取出流程定义对象里面的所有节点
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            // 找出里面的UserTask节点
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                if (userTask.getId().equals(taskDefinitionKey)) {
                    // 取出出口连线信息
                    List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
                    for (SequenceFlow sequenceFlow : outgoingFlows) {
                        outcomes.add(sequenceFlow.getName());// 取出名字 【页面按钮上的名字】
                    }
                    break;
                }
            }
        }
        return outcomes;
    }


    /**
     * 根据任务id查询连线信息
     *
     * @param taskId
     * @return
     */
    @Override
    public List<String> queryOutComeByTaskId2(String taskId) {
        List<String> outcomes = new ArrayList<>();
        // 根据任务ID查询任务对象
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        // 取出当前活动的ID usertask1  //act_ru_task表的TASK_DEF_KEY_字段
        String taskDefinitionKey = task.getTaskDefinitionKey();
        // 找出流程定义的ID
        String processDefinitionId = task.getProcessDefinitionId();
        // 根据流程定义ID找到流程定义对象
        ProcessDefinition processDefinition = this.repositoryService.getProcessDefinition(processDefinitionId);
        // 取出流程定义的KEY   bpmn文件的id
        String key = processDefinition.getKey();
        // 通过流程定义ID找到BPM的对象
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinitionId);
        // 根据流程定义KEY找到从bpmnModel里面找出流程定义
        Process process = bpmnModel.getProcessById(key);
        // 取出流程定义对象里面的所有节点
        Collection<FlowElement> flowElements = process.getFlowElements();
        //获取节点后的网关
        String nextTargetRef="";
        for (FlowElement flowElement : flowElements) {
            // 找出里面的UserTask节点
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                //找到当前节点
                if (userTask.getId().equals(taskDefinitionKey)) {
                    // 取出出口连线信息
                    List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
                    nextTargetRef = outgoingFlows.get(0).getTargetRef();
                    break;
                }
            }
        }

        for(FlowElement flowElement : flowElements){
            //找到当前节点出口连接的网关
            if(flowElement instanceof ExclusiveGateway){
                ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
                if(exclusiveGateway.getId().equals(nextTargetRef)){
                    // 取出网关出口连线信息
                    List<SequenceFlow> outgoingFlows = exclusiveGateway.getOutgoingFlows();
                    for (SequenceFlow sequenceFlow : outgoingFlows) {
                        outcomes.add(sequenceFlow.getName());// 取出名字 【页面按钮上的名字】
                    }
                    break;
                }
            }
        }

        return outcomes;
    }



    /**
     * 根据任务ID查询批注信息
     *
     * @param taskId
     * @return
     */
    @Override
    public DataGridView queryCommentsByTaskId(String taskId) {
        // 根据任务ID查询任务实例
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        // 取出流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        // 根据流程实例ID查询批注
        List<Comment> comments = this.taskService.getProcessInstanceComments(processInstanceId);
        List<ActCommentEntityImpl> commentEntities = new ArrayList<>();
        if (null != comments && comments.size() > 0) {
            for (Comment comment : comments) {
                ActCommentEntityImpl actCommentEntity = new ActCommentEntityImpl();
                BeanUtils.copyProperties(comment, actCommentEntity);
                commentEntities.add(actCommentEntity);
            }
        }
        return new DataGridView(Long.valueOf(commentEntities.size()), commentEntities);
    }

    /**
     * 完成任务
     *
     * @param vo
     * @return
     */
    @Override
    public ResultObj completeTask(WorkFlowVo vo) {

        String taskId = vo.getTaskId();//任务ID
        String outcome = vo.getOutcome();//连线名称
        String comment = vo.getComment();//批注信息
        Integer leveBillId = vo.getId();// 请假单ID
        try {
            // 根据任务ID查询任务对象
            Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
            // 取出流程实例ID
            String processInstanceId = task.getProcessInstanceId();
            // 添加批注信息
            /**
             * 说明，如果直接添加会发现没有办法显示是哪一个人添加的批注，没有批注人
             * 可以查看AddCommentCmd里面的第95行代码 它使用一个线程局部变量去取的值
             * String userId = Authentication.getAuthenticatedUserId()
             * 所以在添加批注 之前可以先设置批注人放到Authentication authenticatedUserIdThreadLocal里面
             */
            Authentication.setAuthenticatedUserId(WebUtils.getCurrentUser().getName());
            this.taskService.addComment(taskId, processInstanceId, "[" + outcome + "]" + comment);

            // 完成任务    在完成任务走完会触发监听器
            Map<String, Object> variables = new HashMap<>();
            variables.put("outcome", outcome);
            this.taskService.complete(taskId, variables);

            // 判断任务是否结束
            ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (null == processInstance) {// 说明流程结束
                LeaveBill bill = new LeaveBill();
                bill.setId(leveBillId);
                if (outcome.equals("放弃")) {// 请假单状态为 3
                    bill.setState(Constast.LEAVEBILL_STATE_GIVEUP);//放弃
                } else { // 请假单状态为2
                    bill.setState(Constast.LEAVEBILL_STATE_APPROVALED);//审批完成
                }
                this.leavebillMapper.updateById(bill);// 更新请假单的状态
            }
            return ResultObj.MISSION_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("任务完成失败", e);
            return ResultObj.MISSION_FAILURE;
        }
    }

    /**
     * 根据任务ID查看流程图的高亮显示
     *
     * @param taskId
     * @return
     */
    @Override
    public InputStream viewTaskProcessImageByTaskId(String taskId) {
        //根据任务ID查询任务对象
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        //取出流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        InputStream inputStream = ActivitiProcessImageUtils.getActivitiProccessImage(processInstanceId, processEngine);
        return inputStream;
    }

    /**
     * 根据请假单ID查询审批信息
     *
     * @param id
     * @return
     */
    @Override
    public DataGridView queryCommentsByLeaveBillId(Integer id) {
        //组装businessKey
        String businessKey = LeaveBill.class.getSimpleName() + ":" + id;
        // 根据businessKey查询历史流程实例
        HistoricProcessInstance historicProcessInstance = this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey).singleResult();
        // 取出流程实例ID
        String processInstanceId = historicProcessInstance.getId();
        List<ActCommentEntityImpl> commentEntities = new ArrayList<>();
        if (null != processInstanceId) {
            List<Comment> comments = this.taskService.getProcessInstanceComments(processInstanceId);
            for (Comment comment : comments) {
                ActCommentEntityImpl actCommentEntity = new ActCommentEntityImpl();
                BeanUtils.copyProperties(comment, actCommentEntity);
                commentEntities.add(actCommentEntity);
            }
        }
        return new DataGridView(Long.valueOf(commentEntities.size()), commentEntities);
    }

    /**
     * 根据当前登陆的用户信息查询审批记录
     */
    @Override
    public DataGridView queryCurrentUserHistoryTask(WorkFlowVo workFlowVo) {
        User user = WebUtils.getCurrentUser();// 找出当前用户
        String assignee = user.getName();
        long count = this.historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).count();
        List<HistoricTaskInstance> list = this.historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee)
                .orderByHistoricTaskInstanceEndTime().desc()
                .listPage((workFlowVo.getPage() - 1) * workFlowVo.getLimit(), workFlowVo.getLimit());

        List<ActTaskEntityImpl> data = new ArrayList<>();
        for (HistoricTaskInstance task : list) {
            ActTaskEntityImpl entity = new ActTaskEntityImpl();
            // 对象之间的属性值的copy
            BeanUtils.copyProperties(task, entity);
            data.add(entity);
        }
        return new DataGridView(count, data);
    }

    /**
     * 查询流程模型信息
     * @param vo
     * @return
     */
    @Override
    public DataGridView queryProcessModel(WorkFlowVo vo) {
        int firstResult = (vo.getPage() - 1) * vo.getLimit();
        int maxResult = vo.getLimit();
        List<Model> queryResult = new ArrayList<>();
//        boolean fl = vo.getModelId().equals("");
        if (vo.getModelId() != null && !vo.getModelId().equals("")) {
            queryResult = this.repositoryService.createModelQuery().modelId(vo.getModelId()).modelNameLike("%" + vo.getModelName() + "%").listPage(firstResult, maxResult);
        }
        //根据模型名称查询
        if (vo.getModelId() == null || vo.getModelId().equals("")) {
            if (vo.getModelName() == null) {
                vo.setModelName("");
            }
            queryResult = this.repositoryService.createModelQuery().modelNameLike("%" + vo.getModelName() + "%").listPage(firstResult, maxResult);
        }
        long count = queryResult.size();
        ArrayList<ModelEntityVo> objects = new ArrayList<>();

        for (Model model : queryResult) {
            objects.add(new ModelEntityVo(model));
        }
        return new DataGridView(count, objects);
    }

    /**
     * 发布模型为流程定义
     * @param id
     */
    @Override
    public ResultObj deployModel(String id) {
        try {
            //获取模型
            Model modelData = this.repositoryService.getModel(id);
            byte[] bytes = this.repositoryService.getModelEditorSource(modelData.getId());

            if (bytes == null) {
                return ResultObj.DEPLOY_ERROR;
                //            return ToWeb.buildResult().status(Status.FAIL)
                //                    .msg("模型数据为空，请先设计流程并成功保存，再进行发布。");
            }

            JsonNode modelNode = new ObjectMapper().readTree(bytes);

            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                return ResultObj.DEPLOY_ERROR;
                //            return ToWeb.buildResult().status(Status.FAIL)
                //                    .msg("数据模型不符要求，请至少设计一条主线流程。");
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

            //发布流程
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = this.repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, new String(bpmnBytes, "UTF-8"))
                    .deploy();
            modelData.setDeploymentId(deployment.getId());
            this.repositoryService.saveModel(modelData);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("【{}】流程部署出现问题", id, e);
            return ResultObj.DEPLOY_ERROR;
        }
        return ResultObj.DEPLOY_SUCCESS;
    }


    /**
     * 在线设计模型
     * @return
     */
    @Override
    public DataGridView onlineModel() {
        String id = "";
        try {
            //初始化一个空模型
            Model model = this.repositoryService.newModel();

            //设置一些默认信息
            String name = "new-process";
            String description = "";
            int revision = 1;
            String key = "process";

            ObjectNode modelNode = objectMapper.createObjectNode();
            modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

            model.setName(name);
            model.setKey(key);
            model.setMetaInfo(modelNode.toString());

            this.repositoryService.saveModel(model);
            id = model.getId();

            //完善ModelEditorSource
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            this.repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return new DataGridView();
        }
        return new DataGridView(id);
    }

    /**
     * 删除流程模型
     * @param id
     */
    @Override
    public void deleteProcessModelById(Integer id) {
        this.repositoryService.deleteModel(id + "");
    }




}
