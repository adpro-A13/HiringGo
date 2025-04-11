package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.factory;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {
    
    private UserFactory userFactory;
    
    @BeforeEach
    void setUp() {
        userFactory = new UserFactory();
    }
    
    @Test
    void testCreateAdmin() {
        String email = "admin@example.com";
        String password = "adminpass";
        
        User admin = userFactory.createAdmin(email, password);
        
        assertNotNull(admin);
        assertTrue(admin instanceof Admin);
        assertEquals(email, admin.getEmail());
        assertEquals(password, admin.getPassword());
        assertEquals(UserRole.ADMIN, admin.getRole());
    }
    
    @Test
    void testCreateDosen() {
        String nip = "12345678";
        String name = "Chris";
        String email = "chris@example.com";
        String password = "whoami";
        
        User dosen = userFactory.createDosen(nip, name, email, password);
        
        assertNotNull(dosen);
        assertTrue(dosen instanceof Dosen);
        Dosen castedDosen = (Dosen) dosen;
        
        assertEquals(nip, castedDosen.getNip());
        assertEquals(name, castedDosen.getName());
        assertEquals(email, castedDosen.getEmail());
        assertEquals(password, castedDosen.getPassword());
        assertEquals(UserRole.DOSEN, castedDosen.getRole());
    }
    
    @Test
    void testCreateMahasiswa() {
        String name = "bokbok";
        String nim = "2101234567";
        String email = "bokbok@example.com";
        String password = "bokbokismyname";
        
        User mahasiswa = userFactory.createMahasiswa(name, nim, email, password);
        
        assertNotNull(mahasiswa);
        assertTrue(mahasiswa instanceof Mahasiswa);
        Mahasiswa castedMahasiswa = (Mahasiswa) mahasiswa;
        
        assertEquals(name, castedMahasiswa.getName());
        assertEquals(nim, castedMahasiswa.getNim());
        assertEquals(email, castedMahasiswa.getEmail());
        assertEquals(password, castedMahasiswa.getPassword());
        assertEquals(UserRole.MAHASISWA, castedMahasiswa.getRole());
    }
    
    @Test
    void testConvertToAdmin() {
        User dosen = userFactory.createDosen("12345", "John", "john@example.com", "pass123");
        
        User admin = userFactory.convertToAdmin(dosen);
        
        assertNotNull(admin);
        assertTrue(admin instanceof Admin);
        assertEquals("john@example.com", admin.getEmail());
        assertEquals("pass123", admin.getPassword());
        assertEquals(UserRole.ADMIN, admin.getRole());
    }
    
    @Test
    void testConvertToDosen() {
        User admin = userFactory.createAdmin("admin@example.com", "adminpass");
        String nip = "12345";
        String name = "John Doe";
        
        User dosen = userFactory.convertToDosen(admin, nip, name);
        
        assertNotNull(dosen);
        assertTrue(dosen instanceof Dosen);
        Dosen castedDosen = (Dosen) dosen;
        
        assertEquals(nip, castedDosen.getNip());
        assertEquals(name, castedDosen.getName());
        assertEquals("admin@example.com", castedDosen.getEmail());
        assertEquals("adminpass", castedDosen.getPassword());
        assertEquals(UserRole.DOSEN, castedDosen.getRole());
    }
    
    @Test
    void testConvertToMahasiswa() {
        User admin = userFactory.createAdmin("admin@example.com", "adminpass");
        String nim = "2101234567";
        String name = "John Student";
        
        User mahasiswa = userFactory.convertToMahasiswa(admin, nim, name);
        
        assertNotNull(mahasiswa);
        assertTrue(mahasiswa instanceof Mahasiswa);
        Mahasiswa castedMahasiswa = (Mahasiswa) mahasiswa;
        
        assertEquals(nim, castedMahasiswa.getNim());
        assertEquals(name, castedMahasiswa.getName());
        assertEquals("admin@example.com", castedMahasiswa.getEmail());
        assertEquals("adminpass", castedMahasiswa.getPassword());
        assertEquals(UserRole.MAHASISWA, castedMahasiswa.getRole());
    }
}