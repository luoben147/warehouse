package com.luoben.warehouse.sys.controller;


import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.Constast;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.PinyinUtils;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.domain.Dept;
import com.luoben.warehouse.sys.domain.Role;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.service.DeptService;
import com.luoben.warehouse.sys.service.RoleService;
import com.luoben.warehouse.sys.service.UserService;
import com.luoben.warehouse.sys.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  用户管理前端控制器
 * </p>
 *
 * @author luoben-
 * @since 2020-03-18
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;


    /**
     * 用户查询
     */
    @RequestMapping("/loadAllUser")
    public DataGridView loadAllUser(UserVo userVo){
        IPage<User> page=new Page<>(userVo.getPage(), userVo.getLimit());
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getName()), "loginname", userVo.getName()).or().eq(StringUtils.isNotBlank(userVo.getName()), "name", userVo.getName());
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getAddress()), "address", userVo.getAddress());
        queryWrapper.eq("type", Constast.USER_TYPE_NORMAL);//查询系统用户
        queryWrapper.eq(userVo.getDeptid()!=null, "deptid",userVo.getDeptid());//部门
        this.userService.page(page, queryWrapper);

        List<User> list = page.getRecords();
        for (User user : list) {
            Integer deptid = user.getDeptid();
            //根据部门id 获取部门名称
            if(deptid!=null) {
                Dept dept =deptService.getById(deptid);
                user.setDeptname(dept.getTitle());
            }
            Integer mgr = user.getMgr();
            //根据 领导的用户ID 领导名称
            if(mgr!=null) {
                User mgrUser = this.userService.getById(mgr);
                user.setLeadername(mgrUser.getName());
            }
        }

        return new DataGridView(page.getTotal(), list);
    }


    /**
     * 加载最大的排序码
     * @return
     */
    @RequestMapping("/loadUserMaxOrderNum")
    public Map<String,Object> loadUserMaxOrderNum(){
        Map<String,Object> map=new HashMap<>();
        Integer  maxOrderNum= userService.getMaxOrderNum();
        if (maxOrderNum != null) {
            map.put("value", maxOrderNum + 1);
        } else {
            map.put("value", 1);
        }
        return map;
    }


    /**
     * 根据部门ID查询当前部门下面的领导列表
     * @param deptid
     * @return
     */
    @RequestMapping("/loadUsersByDeptId")
    public DataGridView loadUsersByDeptId(Long deptid){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deptid",deptid);
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        queryWrapper.eq("type",Constast.USER_TYPE_NORMAL);
        List<User> list = userService.list(queryWrapper);

        return new DataGridView(list);
    }

    /**
     * 汉字转拼音
     * @param username
     * @return
     */
    @RequestMapping("/changeChineseToPinyin")
    public Map<String,Object> changeChineseToPinyin(String username){
        Map<String,Object> map=new HashMap<>();
        if(null!=username){
            map.put("value",PinyinUtils.getPingYin(username));
        }else {
            map.put("value","");
        }
        return map;
    }


    /**
     * 添加用户
     * @param userVo
     * @return
     */
    @RequestMapping("/addUser")
    public ResultObj addUser(UserVo userVo){

        try {
            //设置类型
            userVo.setType(Constast.USER_TYPE_NORMAL);
            userVo.setHiredate(new Date());
            //设置盐 随机UUID
            String salt = IdUtil.simpleUUID().toUpperCase();
            userVo.setSalt(salt);
            //设置默认密码
            userVo.setPwd(new Md5Hash(Constast.USER_DEFAULT_PWD,salt,2).toString());
            //设置用户默认头像
            userVo.setImgpath(Constast.DEFAULT_IMG_USER);
            userService.save(userVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     * 根据id查询一个用户
     * @param id  领导的id
     * @return
     */
    @RequestMapping("loadUserById")
    public DataGridView loadUserById(Integer id){
        return new DataGridView(userService.getById(id));
    }

    /**
     * 修改用户
     * @param userVo
     * @return
     */
    @RequestMapping("updateUser")
    public ResultObj updateUser(UserVo userVo){
        try {
            userService.updateById(userVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequestMapping("deleteUser/{id}")
    public ResultObj deleteUser(@PathVariable("id") Integer id){
        try {
            userService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }



    /**
     * 重置用户密码
     * @param id
     * @return
     */
    @RequestMapping("resetPwd")
    public ResultObj resetPwd(Integer id){
        try {
            User user = new User();
            user.setId(id);
            //设置盐  32位(大写英文字母(A-Z)加数字(0-9))
            String salt = IdUtil.simpleUUID().toUpperCase();
            user.setSalt(salt);
            //设置密码
            user.setPwd(new Md5Hash(Constast.USER_DEFAULT_PWD,salt,2).toString());
            userService.updateById(user);
            return ResultObj.RESET_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.RESET_ERROR;
        }
    }

    /**
     * 根据用户id查询角色并选中已拥有的角色
     * @param id 用户id
     * @return
     */
    @RequestMapping("initRoleByUserId")
    public DataGridView initRoleByUserId(Integer id){
        //1.查询所有可用的角色
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        List<Map<String, Object>> listMaps = roleService.listMaps(queryWrapper);
        //2.查询当前用户拥有的角色ID集合
        List<Integer> currentUserRoleIds = roleService.queryUserRoleIdsByUid(id);
        for (Map<String, Object> map : listMaps) {
            Boolean LAY_CHECKED=false;
            Integer roleId = (Integer) map.get("id");
            for (Integer rid : currentUserRoleIds) {
                //如果当前用户已有该角色，则让LAY_CHECKED为true。LAY_CHECKED为true时，复选框选中
                if (rid.equals(roleId)){
                    LAY_CHECKED=true;
                    break;
                }
            }
            map.put("LAY_CHECKED",LAY_CHECKED);
        }
        return new DataGridView(Long.valueOf(listMaps.size()),listMaps);
    }


    /**
     * 保存用户和角色的关系
     * @param uid 用户的ID
     * @param ids 用户拥有的角色的ID的数组
     * @return
     */
    @RequestMapping("saveUserRole")
    public ResultObj saveUserRole(Integer uid,Integer[] ids){
        try {
            userService.saveUserRole(uid,ids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }
}

