package com.luoben.warehouse.sys.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.service.WorkFlowService;
import com.luoben.warehouse.sys.vo.WorkFlowVo;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 流程模型控制器
 */
@Controller
@RequestMapping("promodel")
public class ProcessModelController {

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private RepositoryService repositoryService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ProcessModelController.class);

    /**
     * 加载模型信息
     */
    @RequestMapping("loadAllProcessModel")
    @ResponseBody
    public DataGridView loadAllProcessDefinition(WorkFlowVo vo) {
        return workFlowService.queryProcessModel(vo);
    }
    /**
     * 在线设计模型
     */
    @RequestMapping("onlineModel")
    @ResponseBody
    public DataGridView onlineModel()
    {
        return this.workFlowService.onlineModel();
    }

    /**
     * 发布模型为流程定义
     * @param id
     * @return
     */
    @RequestMapping("deployModel")
    @ResponseBody
    public ResultObj deployModel(Integer id)
    {
        return this.workFlowService.deployModel(id+"");
    }
    /**
     * 删除模型
     */
    @RequestMapping("deleteProcessModel")
    @ResponseBody
    public ResultObj deleteProcessModel(Integer id) {
        try {
            this.workFlowService.deleteProcessModelById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
    /**
     * 批量删除模型
     */
    @RequestMapping("batchdeleteProcessModel")
    @ResponseBody
    public ResultObj batchdeleteProcessModel(WorkFlowVo vo) {
        try {
            for (String id : vo.getIds()) {
                this.deleteProcessModel(Integer.parseInt(id));
            }
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    @RequestMapping("export")
    public void export(String modelId, HttpServletResponse response){
        try {
            Model modelData = repositoryService.getModel(modelId);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);

            // 流程非空判断
            if (!CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
                byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

                ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
                String filename = bpmnModel.getMainProcess().getId() + ".bpmn";
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
                IOUtils.copy(in, response.getOutputStream());
                response.flushBuffer();
            } else {
                try {
                    response.sendRedirect("/system/workFlow/processModelManager");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            LOGGER.error("导出model的xml文件失败：modelId={}", modelId, e);
            try {
                response.sendRedirect("/system/workFlow/processModelManager");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
