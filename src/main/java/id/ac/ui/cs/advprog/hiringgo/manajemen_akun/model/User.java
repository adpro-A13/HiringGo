package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class User {
    protected String email;
    protected String password;
    protected String role;
}
