package com.itheima.activiti.dto.system;

import com.itheima.authority.api.v1.dto.RouterMeta;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuDTO implements Serializable {
    private static final long serialVersionUID = -3327478146308500708L;
    private Long id;
    private Long parentId;
    @ApiModelProperty("路径")
    private String path;
    @ApiModelProperty("菜单名称")
    private String name;
    @ApiModelProperty("组件")
    private String component;
    @ApiModelProperty("重定向")
    private String redirect;
    @ApiModelProperty("元数据")
    private RouterMeta meta;
    @ApiModelProperty("是否隐藏")
    private Boolean hidden = false;
    @ApiModelProperty("总是显示")
    private Boolean alwaysShow = false;
    @ApiModelProperty("子路由")
    private List<MenuDTO> children;

    public String getComponent() {
        return this.getChildren() != null && !this.getChildren().isEmpty() ? "Layout" : this.component;
    }
}
