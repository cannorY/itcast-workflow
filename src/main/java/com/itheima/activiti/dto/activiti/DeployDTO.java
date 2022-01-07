package com.itheima.activiti.dto.activiti;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("部署流程")
public class DeployDTO {

    @ApiModelProperty("模型主键")
    private String modelId;
}
