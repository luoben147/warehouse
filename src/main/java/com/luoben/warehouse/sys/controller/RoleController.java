package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.Constast;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.common.TreeNode;
import com.luoben.warehouse.sys.domain.Permission;
import com.luoben.warehouse.sys.domain.Role;
import com.luoben.warehouse.sys.service.PermissionService;
import com.luoben.warehouse.sys.service.RoleService;
import com.luoben.warehouse.sys.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoben
 * @since 2020-03-22
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询
     */
    @RequestMapping("loadAllRole")
    public DataGridView loadAllRole(RoleVo roleVo) {
        IPage<Role> page=new Page<>(roleVo.getPage(), roleVo.getLimit());
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getName()), "name", roleVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getRemark()), "remark", roleVo.getRemark());
        queryWrapper.eq(roleVo.getAvailable()!=null, "available", roleVo.getAvailable());
        queryWrapper.orderByDesc("createtime");
        this.roleService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }


    /**
     * 添加
     */
    @RequestMapping("addRole")
    public ResultObj addRole(RoleVo roleVo) {
        try {
            roleVo.setCreatetime(new Date());
            this.roleService.save(roleVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }
    /**
     * 修改
     */
    @RequestMapping("updateRole")
    public ResultObj updateRole(RoleVo roleVo) {
        try {
            this.roleService.updateById(roleVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     */
    @RequestMapping("deleteRole")
    public ResultObj deleteRole(Integer id) {
        try {
            this.roleService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }


    /**
     * 根据角色id加载菜单和权限的树的json
     *
     */
    @RequestMapping("initPermissionByRoleId")
    public DataGridView initPermissionByRoleId(Long roleId){
        //查询所有可用的菜单和权限
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        List<Permission> permissionList = permissionService.list(queryWrapper);

        //根据角色ID查询当前角色拥有的权限和菜单

        //1.根据角色ID查询当前角色拥有的权限或菜单Id
        List<Integer> pids = roleService.queryRolePermissionIdsByRid(roleId);
        //2.根据查询出来的菜单ID和权限ID，再查询出菜单的数据和权限的数据
        List<Permission> currentPermissions = null;
        //如果根据角色id查询出来了菜单ID或权限ID，就去查询
        if (pids.size()>0){
            queryWrapper.in("id",pids);
            currentPermissions = permissionService.list(queryWrapper);
        }else {
            currentPermissions = new ArrayList<>();
        }
        //构造List<TreeNode>
        List<TreeNode> nodes = new ArrayList<>();
        for (Permission allPermission : permissionList) {
            String checkArr = "0";
            for (Permission currentPermission : currentPermissions) {
                if (allPermission.getId().equals(currentPermission.getId())){
                    checkArr = "1";
                    break;
                }
            }
            Boolean spread = (allPermission.getOpen()==null||allPermission.getOpen()==1)?true:false;
            nodes.add(new TreeNode(allPermission.getId(),allPermission.getPid(),allPermission.getTitle(),spread,checkArr));
        }
        return new DataGridView(nodes);

    }

    /**
     * 保存角色和菜单权限之间的关系
     * @param rid
     * @param ids
     * @return
     */
    @RequestMapping("saveRolePermission")
    public ResultObj saveRolePermission(Integer rid,Integer[] ids){
        try {
            roleService.saveRolePermission(rid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }



}

