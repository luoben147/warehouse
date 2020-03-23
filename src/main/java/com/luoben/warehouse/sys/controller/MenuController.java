package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.*;
import com.luoben.warehouse.sys.domain.Permission;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.service.PermissionService;
import com.luoben.warehouse.sys.service.RoleService;
import com.luoben.warehouse.sys.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 *  菜单管理前端控制器
 * </p>
 *
 * @author luoben
 * @since 2020-03-19
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @RequestMapping("/loadIndexLeftMenuJson")
    public DataGridView loadIndexLeftMenuJson(PermissionVo permissionVo){
        //查询所有菜单
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        //设置只查菜单
        wrapper.eq("type",Constast.TYPE_MENU)
                .eq("available",Constast.AVAILABLE_TRUE);

        User user = (User) WebUtils.getSession().getAttribute("user");
        List<Permission> list=null;
        //当前登录的是超级管理员
        if(user.getType()==Constast.USER_TYPE_SUPER){
            list = permissionService.list(wrapper);
        }else {
            //根据用户ID+角色+权限查询
            Integer uid=user.getId();
            //1.根据用户id查询角色
            List<Integer> curUserRoleIds = roleService.queryUserRoleIdsByUid(uid);
            //2.根据角色ID查询菜单ID和权限ID
            //使用set去重
            Set<Integer> pids = new HashSet<>();
            for (Integer rid : curUserRoleIds) {
                //根据角色ID查询菜单ID和权限ID
                List<Integer> permissionIds = roleService.queryRolePermissionIdsByRid(rid);
                //将菜单ID和权限ID放入Set中去重
                pids.addAll(permissionIds);
            }
            //3.根据角色ID查询权限
            if (pids.size()>0){
                wrapper.in("id",pids);
                list = permissionService.list(wrapper);
            }else {
                list=new ArrayList<>();
            }
        }

        //构建menu树
        List<TreeNode> treeNodes=new ArrayList<>();
        for (Permission p:list){
            Integer id =p.getId();
            Integer pid=p.getPid();
            String title = p.getTitle();
            String icon = p.getIcon();
            String href = p.getHref();
            Boolean spread = p.getOpen() == Constast.OPEN_TRUE ? true : false;
            treeNodes.add(new TreeNode(id,pid,title,icon,href,spread));
        }
        //构建层级关系
        treeNodes = TreeNodeBuilder.build(treeNodes, 1);
        return  new DataGridView(treeNodes);
    }


    /****************菜单管理开始****************/

    /**
     * 加载菜单管理左边的菜单树的json
     */
    @RequestMapping("loadMenuManagerLeftTreeJson")
    public DataGridView loadMenuManagerLeftTreeJson(PermissionVo permissionVo) {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constast.TYPE_MENU);
        List<Permission> list = permissionService.list(queryWrapper);
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission menu : list) {
            Boolean spread = menu.getOpen() == 1 ? true : false;
            treeNodes.add(new TreeNode(menu.getId(), menu.getPid(), menu.getTitle(), spread));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 查询
     */
    @RequestMapping("loadAllMenu")
    public DataGridView loadAllMenu(PermissionVo permissionVo) {
        IPage<Permission> page = new Page<>(permissionVo.getPage(), permissionVo.getLimit());

        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(permissionVo.getId() != null, "id", permissionVo.getId()).or().eq(permissionVo.getId() != null, "pid", permissionVo.getId());
        queryWrapper.eq("type", Constast.TYPE_MENU);//只能查询菜单
        queryWrapper.like(StringUtils.isNotBlank(permissionVo.getTitle()), "title", permissionVo.getTitle());
        queryWrapper.orderByAsc("ordernum");
        permissionService.page(page, queryWrapper);

        return new DataGridView(page.getTotal(), page.getRecords());
    }

    /**
     * 加载最大的排序码
     */
    @RequestMapping("loadMenuMaxOrderNum")
    public Map<String, Object> loadDeptMaxOrderNum() {
        Map<String, Object> map = new HashMap<>();
        Integer maxOrderNum = permissionService.getMaxOrderNum();
        if (maxOrderNum != null) {
            map.put("value", maxOrderNum + 1);
        } else {
            map.put("value", 1);
        }
        return map;
    }


    /**
     * 添加
     *
     * @param permissionVo
     * @return
     */
    @RequestMapping("addMenu")
    public ResultObj addMenu(PermissionVo permissionVo) {
        try {
            permissionVo.setType(Constast.TYPE_MENU);//设置添加类型
            this.permissionService.save(permissionVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     * 修改
     *
     * @param permissionVo
     * @return
     */
    @RequestMapping("updateMenu")
    public ResultObj updateMenu(PermissionVo permissionVo) {
        try {
            this.permissionService.updateById(permissionVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }


    /**
     * 查询当前的ID的菜单有没有子菜单
     */
    @RequestMapping("checkMenuHasChildrenNode")
    public Map<String, Object> checkMenuHasChildrenNode(PermissionVo permissionVo) {
        Map<String, Object> map = new HashMap<String, Object>();
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", permissionVo.getId());
        List<Permission> list = this.permissionService.list(queryWrapper);
        if (list.size() > 0) {
            map.put("value", true);
        } else {
            map.put("value", false);
        }
        return map;
    }

    /**
     * 删除
     *
     * @param permissionVo
     * @return
     */
    @RequestMapping("deleteMenu")
    public ResultObj deleteMenu(PermissionVo permissionVo) {
        try {
            this.permissionService.removeById(permissionVo.getId());
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /****************菜单管理结束****************/

}

