package com.itheima.activiti.auth;

import com.itheima.activiti.dto.system.UserDTO;

/**
 * ThreadLocal中设置用户
 */
public class JwtContextUtils {
    private static final ThreadLocal<UserDTO> JWT_USERDTO = new ThreadLocal<>();

    public static void setUser(UserDTO userDTO) {
        JWT_USERDTO.set(userDTO);
    }

    public static UserDTO getUser() {
        return JWT_USERDTO.get();
    }
}
