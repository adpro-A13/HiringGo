package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PendaftaranControllerTest {

    private LowonganService lowonganService;
    private PendaftaranService pendaftaranService;
    private PendaftaranController controller;
    private Model model;
    private Principal principal;
    private UUID lowonganId;
    private Lowongan lowongan;

    @BeforeEach
    void setUp() {
        lowonganService = mock(LowonganService.class);
        pendaftaranService = mock(PendaftaranService.class);
        controller = new PendaftaranController(lowonganService, pendaftaranService);
        model = new ExtendedModelMap();
        principal = () -> "user123";  // Principal dengan nama user123
        lowonganId = UUID.randomUUID();
        // Siapkan dummy Lowongan
        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setIdMataKuliah("IF1234");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setSemester(Semester.GANJIL.getValue());
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        lowongan.setJumlahAsdosDibutuhkan(5);
        lowongan.setJumlahAsdosPendaftar(2);
    }

    @Test
    void testShowDaftarForm() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        String viewName = controller.showDaftarForm(lowonganId, model, principal);

        assertEquals("daftar", viewName);
        // Model harus berisi lowongan, daftarForm, prasyaratList, dan kandidatId
        assertTrue(model.containsAttribute("lowongan"));
        assertTrue(model.containsAttribute("daftarForm"));
        assertTrue(model.containsAttribute("prasyaratList"));
        assertTrue(model.containsAttribute("kandidatId"));
        // Data lowongan pada model sesuai dengan dummy lowongan
        Lowongan modelLowongan = (Lowongan) model.getAttribute("lowongan");
        assertEquals(lowonganId, modelLowongan.getLowonganId());
        assertEquals("IF1234", modelLowongan.getIdMataKuliah());
        // Objek form baru harus ada dan belum terisi
        DaftarForm form = (DaftarForm) model.getAttribute("daftarForm");
        assertNotNull(form);
        assertNull(form.getIpk());
        assertNull(form.getSks());
        // Daftar dummy prasyarat tersedia
        assertNotNull(model.getAttribute("prasyaratList"));
        // KandidatId sesuai principal
        assertEquals("user123", model.getAttribute("kandidatId"));
        // Verifikasi lowonganService dipanggil
        verify(lowonganService).findById(lowonganId);
    }

    @Test
    void testHandleDaftarSuccess() {
        DaftarForm form = new DaftarForm();
        form.setIpk(new BigDecimal("3.5"));
        form.setSks(100);
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "daftarForm");

        // Siapkan dummy Pendaftaran untuk return
        Pendaftaran dummy = new Pendaftaran();
        when(pendaftaranService.daftar(lowonganId, "user123", form.getIpk(), form.getSks()))
                .thenReturn(dummy);

        String viewName = controller.handleDaftar(lowonganId, form, bindingResult, model, principal);

        assertTrue(viewName.startsWith("redirect:/lowongan/" + lowonganId));
        verify(pendaftaranService).daftar(lowonganId, "user123", form.getIpk(), form.getSks());
    }

    @Test
    void testHandleDaftarWithErrors() {
        DaftarForm form = new DaftarForm();
        form.setIpk(null);
        form.setSks(100);
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "daftarForm");
        // Simulasikan error pada field ipk
        bindingResult.rejectValue("ipk", "NotNull", "IPK tidak boleh kosong");
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        String viewName = controller.handleDaftar(lowonganId, form, bindingResult, model, principal);

        // Harus kembali ke view "daftar" jika ada error
        assertEquals("daftar", viewName);
        // Model tetap berisi data lowongan, prasyarat, dsb. untuk ditampilkan ulang
        assertTrue(model.containsAttribute("lowongan"));
        assertTrue(model.containsAttribute("prasyaratList"));
        // Service tidak boleh dipanggil jika terjadi error validasi
        verify(pendaftaranService, never()).daftar(any(UUID.class), anyString(), any(BigDecimal.class), anyInt());
    }
}
