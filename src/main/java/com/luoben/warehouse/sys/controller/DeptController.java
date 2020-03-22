package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.common.TreeNode;
import com.luoben.warehouse.sys.domain.Dept;
import com.luoben.warehouse.sys.service.DeptService;
import com.luoben.warehouse.sys.vo.DeptVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoben
 * @since 2020-03-20
 */
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 加载部门管理左边的部门树json
     * @return
     */
    @RequestMapping("/loadDeptManagerLeftTreeJson")
    public DataGridView loadDeptManagerLeftTreeJson(DeptVo deptVo){
        List<Dept> list=deptService.list();

        List<TreeNode> treeNodes=new ArrayList<>();
        list.forEach(d ->{
            Boolean spread =d.getOpen()==1? true:false;
            treeNodes.add(new TreeNode(d.getId(),d.getPid(),d.getTitle(),spread));
        });

        return  new DataGridView(treeNodes);
    }

    @RequestMapping("/loadAllDept")
    public DataGridView loadAllDept(DeptVo deptVo){

        IPage<Dept> page=new Page<>(deptVo.getPage(),deptVo.getLimit());
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNoneBlank(deptVo.getTitle()),"title",deptVo.getTitle())
                .like(StringUtils.isNoneBlank(deptVo.getAddress()),"address",deptVo.getAddress())
                .like(StringUtils.isNoneBlank(deptVo.getRemark()),"remark",deptVo.getRemark());
        queryWrapper.eq(deptVo.getId()!=null,"id",deptVo.getId()).or().eq(deptVo.getId()!=null,"pid",deptVo.getId());
        queryWrapper.orderByAsc("ordernum");
        deptService.page(page,queryWrapper);

        return  new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 加载最大的排序吗
     * @return
     */
    @RequestMapping("/loadDeptMaxOrderNum")
    public Map<String,Object> loadDeptMaxOrderNum(){
        Map<String,Object> map=new HashMap<>();
        Dept dept = deptService.getLastOneDept();
        if(dept!=null){
            map.put("value",dept.getOrdernum()+1);
        }else {
            map.put("value",1);
        }
        return map;
    }

    /**
     * 新增
     * @param deptVo
     * @return
     */
    @RequestMapping("/addDept")
    public ResultObj addDept(DeptVo deptVo){
        try {
            //前台没有选择父级部门，则认为默认添加的是顶级部门
            if(deptVo.getPid()==null){
                deptVo.setPid(0);
            }
            deptVo.setCreatetime(new Date());
            deptService.save(deptVo);
            return  ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改
     * @param deptVo
     * @return
     */
    @RequestMapping("/updateDept")
    public ResultObj updateDept(DeptVo deptVo){
        try {
            deptService.updateById(deptVo);
            return  ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 查询当前id的部门下有没有子部门
     * @param id
     * @return
     */
    @RequestMapping("/checkDeptHasChildrenNode")
    public Map<String,Object> checkDeptHasChildrenNode(Long id){
        Map<String,Object> map=new HashMap<>();

        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.eq("pid",id);
        List<Dept> list = deptService.list(wrapper);

        if(list.size()>0){
            map.put("value",true);
        }else {
            map.put("value",false);
        }
        return map;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("/deleteDept")
    public ResultObj deleteDept(Long id){
        try {
            deptService.removeById(id);
            return  ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultObj.DELETE_ERROR;
        }
    }

}

