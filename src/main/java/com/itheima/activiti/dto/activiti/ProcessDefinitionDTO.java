package com.itheima.activiti.dto.activiti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("流程定义")
public class ProcessDefinitionDTO {
    @ApiModelProperty("流程定义Id")
    private String id;
    @ApiModelProperty("流程定义名称")
    private String name;
    @ApiModelProperty("流程定义的key")
    private String key;
    @ApiModelProperty("流程定义的版本")
    private Integer version;
    @ApiModelProperty("资源名称bpmn文件")
    private String resourceName;
    @ApiModelProperty("资源名称png文件")
    private String diagramResourceName;
    @ApiModelProperty("部署对象ID")
    private String deploymentId;
    @ApiModelProperty("部署时间")
    private LocalDateTime deploymentTime;
    @ApiModelProperty("挂起状态")
    private Boolean suspended;
}
