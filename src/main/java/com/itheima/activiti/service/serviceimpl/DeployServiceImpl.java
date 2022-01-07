package com.itheima.activiti.service.serviceimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.activiti.dragram.ActivitiProcessDiagramGenerator;
import com.itheima.activiti.dto.activiti.ProcessDefinitionDTO;
import com.itheima.activiti.service.DeployService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeployServiceImpl implements DeployService {
    @Resource
    private RepositoryService repositoryService;

    @Override
    public void deploy(String id) {
        //根据模型id获取模型对象
        Model model = repositoryService.getModel(id);
        //根据模型id获取模型资源
        byte[] modelEditorSource = repositoryService.getModelEditorSource(id);

        //部署模型
        repositoryService.createDeployment()
                .key(model.getKey())
                .name(model.getName())
                .addString(model.getName() + "bpmn20.xml", new String(modelEditorSource))
                .deploy();
    }

    @Override
    public Page<ProcessDefinitionDTO> page(ProcessDefinitionDTO pdDTO, int page, int pageSize) {
        //创建流程定义的查询对象
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

        //设置查询条件：key、version
        if (StringUtils.isNotBlank(pdDTO.getKey())) {
            query.processDefinitionKey(pdDTO.getKey());
        }
        if (null != pdDTO.getVersion()) {
            query.processDefinitionVersion(pdDTO.getVersion());
        }

        //分页查询流程定义
        List<ProcessDefinition> list = query.listPage((page - 1) * pageSize, pageSize);

        //返回分页的流程定义dto:复制对象属性、设置部署时间
        List<ProcessDefinitionDTO> pdDtoList = null;
        if (null != list && list.size() > 0) {
            //将流程定义转换成dto对象
            pdDtoList = list.stream().map(item -> {
                ProcessDefinitionDTO dto = new ProcessDefinitionDTO();
                //复制对象属性
                BeanUtils.copyProperties(item, dto);

                //查询此流程定义的部署时间
                Deployment deployment = repositoryService.createDeploymentQuery()
                        .deploymentId(item.getDeploymentId())
                        .singleResult();

                //设置dto的部署时间
                dto.setDeploymentTime(LocalDateTime.ofInstant(deployment.getDeploymentTime().toInstant(), ZoneId.systemDefault()));

                return dto;
            }).collect(Collectors.toList());
        }
        //查询总数
        long total = query.count();

        //分页返回dto
        Page<ProcessDefinitionDTO> pageResult = new Page(page, pageSize, total);
        pageResult.setRecords(pdDtoList);

        return pageResult;
    }

    @Override
    public void removeByIds(List<String> ids) {
        for (int i = 0; i < ids.size(); i++) {
            repositoryService.deleteDeployment(ids.get(i));
        }
    }

    @Override
    public void suspend(String id) {
        repositoryService.suspendProcessDefinitionById(id, true, null);
    }

    @Override
    public void active(String id) {
        repositoryService.activateProcessDefinitionById(id, true, null);
    }

    @Override
    public InputStream getResourceAsStream(String deploymentId, String resourceName) {
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);

        return resourceAsStream;
    }

    @Override
    public InputStream svg(String deploymentId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        ProcessDiagramGenerator diagramGenerator = new ActivitiProcessDiagramGenerator();

        InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, new ArrayList<>());

        return inputStream;
    }
}
