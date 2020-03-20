package com.luoben.warehouse.sys.vo;

import com.luoben.warehouse.sys.domain.Loginfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginfoVo extends Loginfo {

    private static final long serialVersionUID = 1L;

    private Integer page = 1;   //当前页码
    private Integer limit = 10; //每页条数

    //接收多个ID
    private Integer[] ids;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}