package com.luoben.warehouse.sys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    /**
     * 路由跳转
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("sys/toLogin").setViewName("system/index/login");
        registry.addViewController("sys/index").setViewName("system/index/index");
        registry.addViewController("sys/toDeskManager").setViewName("system/index/deskManager");
        registry.addViewController("sys/toLoginfoManager").setViewName("system/loginfo/loginfoManager");
        registry.addViewController("sys/toNoticeManager").setViewName("system/notice/noticeManager");
        registry.addViewController("sys/toAddOrUpdate").setViewName("system/notice/addOrUpdate");
        registry.addViewController("sys/toDeptManager").setViewName("system/dept/deptManager");
        registry.addViewController("sys/toDeptLeft").setViewName("system/dept/deptLeft");
        registry.addViewController("sys/toDeptRight").setViewName("system/dept/deptRight");
        registry.addViewController("sys/toMenuManager").setViewName("system/menu/menuManager");
        registry.addViewController("sys/toMenuLeft").setViewName("system/menu/menuLeft");
        registry.addViewController("sys/toMenuRight").setViewName("system/menu/menuRight");
        registry.addViewController("sys/toPermissionManager").setViewName("system/permission/permissionManager");
        registry.addViewController("sys/toPermissionLeft").setViewName("system/permission/permissionLeft");
        registry.addViewController("sys/toPermissionRight").setViewName("system/permission/permissionRight");
        registry.addViewController("sys/toRoleManager").setViewName("system/role/roleManager");
        registry.addViewController("sys/toUserManager").setViewName("system/user/userManager");
        registry.addViewController("bus/toCustomerManager").setViewName("business/customer/customerManager");
        registry.addViewController("bus/toProviderManager").setViewName("business/provider/providerManager");
        registry.addViewController("bus/toGoodsManager").setViewName("business/goods/goodsManager");
        registry.addViewController("bus/toInportManager").setViewName("business/inport/inportManager");//进货管理
        registry.addViewController("bus/toOutportManager").setViewName("business/outport/outportManager");//销售管理
        registry.addViewController("bus/toSalesManager").setViewName("business/sales/salesManager");//商品销售
        registry.addViewController("bus/toSalesbackManager").setViewName("business/salesback/salesbackManager");//销售退货
        registry.addViewController("sys/toCacheManager").setViewName("system/cache/cacheManager");
        registry.addViewController("leaveBill/toLeaveBillManager").setViewName("system/leavebill/leavebillManager");//请假单管理
        //registry.addViewController("workFlow/toWorkFlowManager").setViewName("system/workFlow/workFlowManager");//流程管理页面
        registry.addViewController("workFlow/toWorkFlowDef").setViewName("system/workFlow/processDefManager");//流程定义
        registry.addViewController("workFlow/toWorkFlowDeploy").setViewName("system/workFlow/processDeployManager");//流程部署
        registry.addViewController("workFlow/toWorkFlowModel").setViewName("system/workFlow/processModelManager");//流程模型管理页面
        registry.addViewController("workFlow/toTaskManager").setViewName("system/workFlow/taskManager");//待办任务管理页面
        registry.addViewController("workFlow/toHisTaskManager").setViewName("system/workFlow/hisTaskManager");//已办任务管理页面
        registry.addViewController("editor").setViewName("activiti/modeler");//流程模型页面
    }
}
