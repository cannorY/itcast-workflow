package com.itheima.activiti.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.activiti.dto.activiti.ProcessDefinitionDTO;

import java.io.InputStream;
import java.util.List;

public interface DeployService {
    /**
     * 部署模型
     * @param id
     */
    void deploy(String id);
    /**
     * 分页/搜索流程定义
     * @param processDefinitionDTO
     * @param page
     * @param pageSize
     * @return
     */
    Page<ProcessDefinitionDTO> page(ProcessDefinitionDTO processDefinitionDTO, int page, int pageSize);

    /**
     * 根据部署id，删除流程定义
     * @param ids:部署id的列表
     */
    void removeByIds(List<String> ids);
    /**
     * 挂起
     * @param id
     */
    void suspend(String id);
    /**
     * 激活
     * @param id
     */
    void active(String id);
    /**
     * 获取流程资源，返回数据流
     * @param deploymentId
     * @param resourceName
     * @return
     */
    InputStream getResourceAsStream(String deploymentId, String resourceName);
    /**
     * 获取流程图片
     * @param deploymentId
     * @return
     */
    InputStream svg(String deploymentId);
}
