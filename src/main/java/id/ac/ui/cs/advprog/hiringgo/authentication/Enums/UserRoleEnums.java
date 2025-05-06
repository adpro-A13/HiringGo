package id.ac.ui.cs.advprog.hiringgo.authentication.Enums;

public enum UserRoleEnums {
    ADMIN,
    DOSEN,
    MAHASISWA;

    public String getValue() {
        return this.name();
    }
    
    public static UserRoleEnums fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return UserRoleEnums.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}