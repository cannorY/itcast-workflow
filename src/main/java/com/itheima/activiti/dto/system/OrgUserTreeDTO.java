package com.itheima.activiti.dto.system;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@ApiModel(
        value = "OrgUserTreeDTO",
        description = "组织用户树"
)
@Data
public class OrgUserTreeDTO {
    private List<OrgUserTreeDTO> children;
    private String label;
    private Long id;
    private Long parentId;
    private Boolean status;
    private Integer type;

    public Boolean getEnable() {
        return this.type == 1 && CollectionUtils.isEmpty(this.children) ? false : true;
    }
}
