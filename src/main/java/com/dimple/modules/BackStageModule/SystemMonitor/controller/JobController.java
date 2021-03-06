package com.dimple.modules.BackStageModule.SystemMonitor.controller;

import com.dimple.framework.enums.OperateType;
import com.dimple.framework.log.annotation.Log;
import com.dimple.framework.message.Result;
import com.dimple.framework.message.ResultUtil;
import com.dimple.modules.BackStageModule.SystemMonitor.bean.Job;
import com.dimple.modules.BackStageModule.SystemMonitor.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @className: JobController
 * @description: 任务调度处理
 * @auther: Owenb
 * @date: 01/29/19
 * @version: 1.0
 */
@Controller
@Api("任务调度处理Controller")
public class JobController {

    @Autowired
    JobService jobService;

    @GetMapping("/page/job.html")
    @ApiIgnore
    @RequiresPermissions("page:job:view")
    public String jobListPage() {
        return "monitor/job/job";
    }


    @GetMapping("/page/job/add.html")
    @ApiIgnore
    @RequiresPermissions("page:jobAdd:view")
    public String jobAddPage() {
        return "monitor/job/add";
    }

    @GetMapping("/page/job/{id}.html")
    @ApiIgnore
    @RequiresPermissions("page:jobDetail:view")
    public String jobDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobService.getJobById(id));
        return "monitor/job/detail";
    }

    @RequiresPermissions("page:jobUpdate:view")
    @ApiIgnore
    @GetMapping("/page/job/update/{id}.html")
    public String jobUpdatePage(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobService.getJobById(id));
        return "monitor/job/edit";
    }

    @ApiOperation("新增任务")
    @Log(title = "作业管理", operateType = OperateType.INSERT)
    @RequiresPermissions("SystemMonitor:job:insert")
    @ResponseBody
    @PostMapping("/api/job")
    public Result jobAdd(Job job) {
        jobService.insertJob(job);
        return ResultUtil.success();
    }

    @ApiOperation("获取任务列表")
    @RequiresPermissions("SystemMonitor:job:query")
    @Log(title = "作业管理", operateType = OperateType.SELECT)
    @GetMapping("/api/job")
    @ResponseBody
    public Result jobList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "jobName", required = false) String jobName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "methodName", required = false) String methodName) {
        Pageable pageable = PageRequest.of(pageNum < 0 ? 0 : pageNum, pageSize, Sort.Direction.DESC, "createTime");
        Page<Job> allJob = jobService.getAllJob(pageable, jobName, methodName, status);
        return ResultUtil.success(allJob);
    }


    @ApiOperation("校验cron表达式是否有效")
    @GetMapping("/api/job/cronExprValidate")
    @Log(title = "作业管理", operateType = OperateType.OTHER)
    @ResponseBody
    public Boolean cronExpressionValidate(String expression) {
        return jobService.checkCronExpression(expression);
    }

    @ApiOperation("删除定时任务")
    @DeleteMapping("/api/job/{ids}")
    @RequiresPermissions("SystemMonitor:job:delete")
    @Log(title = "作业管理", operateType = OperateType.DELETE)
    @ResponseBody
    public Result deleteCronJob(@PathVariable Long[] ids) {
        jobService.deleteJobById(ids);
        return ResultUtil.success();
    }

    @ApiOperation("立即运行定时任务")
    @Log(title = "作业管理", operateType = OperateType.OTHER)
    @ResponseBody
    @RequiresPermissions("SystemMonitor:jobRun:update")
    @PostMapping("/api/job/start/{id}")
    public Result startJob(@PathVariable Long id) {
        jobService.run(id);
        return ResultUtil.success();
    }

    @ApiOperation("切换定时任务的状态")
    @ResponseBody
    @RequiresPermissions("SystemMonitor:jobStatus:update")
    @Log(title = "作业管理", operateType = OperateType.CHANGE_STATUS)
    @PostMapping("/api/job/{id}/{status}")
    public Result changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        jobService.changStatus(id, status);
        return ResultUtil.success();
    }

    @ApiOperation("更新任务信息")
    @Log(title = "作业管理", operateType = OperateType.UPDATE)
    @PutMapping("/api/job")
    @RequiresPermissions("SystemMonitor:job:update")
    @ResponseBody
    public Result jobUpdate(Job job) {
        jobService.updateJob(job);
        return ResultUtil.success();
    }
}
