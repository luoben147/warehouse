package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.domain.Loginfo;
import com.luoben.warehouse.sys.service.LoginfoService;
import com.luoben.warehouse.sys.vo.LoginfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * <p>
 *  登陆日志管理前端控制器
 * </p>
 *
 * @author luoben
 * @since 2020-03-19
 */
@RestController
@RequestMapping("/loginfo")
public class LoginfoController {

    @Autowired
    private LoginfoService loginfoService;

    /**
     * 全查询
     * @return
     */
    @RequestMapping("/loadAllLoginfo")
    public DataGridView loadAllLoginfo(LoginfoVo loginfoVo){

        IPage<Loginfo> page = new Page<>(loginfoVo.getPage(),loginfoVo.getLimit());
        QueryWrapper<Loginfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNoneBlank(loginfoVo.getLoginname()),"loginname",loginfoVo.getLoginname())
        .like(StringUtils.isNoneBlank(loginfoVo.getLoginip()),"loginip",loginfoVo.getLoginip())
        .ge(loginfoVo.getStartTime()!=null,"logintime",loginfoVo.getStartTime())
        .le(loginfoVo.getEndTime()!=null,"logintime",loginfoVo.getEndTime())
        .orderByDesc("logintime");
        loginfoService.page(page,queryWrapper);

        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("/deleteLoginfo")
    public ResultObj deleteLoginfo(Integer id){
        try {
            loginfoService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/batchDeleteLoginfo")
    public ResultObj batchDeleteLoginfo(Integer[] ids){
        try {
            loginfoService.removeByIds(Arrays.asList(ids));
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }


}

