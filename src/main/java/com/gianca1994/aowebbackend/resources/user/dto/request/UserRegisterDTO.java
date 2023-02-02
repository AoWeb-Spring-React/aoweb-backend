package com.gianca1994.aowebbackend.resources.user.dto.request;

import lombok.*;

/**
 * @Author: Gianca1994
 * Explanation: UserDTO
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String className;
}
