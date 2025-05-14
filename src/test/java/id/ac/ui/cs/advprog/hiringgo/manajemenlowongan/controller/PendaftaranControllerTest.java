package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PendaftaranControllerTest {

    @Mock
    private LowonganService lowonganService;

    @Mock
    private PendaftaranService pendaftaranService;

    @Mock
    private JwtService jwtService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Mahasiswa mahasiswa;

    @InjectMocks
    private PendaftaranController pendaftaranController;

    private UUID lowonganId;
    private Lowongan lowongan;
    private DaftarForm daftarForm;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        lowonganId = UUID.randomUUID();
        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        MataKuliah mataKuliah = new MataKuliah("IF3270", "TBA", "metal gear rising");
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setJumlahAsdosDibutuhkan(3);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);

        daftarForm = new DaftarForm();
        daftarForm.setIpk(3.5);
        daftarForm.setSks(100);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(mahasiswa);
        when(mahasiswa.getId()).thenReturn(UUID.randomUUID());

    }

    @Test
    public void testShowDaftarFormWithAuthentication() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        String viewName = pendaftaranController.showDaftarForm(lowonganId, model, authentication, redirectAttributes);

        assertEquals("daftar", viewName);
        verify(model).addAttribute(eq("lowongan"), eq(lowongan));
        verify(model).addAttribute(eq("kandidat"), eq(mahasiswa));
        verify(model).addAttribute(eq("daftarForm"), any(DaftarForm.class));
        verify(model).addAttribute(eq("prasyaratList"), anyList());
    }

    @Test
    public void testShowDaftarFormWithoutAuthentication() {
        String viewName = pendaftaranController.showDaftarForm(lowonganId, model, null, redirectAttributes);

        assertEquals("redirect:/auth/login", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    public void testShowDaftarFormLowonganNotFound() {
        when(lowonganService.findById(lowonganId)).thenThrow(new NoSuchElementException());

        String viewName = pendaftaranController.showDaftarForm(lowonganId, model, authentication, redirectAttributes);

        assertEquals("redirect:/lowongan/list", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    public void testHandleDaftarSuccess() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(pendaftaranService.daftar(eq(lowonganId), any(Mahasiswa.class), BigDecimal.valueOf(eq(3.5)), eq(100)))
                .thenReturn(new Pendaftaran());

        String viewName = pendaftaranController.handleDaftar(
                lowonganId, daftarForm, bindingResult, model, authentication, redirectAttributes);

        assertEquals("redirect:/lowongan/" + lowonganId, viewName);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    public void testHandleDaftarWithValidationErrors() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = pendaftaranController.handleDaftar(
                lowonganId, daftarForm, bindingResult, model, authentication, redirectAttributes);

        assertEquals("daftar", viewName);
        verify(model).addAttribute(eq("lowongan"), eq(lowongan));
        verify(model).addAttribute(eq("kandidat"), eq(mahasiswa));
        verify(model).addAttribute(eq("prasyaratList"), anyList());
    }

    @Test
    public void testHandleDaftarServiceException() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(pendaftaranService.daftar(
                eq(lowonganId),
                any(Mahasiswa.class),
                eq(BigDecimal.valueOf(3.5)),
                eq(100)))
                .thenThrow(new IllegalStateException("Lowongan sudah ditutup"));

        String viewName = pendaftaranController.handleDaftar(
                lowonganId, daftarForm, bindingResult, model, authentication, redirectAttributes);

        assertEquals("redirect:/lowongan/" + lowonganId + "/daftar", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    public void testShowLowonganDetail() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        String viewName = pendaftaranController.showLowonganDetail(
                lowonganId, model, authentication, redirectAttributes);

        assertEquals("lowonganDetail", viewName);
        verify(model).addAttribute(eq("lowongan"), eq(lowongan));
        verify(model).addAttribute(eq("kandidat"), eq(mahasiswa));
    }

    @Test
    public void testShowLowonganDetailNotFound() {
        when(lowonganService.findById(lowonganId)).thenThrow(new NoSuchElementException());

        String viewName = pendaftaranController.showLowonganDetail(
                lowonganId, model, authentication, redirectAttributes);

        assertEquals("redirect:/lowongan/list", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }
}