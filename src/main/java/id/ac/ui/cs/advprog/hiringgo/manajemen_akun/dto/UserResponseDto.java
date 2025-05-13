package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String id;
    private String email;
    private String role;
    private String fullName;
    private String nim;
    private String nip;
}