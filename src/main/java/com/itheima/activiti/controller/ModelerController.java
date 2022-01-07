package com.itheima.activiti.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.activiti.common.Result;
import com.itheima.activiti.dto.modeler.ModelDTO;
import com.itheima.activiti.service.ModelerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
@Slf4j
@RestController
@RequestMapping("/modeler")
@Api("模型管理")
public class ModelerController {
    @Resource
    ModelerService modelerService;


    @PostMapping
    @ApiOperation(value = "新增模型")
    public Result save(@RequestBody ModelDTO modelDTO) {
        String save = modelerService.save(modelDTO);
        modelDTO.setId(save);
        return Result.success(modelDTO);
    }

    @GetMapping("/page")
    public Result<Page<ModelDTO>> page(ModelDTO modelDTO,
                                       @RequestParam(required = false,defaultValue = "1")int page,
                                       @RequestParam(required = false,defaultValue = "10")int pageSize) {
      Page<ModelDTO> list=  modelerService.page(modelDTO,page,pageSize);
      return  Result.success(list);
    }

    @DeleteMapping
    public Result deleteModel(String id) {
        modelerService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("xml")
    public Result<ModelDTO> savexml(@RequestBody ModelDTO modelDTO) {
        modelerService.saveXml(modelDTO);
        return  Result.success(modelDTO);
    }

    /**
     * 查询流程图的内容
     * @param id
     * @return
     */
    @GetMapping("/xml/{id}")
    @ApiOperation(value = "查询流程图")
    public Result getXmlById(@PathVariable String id){
        String content = modelerService.getXmlById(id);
        return Result.success(content);
    }
}
