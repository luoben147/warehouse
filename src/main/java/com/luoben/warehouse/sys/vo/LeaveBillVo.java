package com.luoben.warehouse.sys.vo;

import com.luoben.warehouse.sys.domain.Leavebill;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class LeaveBillVo extends Leavebill {

	private static final long serialVersionUID = 1L;

	// 批量删除使用
	private Integer[] ids;

	private Integer page;
	private Integer limit;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;


}
