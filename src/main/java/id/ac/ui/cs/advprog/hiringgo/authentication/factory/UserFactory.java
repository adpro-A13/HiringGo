package id.ac.ui.cs.advprog.hiringgo.authentication.factory;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;

public class UserFactory {    public static User createUser(UserRoleEnums role, String email, String password, String fullName, String nimOrNip, java.util.UUID id) {
        String safeIdentifier = (nimOrNip != null) ? nimOrNip : "";
        System.out.println("UserFactory creating " + role + " with identifier: " + safeIdentifier);
        User user;
        switch (role) {
            case MAHASISWA:
                user = new Mahasiswa(email, password, fullName, safeIdentifier);
                break;
            case DOSEN:
                user = new Dosen(email, password, fullName, safeIdentifier);
                break;
            case ADMIN:
                user = new Admin(email, password);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
        if (id != null) {
            user.setId(id);
        } else if (user.getId() == null) {
            user.setId(java.util.UUID.randomUUID());
        }
        return user;
    }

    public static User createUser(UserRoleEnums role, String email, String password, String fullName, String nimOrNip) {
        return createUser(role, email, password, fullName, nimOrNip, null);
    }
}