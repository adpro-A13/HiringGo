package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model;

public enum UserRole {
    ADMIN,
    DOSEN,
    MAHASISWA;

    @Override
    public String toString() {
        return this.name();
    }
}