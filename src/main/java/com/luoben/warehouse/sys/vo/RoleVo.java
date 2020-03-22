package com.luoben.warehouse.sys.vo;

import com.luoben.warehouse.sys.domain.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RoleVo extends Role {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Integer page=1;
	private Integer limit=10;
	
}
