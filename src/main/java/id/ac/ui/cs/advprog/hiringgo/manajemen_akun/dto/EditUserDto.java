package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserDto {
    private String newRole;
    private String fullName;
    private String password;
    private String identifier;
    private String email;
}
