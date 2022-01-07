package com.itheima.activiti.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.activiti.common.Result;
import com.itheima.activiti.dto.activiti.DeployDTO;
import com.itheima.activiti.dto.activiti.ProcessDefinitionDTO;
import com.itheima.activiti.service.DeployService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/deploy")
@Api(tags = "流程部署")
public class DeployController {
    @Resource
    private DeployService deployService;

    @PostMapping
    @ApiOperation(value = "部署", notes = "传入模型id")
    public Result deploy(@RequestBody DeployDTO deployDTO) {
        deployService.deploy(deployDTO.getModelId());
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页")
    public Result<Page<ProcessDefinitionDTO>> page(ProcessDefinitionDTO processDefinitionDTO,
                                                   @RequestParam(required = false, defaultValue = "1") int page,
                                                   @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return Result.success(deployService.page(processDefinitionDTO, page, pageSize));
    }

    @DeleteMapping
    @ApiOperation(value = "删除流程", notes = "流程id")
    public Result delete(@RequestParam List<String> ids) {
        deployService.removeByIds(ids);
        return Result.success("删除成功");
    }

    @PutMapping("suspend/{id}")
    @ApiOperation("挂起流程")
    @ApiImplicitParam(name = "id", value = "流程ID", paramType = "query", dataType = "String")
    public Result suspend(@PathVariable("id") String id) {
        deployService.suspend(id);

        return Result.success();
    }

    @PutMapping("active/{id}")
    @ApiOperation("激活流程")
    @ApiImplicitParam(name = "id", value = "流程ID", paramType = "query", dataType = "String")
    public Result active(@PathVariable("id") String id) {
        deployService.active(id);
        return Result.success();
    }

    @GetMapping(value = "resource")
    @ApiOperation("获取资源文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deploymentId", value = "部署ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "resourceName", value = "资源名称", paramType = "query", dataType = "String")
    })
    public void resource(String deploymentId, String resourceName, @ApiIgnore HttpServletResponse response) throws Exception {
        InputStream resourceAsStream = deployService.getResourceAsStream(deploymentId, resourceName);
        IOUtils.copy(resourceAsStream, response.getOutputStream());
    }
    @GetMapping(value = "svg")
    @ApiOperation("获取图片文件")
    public void svg(String deploymentId,@ApiIgnore HttpServletResponse response) throws Exception {
        InputStream resourceAsStream = deployService.svg(deploymentId);

        response.setHeader("Content-Type", "image/svg+xml");
        response.setHeader("Cache-Control", "no-store, no-cache");

        IOUtils.copy(resourceAsStream, response.getOutputStream());
    }
}