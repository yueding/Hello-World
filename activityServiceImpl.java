package com.aojiaodage.crm.workbench.web.controller;

import com.aojiaodage.crm.common.vo.PaginationVO;
import com.aojiaodage.crm.settings.domain.User;
import com.aojiaodage.crm.util.JsonUtil;
import com.aojiaodage.crm.util.ParameterUtil;
import com.aojiaodage.crm.util.ProxyUtil;
import com.aojiaodage.crm.workbench.domain.Activity;
import com.aojiaodage.crm.workbench.domain.ActivityRemark;
import com.aojiaodage.crm.workbench.service.ActivityService;
import com.aojiaodage.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ActivityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/workbench/activities.do":
                getActivities(req, resp);
                break;
            case "/workbench/activity.do":
                toActivityDetail(req, resp);
                break;
            case "/workbench/activity/remarks.do":
                getActivityRemarks(req, resp);
                break;
            case "/workbench/associatedActivities.do":
                getAssociatedActivities(req, resp);
                break;
            case "/workbench/unassociatedActivities.do":
                getUnassociatedActivities(req, resp);
                break;
            case "/workbench/byName/activities.do":
                getActivitiesByName(req, resp);
                break;
            default:
                JsonUtil.printJson(resp, 404, -1, "未匹配到路径：" + path);
        }
    }

    private void getActivitiesByName(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String name = req.getParameter("name");
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            List<Activity> activities = activityService.getActivitiesByName(name);
            JsonUtil.printJson(resp, activities);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }

	add func3
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/workbench/activity/save.do":
                save(req, resp);
                break;
            case "/workbench/activity/edit.do":
                editActivity(req, resp);
                break;
            case "/workbench/activity/delete.do":
                deleteActivity(req, resp);
                break;
            case "/workbench/activity/remark/save.do":
                saveActivityRemark(req, resp);
                break;
            case "/workbench/activity/remark/delete.do":
                deleteActivityRemark(req, resp);
                break;
            case "/workbench/activity/remark/edit.do":
                editActivityRemark(req, resp);
                break;
            default:
                JsonUtil.printJson(resp, 404, -1, "未匹配到路径：" + path);
        }
    }
    // 获取未关联的市场活动
    private void getUnassociatedActivities(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, String> params = ParameterUtil.getParameterMap(req);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            List<Activity> activities = activityService.getUnassociatedActivities(params);
            JsonUtil.printJson(resp, activities);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 获取已关联的市场活动
    private void getAssociatedActivities(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("clueId");
        try {
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            List<Activity> activities = activityService.getAssociatedActivitiesByClueId(id);
            JsonUtil.printJson(resp, activities);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 获取活动备注
    private void getActivityRemarks(HttpServletRequest req, HttpServletResponse resp) {
        ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
        String id = req.getParameter("id");
        try {
            List<ActivityRemark> activityRemarks = activityService.getActivityRemarks(id);
            JsonUtil.printJson(resp, activityRemarks);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 编辑活动备注
    private void editActivityRemark(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ActivityRemark activityRemark = JsonUtil.parseJson(req.getInputStream(), ActivityRemark.class);
            String editBy = ((User) req.getSession().getAttribute("user")).getName();
            activityRemark.setEditBy(editBy);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            activityService.editActivityRemark(activityRemark);
            JsonUtil.printJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 删除活动备注
    private void deleteActivityRemark(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ActivityRemark activityRemark = JsonUtil.parseJson(req.getInputStream(), ActivityRemark.class);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            activityService.deleteActivityRemarkById(activityRemark.getId());
            JsonUtil.printJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 保存活动备注
    private void saveActivityRemark(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ActivityRemark activityRemark = JsonUtil.parseJson(req.getInputStream(), ActivityRemark.class);
            // 创建人就是当前登陆的用户
            String createBy = ((User) req.getSession().getAttribute("user")).getName();
            // params.put("createBy", createBy);
            activityRemark.setCreateBy(createBy);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            activityService.saveActivityRemark(activityRemark);
            JsonUtil.printJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 删除活动
    private void deleteActivity(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Activity activity = JsonUtil.parseJson(req.getInputStream(), Activity.class);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            activityService.deleteActivityById(activity.getId());
            JsonUtil.printJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 编辑活动
    private void editActivity(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Activity activity = JsonUtil.parseJson(req.getInputStream(), Activity.class);
            // 设置修改人
            String editBy = ((User) req.getSession().getAttribute("user")).getName();
            // params.put("editBy", editBy);
            activity.setEditBy(editBy);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            activityService.editActivity(activity);
            JsonUtil.printJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 保存活动
    private void save(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Activity activity = JsonUtil.parseJson(req.getInputStream(), Activity.class);
            // 创建人就是当前登陆的用户
            String createBy = ((User) req.getSession().getAttribute("user")).getName();
            activity.setCreateBy(createBy);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            activityService.saveActivity(activity);
            JsonUtil.printJson(resp);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 查询活动【分页】
    private void getActivities(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, String> query = ParameterUtil.getParameterMap(req);
            ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
            PaginationVO<Activity> paginationVO = activityService.getActivities(query);
            JsonUtil.printJson(resp, paginationVO);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.printJson(resp, 400, -1, e.getMessage());
        }
    }
    // 转发到活动详情页面
    private void toActivityDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        ActivityService activityService = (ActivityService) ProxyUtil.getServiceProxy(new ActivityServiceImpl());
        Activity activity = activityService.getActivity(id);
        req.setAttribute("activity", activity);
        req.getRequestDispatcher("/workbench/activity/detail.jsp").forward(req, resp);
    }
}
