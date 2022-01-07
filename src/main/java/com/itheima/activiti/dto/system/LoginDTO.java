package com.itheima.activiti.dto.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginDTO implements Serializable {
    private String username;
    private String password;
    private String token;
    private UserDTO userDTO;
}
