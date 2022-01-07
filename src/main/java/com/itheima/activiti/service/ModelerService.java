package com.itheima.activiti.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.activiti.dto.modeler.ModelDTO;

/**
 * @author 18122
 */
public interface ModelerService {
    /**
     * 保存模型
     * @param modelDTO
     * @return
     */
    String save(ModelDTO modelDTO);
    /**
     * 分页查询页面设计的模型
     * @param modelDTO
     * @param page
     * @param pageSize
     * @return
     */
    Page<ModelDTO> page(ModelDTO modelDTO, int page, int pageSize);

    /**
     * id删除
     * @param id
     */
    void delete(String id);

    /**
     * 保存流程图
     * @param modelDTO
     */
    void saveXml(ModelDTO modelDTO);

    /**
     * 根据模型的id获取流程图
     */
    String getXmlById(String id);
}