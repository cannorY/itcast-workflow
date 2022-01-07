package com.itheima.activiti.service.serviceimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itheima.activiti.dto.modeler.ModelDTO;
import com.itheima.activiti.service.ModelerService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 18122
 */
@Service
public class ModelerServiceImpl implements ModelerService {

    @Resource
    RepositoryService repositoryService;

    @Resource
    ObjectMapper objectMapper;

    @Override
    public String save(ModelDTO modelDTO) {
        //创建activiti模型
        Model model = repositoryService.newModel();

        //赋值
        model.setKey(modelDTO.getKey());
        model.setName(modelDTO.getName());

        //创建model的mateinfo(json格式)

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("name", modelDTO.getName());
        objectNode.put("description", modelDTO.getDescription());
        objectNode.put("reversion", modelDTO.getVersion());
        //赋值mateinfo
        model.setMetaInfo(objectNode.toPrettyString());
        //保存模型
        repositoryService.saveModel(model);

        return model.getId();
    }

    @Override
    public Page<ModelDTO> page(ModelDTO modelDTO, int page, int pageSize) {
        //从前端传入对象中，获取key和name
        String key = modelDTO.getKey();
        String name = modelDTO.getName();

        //创建模型的查询对象
        ModelQuery modelQuery = repositoryService.createModelQuery()
                .orderByModelName().desc();

        //设置查询条件
        if(StringUtils.isNotEmpty(key)){
            modelQuery.modelKey(key);
        }
        if(StringUtils.isNotEmpty(name)){
            modelQuery.modelName(name);
        }

        //返回查询数量
        long total = modelQuery.count();

        //返回查询结果
        List<Model> list = modelQuery.listPage((page - 1) * pageSize, pageSize);

        //定义分页对象
        Page<ModelDTO> pageResult = new Page(page, pageSize, total);

        //遍历查询结果，返回ModelDTO
        List<ModelDTO> modelDTOList = list.stream().map(item -> {
            ModelDTO dto = new ModelDTO();
            BeanUtils.copyProperties(item, dto);

            //设置流程定义的属性
            //获取流程定义
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(item.getKey())
                    .latestVersion().singleResult();
            if(processDefinition == null){
                dto.setVersion(null);
            }
            else{
                dto.setVersion(processDefinition.getVersion()); //当前部署的流程的版本
                dto.setProcessDefinitionId(processDefinition.getId());//流程定义的id
                dto.setProcessDefinitionSuspended(processDefinition.isSuspended());//流程的状态
            }

            return dto;
        }).collect(Collectors.toList());

        pageResult.setRecords(modelDTOList);

        return pageResult;
    }

    @Override
    public void delete(String id) {
        repositoryService.deleteModel(id);
    }

    @Override
    public void saveXml(ModelDTO modelDTO) {
        //保存模型
        if (StringUtils.isEmpty(modelDTO.getId())) {
            modelDTO.setId(this.save(modelDTO));
        }
        //保存流程图
        try {
            repositoryService.addModelEditorSource(modelDTO.getId(),modelDTO.getContent().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getXmlById(String id) {
        byte[] content = repositoryService.getModelEditorSource(id);
        //新建时内容为空
        if(content == null){
            return "";
        }
        else{
            return new String(content);
        }
    }
}