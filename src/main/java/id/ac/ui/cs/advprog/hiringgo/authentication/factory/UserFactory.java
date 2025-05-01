package id.ac.ui.cs.advprog.hiringgo.authentication.factory;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;

public class UserFactory {
    public static User createUser(UserRoleEnums role, String email, String password, String fullName, String nimOrNip) {
        switch (role) {
            case MAHASISWA:
                return new Mahasiswa(email, password, fullName, nimOrNip);
            case DOSEN:
                return new Dosen(email, password, fullName, nimOrNip);
            case ADMIN:
                return new Admin(email, password);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}