package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {
    
    @Test
    void testAdminCreation() {
        String email = "admin@example.com";
        String password = "adminpass";
        
        Admin admin = new Admin(email, password);
        
        assertEquals(email, admin.getEmail());
        assertEquals(password, admin.getPassword());
        assertEquals(UserRole.ADMIN, admin.getRole());
    }
    
    @Test
    void testDosenCreation() {
        String nip = "12345678";
        String name = "Chris";
        String email = "chris@example.com";
        String password = "whoami";
        
        Dosen dosen = new Dosen(nip, name, email, password);
        
        assertEquals(nip, dosen.getNip());
        assertEquals(name, dosen.getName());
        assertEquals(email, dosen.getEmail());
        assertEquals(password, dosen.getPassword());
        assertEquals(UserRole.DOSEN, dosen.getRole());
    }
    
    @Test
    void testMahasiswaCreation() {
        String name = "bokbok";
        String nim = "2101234567";
        String email = "bokbok@example.com";
        String password = "bokbokismyname";
        
        Mahasiswa mahasiswa = new Mahasiswa(name, nim, email, password);
        
        assertEquals(name, mahasiswa.getName());
        assertEquals(nim, mahasiswa.getNim());
        assertEquals(email, mahasiswa.getEmail());
        assertEquals(password, mahasiswa.getPassword());
        assertEquals(UserRole.MAHASISWA, mahasiswa.getRole());
    }
    
    @Test
    void testSettersAndGetters() {
        Admin admin = new Admin("admin@example.com", "oldpass");
        admin.setPassword("newpass");
        assertEquals("newpass", admin.getPassword());
        
        Dosen dosen = new Dosen("12345", "Old Name", "dosen@example.com", "oldpass");
        dosen.setName("New Name");
        dosen.setNip("67890");
        dosen.setPassword("newpass");
        
        assertEquals("New Name", dosen.getName());
        assertEquals("67890", dosen.getNip());
        assertEquals("newpass", dosen.getPassword());
        
        Mahasiswa mahasiswa = new Mahasiswa("Old Name", "12345", "mhs@example.com", "oldpass");
        mahasiswa.setName("New Name");
        mahasiswa.setNim("67890");
        mahasiswa.setPassword("newpass");
        
        assertEquals("New Name", mahasiswa.getName());
        assertEquals("67890", mahasiswa.getNim());
        assertEquals("newpass", mahasiswa.getPassword());
    }
}