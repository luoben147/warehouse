package com.luoben.warehouse.sys.controller;

import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.domain.LeaveBill;
import com.luoben.warehouse.sys.service.LeavebillService;
import com.luoben.warehouse.sys.service.WorkFlowService;
import com.luoben.warehouse.sys.vo.WorkFlowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流的控制器
 */
@Controller
@RequestMapping("/workFlow")
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private LeavebillService leavebillService;

    /**
     * 加载部署信息
     */
    @ResponseBody
    @RequestMapping("/loadAllDeployment")
    public DataGridView loadAllDeployment(WorkFlowVo workFlowVo) {
        return this.workFlowService.queryProcessDeploy(workFlowVo);
    }

    /**
     * 加载流程定义信息
     */
    @ResponseBody
    @RequestMapping("/loadAllProcessDefinition")
    public DataGridView loadAllProcessDefinition(WorkFlowVo workFlowVo) {
        return this.workFlowService.queryAllProcessDefinition(workFlowVo);
    }

    /**
     * 添加流程部署
     * zip部署
     */
    @ResponseBody
    @RequestMapping("/addWorkFlow")
    public Map<String, Object> addWorkFlow(MultipartFile mf, WorkFlowVo workFlowVo) {
        Map<String, Object> map = new HashMap<>();
        try {

            workFlowService.addWorkFlow(mf.getInputStream(), workFlowVo.getDeploymentName());
            map.put("msg", "部署成功");
        } catch (Exception e) {
            map.put("msg", "部署失败");
            e.printStackTrace();
        }

        return map;
    }


    /**
     * 删除流程部署
     */
    @ResponseBody
    @RequestMapping("/deleteWorkFlow")
    public ResultObj deleteWorkFlow(Integer id) {
        try {
            this.workFlowService.deleteProcessDeployById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }


    /**
     * 查看流程图
     */
    @RequestMapping("toViewProcessImage")
    public String toViewProcessImage(Model model, WorkFlowVo workFlowVo){
        model.addAttribute("deploymentId", workFlowVo.getDeploymentId());
        return "system/workFlow/viewProcessImage";}

    /**
     * 查看流程图
     */
    @RequestMapping("/viewProcessImage")
    public void viewProcessImage(WorkFlowVo workFlowVo, HttpServletResponse response) {
        InputStream stream = workFlowService.queryProcessDeploymentImage(workFlowVo.getDeploymentId());
        try {
            BufferedImage image = ImageIO.read(stream);
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"JPEG",outputStream);
            stream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 提交请假单，启动流程
     * zip部署
     */
    @ResponseBody
    @RequestMapping("/startProcess")
    public ResultObj startProcess(WorkFlowVo workFlowVo) {
        try {
            Integer leaveBillId=workFlowVo.getId();
            workFlowService.startProcess(leaveBillId);
            return ResultObj.START_PROCESS_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.START_PROCESS_ERROR;
        }
    }

    /**
     * 获取当前用户的待办任务
     * @return
     */
    @ResponseBody
    @RequestMapping("/loadCurrentUserTask")
    public DataGridView loadCurrentUserTask(WorkFlowVo vo){
        return workFlowService.loadCurrentUserTask(vo);
    }


    /**
     * 跳转到办理任务界面
     */
    @RequestMapping("toDoTask")
    public String toDoTask(Model model, WorkFlowVo vo){
        model.addAttribute("taskId", vo.getTaskId());
        //1.根据任务id查询请假单的信息
        LeaveBill leavebill =this.workFlowService.queryLeaveBillByTaskId(vo.getTaskId());
        model.addAttribute("leaveBill",leavebill);
        //2.根据任务id查询连线信息
        List<String> outComeNames =this.workFlowService.queryOutComeByTaskId2(vo.getTaskId());
        model.addAttribute("outcomes",outComeNames);
        return"system/workFlow/doTask";
    }


    /**
     * 根据任务ID查询批注信息
     */
    @ResponseBody
    @RequestMapping("/loadCommentsByTaskId")
    public DataGridView loadCommentsByTaskId(WorkFlowVo vo){
        return workFlowService.queryCommentsByTaskId(vo.getTaskId());
    }


    /**
     * 完成任务
     * @param vo
     * @return
     */
    @RequestMapping("completeTask")
    @ResponseBody
    public ResultObj completeTask(WorkFlowVo vo)
    {
        return this.workFlowService.completeTask(vo);
    }


    /**
     * 根据任务ID查看流程图的高亮显示
     * @param vo
     */
    @RequestMapping("/viewTaskProcessImageByTaskId")
    @ResponseBody
    public void viewTaskProcessImageByTaskId(WorkFlowVo vo,HttpServletResponse response){
        InputStream is =workFlowService.viewTaskProcessImageByTaskId(vo.getTaskId());
        try {
            BufferedImage image = ImageIO.read(is);
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"JPEG",outputStream);
            is.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请假单的审批进度查询
     * 根据请假单ID查询审批批注信息和请假单信息
     * @param vo
     * @return
     */
    @RequestMapping("toViewSPQuery")
    public String toViewSPQuery(WorkFlowVo vo,Model model){
        Integer leavebillId = vo.getId(); //请假单ID
        String taskId = vo.getTaskId();//任务ID
        LeaveBill leavebill=null;
        if(null!=leavebillId){
            //请假单信息
            leavebill = this.leavebillService.getById(vo.getId());
        }else {
            if(null!=taskId){
                leavebill =this.workFlowService.queryLeaveBillByTaskId(taskId);
            }
        }
        model.addAttribute("leavebill", leavebill);
        return "system/workFlow/viewSpManager";
    }

    /**
     * 根据请假单ID查询审批信息
     */
    @RequestMapping("loadCommentsByLeaveBillId")
    @ResponseBody
    public DataGridView loadCommentsByLeaveBillId(WorkFlowVo workFlowVo) {
        return this.workFlowService.queryCommentsByLeaveBillId(workFlowVo.getId());
    }

    /**
     * 查询当前用户的审批记录
     * @param vo
     * @return
     */
    @RequestMapping("loadMyHistoryTask")
    @ResponseBody
    public DataGridView loadMyHistoryTask(WorkFlowVo vo){
        return this.workFlowService.queryCurrentUserHistoryTask(vo);
    }
}
