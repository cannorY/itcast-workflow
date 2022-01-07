package com.itheima.activiti.dto.modeler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("模型")
public class ModelDTO {

    @ApiModelProperty("模型id")
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("标识")
    private String key;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date lastUpdateTime;

    @ApiModelProperty("流程版本")       //流程定义的版本
    private Integer version;

    @ApiModelProperty("模型内容")
    private String content;
	
	private String processDefinitionId; //最新的流程定义的Id

    private boolean processDefinitionSuspended;//最新的流程定义的状态
}