package com.itheima.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class ActivitiTest {
    /**
     * 1、保存模型
     * 2、保存流程图
     * 3、部署模型
     * 4、查询流程定义
     * 5、启动流程
     * 6、执行任务
     * 7、查询历史
     */

    @Autowired
    RepositoryService repositoryService;

    /**
     * 保存模型对象
     * re_model
     */
    @Test
    public void saveModel(){
        //创建activiti模型对象
        Model model = repositoryService.newModel();
        model.setKey("myModelKey");
        model.setName("测试保存ystModel");

        //保存流程模型
        repositoryService.saveModel(model);
    }

    /**
     * 保存流程图
     * ge_bytearray
     */
    @Test
    public void testSaveBpmn() throws IOException {
//        File file = new File("src/main/resources/holiday.bpmn");
        File file = new File("src/main/resources/yst.bpmn20.xml");
        byte[] bytes = FileUtils.readFileToByteArray(file);
        repositoryService.addModelEditorSource("1", bytes);
    }

    /**
     * 部署流程
     */
    @Test
    public void testDeployment(){
        repositoryService.createDeployment()
                .key("mykey")
                .name("简易请假流程1")
//                .addClasspathResource("holiday.bpmn")
                .addClasspathResource("yst.bpmn20.xml")
                .deploy();
    }

    /**
     * 查询流程定义
     * 对于一个流程图，只要key相同，每部署一次将会生成一个流程定义。即一个流程可以有多个流程定义
     * re_processDef
     * re_deployment 部署单元信息
     * ge_bytearray 通用的流程定义和流程资源
     */
    @Test
    public void testProcessDef(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("yst")
                .list();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getId());
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("yst")
                .latestVersion().singleResult();
        System.out.println("最新版"+processDefinition.getId());
    }

    /**
     * 启动实例
     * execution
     */
    @Autowired
    private RuntimeService runtimeService;
    @Test
    public void testStart(){
        runtimeService.startProcessInstanceByKey("yst");
    }

    /**
     * 执行任务
     */
    @Autowired
    private TaskService taskService;
    @Test
    public void testComplete(){
        taskService.complete("17502");
    }

    /**
     * 查询流程历史
     *
     */
    @Autowired
    HistoryService historyService;
    @Test
    public void testHistory(){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myProcess_1")
                .latestVersion()
                .singleResult();

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId(processDefinition.getId()).list();

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getActivityName());

        }
    }

}
