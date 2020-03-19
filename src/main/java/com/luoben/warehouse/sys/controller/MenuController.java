package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luoben.warehouse.sys.common.*;
import com.luoben.warehouse.sys.domain.Permission;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.service.PermissionService;
import com.luoben.warehouse.sys.vo.PermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
            list = permissionService.list(wrapper);
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

}

