package com.luoben.warehouse.sys.service.impl;

import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.mapper.UserMapper;
import com.luoben.warehouse.sys.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luoben-
 * @since 2020-03-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
