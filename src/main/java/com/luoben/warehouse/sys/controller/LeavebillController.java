package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.Constast;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.common.WebUtils;
import com.luoben.warehouse.sys.domain.Leavebill;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.service.LeavebillService;
import com.luoben.warehouse.sys.vo.LeaveBillVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoben
 * @since 2020-03-25
 */
@RestController
@RequestMapping("/leavebill")
public class LeavebillController {

    @Autowired
    private LeavebillService leavebillService;

    /**
     * 全查询
     * @return
     */
    @RequestMapping("/loadAllLeavebill")
    public DataGridView loadAllLeavebill(LeaveBillVo leaveBillVo){

        IPage<Leavebill> page = new Page<>(leaveBillVo.getPage(),leaveBillVo.getLimit());
        QueryWrapper<Leavebill> queryWrapper = new QueryWrapper<>();
        User user= (User)WebUtils.getSession().getAttribute("user");
        if(user.getType()!=Constast.USER_TYPE_SUPER){
            leaveBillVo.setUserid(user.getId());
        }
        queryWrapper.like(StringUtils.isNoneBlank(leaveBillVo.getTitle()),"title",leaveBillVo.getTitle())
                .like(StringUtils.isNoneBlank(leaveBillVo.getContent()),"content",leaveBillVo.getContent())
                .eq(leaveBillVo.getUserid()!=null,"userid",leaveBillVo.getUserid())
                .ge(leaveBillVo.getStartTime()!=null,"leavetime",leaveBillVo.getStartTime())
                .le(leaveBillVo.getEndTime()!=null,"leavetime",leaveBillVo.getEndTime())
                .orderByDesc("leavetime");
        leavebillService.page(page,queryWrapper);

        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 新增请假单
     * @param leaveBillVo
     * @return
     */
    @RequestMapping("/addLeavebill")
    public ResultObj addLeavebill(LeaveBillVo leaveBillVo){
        try {
            User user= (User)WebUtils.getSession().getAttribute("user");
            leaveBillVo.setUserid(user.getId());
            leaveBillVo.setState(Constast.LEAVEBILL_STATE_UNSUBMIT);
            leavebillService.save(leaveBillVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     * 更新请假单
     * @param leaveBillVo
     * @return
     */
    @RequestMapping("/updateLeavebill")
    public ResultObj updateLeavebill(LeaveBillVo leaveBillVo){
        try {
            leavebillService.updateById(leaveBillVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }


    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("/deleteLeavebill")
    public ResultObj deleteLeavebill(Integer id){
        try {
            leavebillService.removeById(id);
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
    @RequestMapping("/batchDeleteLeavebill")
    public ResultObj batchDeleteLeavebill(Integer[] ids){
        try {
            leavebillService.removeByIds(Arrays.asList(ids));
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}

