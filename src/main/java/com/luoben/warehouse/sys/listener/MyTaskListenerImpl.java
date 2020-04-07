package com.luoben.warehouse.sys.listener;

import com.luoben.warehouse.sys.utils.WebUtils;
import com.luoben.warehouse.sys.domain.User;
import com.luoben.warehouse.sys.mapper.UserMapper;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 任务监听器
 */
public class MyTaskListenerImpl implements TaskListener {

    String EVENTNAME_CREATE = "create";
    String EVENTNAME_ASSIGNMENT = "assignment";
    String EVENTNAME_COMPLETE = "complete";
    String EVENTNAME_DELETE = "delete";

    private static final long serialVersionUID = 1L;



    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        if (EVENTNAME_CREATE.endsWith(eventName)){
            System.out.println("create===任务创建");
        }
        else if (EVENTNAME_ASSIGNMENT.endsWith(eventName)){
            System.out.println("assignment===任务分配");
        }
        else if (EVENTNAME_COMPLETE.endsWith(eventName)){
            System.out.println("complete===任务完成");
        }

        else if (EVENTNAME_DELETE.endsWith(eventName)){
            System.out.println("delete===任务删除");
        }


        // 得到IOC容器
        // ApplicationContext context=new
        // ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        WebApplicationContext springContext = WebApplicationContextUtils
                .getWebApplicationContext(WebUtils.getServletContext());
        UserMapper userMapper = springContext.getBean(UserMapper.class);
        // 得到当前用户
        User user = WebUtils.getCurrentUser();
        User temp = new User();
        //获取领导Id
        temp.setId(user.getMgr());
        User leaderUser = userMapper.selectById(temp);
        // 设置下一个任务的办理人
        delegateTask.setAssignee(leaderUser.getName());
    }
}
