package com.itheima.activiti.dto.system;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(
        value = "Role",
        description = "角色"
)
@Data
public class RoleDTO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("角色名称")
    @NotEmpty(
            message = "角色名称不能为空"
    )
    @Length(
            max = 30,
            message = "角色名称长度不能超过30"
    )
    private String name;
    @ApiModelProperty("角色编码")
    @Length(
            max = 20,
            message = "角色编码长度不能超过20"
    )
    private String code;
    @ApiModelProperty("功能描述")
    @Length(
            max = 100,
            message = "功能描述长度不能超过100"
    )
    private String describe;
    @ApiModelProperty("状态")
    private Boolean status;
    @ApiModelProperty("是否内置角色")
    private Boolean readonly;
    @ApiModelProperty("数据权限类型")
    @NotNull(
            message = "数据权限类型不能为空"
    )
    private String dsType;
    @ApiModelProperty("互斥角色")
    @NotNull(
            message = "互斥角色不能为空"
    )
    private Long repel;
}
