package com.luoben.warehouse.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luoben.warehouse.sys.common.DataGridView;
import com.luoben.warehouse.sys.common.ResultObj;
import com.luoben.warehouse.sys.common.WebUtils;
import com.luoben.warehouse.sys.domain.Notice;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.service.NoticeService;
import com.luoben.warehouse.sys.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 *  公告管理前端控制器
 * </p>
 *
 * @author luoben
 * @since 2020-03-20
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @RequestMapping("/loadAllNotice")
    public DataGridView loadAllNotice(NoticeVo noticeVo){

        IPage<Notice> page=new Page<>(noticeVo.getPage(),noticeVo.getLimit());
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNoneBlank(noticeVo.getTitle()),"title",noticeVo.getTitle())
                    .like(StringUtils.isNoneBlank(noticeVo.getOpername()),"opername",noticeVo.getOpername())
                    .ge(noticeVo.getStartTime()!=null,"createtime",noticeVo.getStartTime())
                    .le(noticeVo.getEndTime()!=null,"createtime",noticeVo.getEndTime())
                    .orderByDesc("createtime");
        noticeService.page(page,queryWrapper);

        return  new DataGridView(page.getTotal(),page.getRecords());
    }


    /**
     * 新增公告
     * @param noticeVo
     * @return
     */
    @RequestMapping("/addNotice")
    public ResultObj addNotice(NoticeVo noticeVo){
        try {
            User user= (User)WebUtils.getSession().getAttribute("user");
            noticeVo.setCreatetime(new Date());
            noticeVo.setOpername(user.getName());
            noticeService.save(noticeVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     * 更新公告
     * @param noticeVo
     * @return
     */
    @RequestMapping("/updateNotice")
    public ResultObj updateNotice(NoticeVo noticeVo){
        try {
            noticeService.updateById(noticeVo);
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
    @RequestMapping("/deleteNotice")
    public ResultObj deleteNotice(Integer id){
        try {
            noticeService.removeById(id);
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
    @RequestMapping("/batchDeleteNotice")
    public ResultObj batchDeleteNotice(Integer[] ids){
        try {
            noticeService.removeByIds(Arrays.asList(ids));
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    @RequestMapping("/loadNoticeById")
    public DataGridView loadNoticeById(Integer id){
        Notice notice = noticeService.getById(id);
        return  new DataGridView(notice);
    }


}

