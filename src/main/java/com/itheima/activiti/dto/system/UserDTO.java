package com.itheima.activiti.dto.system;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable {

    private Long id;
    private Long createUser;
    private Long updateUser;
    private Long superior;
    private String account;
    private String name;
    private Long orgId;
    private String orgName;
    private Long stationId;
    private String stationName;
    private String email;
    private String mobile;
    private boolean status;
    private String avatar;
    private List<Long> roles;

    private String applicationId;

}
