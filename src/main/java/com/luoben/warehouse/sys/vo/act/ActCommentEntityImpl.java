package com.luoben.warehouse.sys.vo.act;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 批注信息转换类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ActCommentEntityImpl {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;
    private String userId;
    private String message;
    private String fullMessage;

}
