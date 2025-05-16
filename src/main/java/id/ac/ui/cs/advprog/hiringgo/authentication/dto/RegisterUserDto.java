package id.ac.ui.cs.advprog.hiringgo.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String nim;
}
