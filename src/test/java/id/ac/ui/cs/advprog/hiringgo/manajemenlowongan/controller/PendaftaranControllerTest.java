package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

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
        lowongan.setIdMataKuliah("IF3270");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setJumlahAsdosDibutuhkan(3);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
        lowongan.setIdAsdosDiterima(new ArrayList<>());

        daftarForm = new DaftarForm();
        daftarForm.setIpk(3.5);
        daftarForm.setSks(100);

        when(principal.getName()).thenReturn("mahasiswa1");
    }

    @Test
    public void testShowDaftarFormWithAuthentication() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        String viewName = pendaftaranController.showDaftarForm(lowonganId, model, principal, redirectAttributes);

        assertEquals("daftar", viewName);
        verify(model).addAttribute(eq("lowongan"), eq(lowongan));
        verify(model).addAttribute(eq("kandidatId"), eq("mahasiswa1"));
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

        String viewName = pendaftaranController.showDaftarForm(lowonganId, model, principal, redirectAttributes);

        assertEquals("redirect:/lowongan/list", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    public void testHandleDaftarSuccess() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(pendaftaranService.daftar(eq(lowonganId), eq("mahasiswa1"), BigDecimal.valueOf(eq(3.5)), eq(100)))
                .thenReturn(new Pendaftaran());

        String viewName = pendaftaranController.handleDaftar(
                lowonganId, daftarForm, bindingResult, model, principal, redirectAttributes);

        assertEquals("redirect:/lowongan/" + lowonganId, viewName);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    public void testHandleDaftarWithValidationErrors() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = pendaftaranController.handleDaftar(
                lowonganId, daftarForm, bindingResult, model, principal, redirectAttributes);

        assertEquals("daftar", viewName);
        verify(model).addAttribute(eq("lowongan"), eq(lowongan));
        verify(model).addAttribute(eq("kandidatId"), eq("mahasiswa1"));
        verify(model).addAttribute(eq("prasyaratList"), anyList());
    }

    @Test
    public void testHandleDaftarServiceException() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Fix the mock setup to properly match the method call
        when(pendaftaranService.daftar(
                eq(lowonganId),
                eq("mahasiswa1"),
                eq(BigDecimal.valueOf(3.5)),
                eq(100)))
                .thenThrow(new IllegalStateException("Lowongan sudah ditutup"));

        String viewName = pendaftaranController.handleDaftar(
                lowonganId, daftarForm, bindingResult, model, principal, redirectAttributes);

        assertEquals("redirect:/lowongan/" + lowonganId + "/daftar", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    public void testShowLowonganDetail() {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        String viewName = pendaftaranController.showLowonganDetail(
                lowonganId, model, principal, redirectAttributes);

        assertEquals("lowonganDetail", viewName);
        verify(model).addAttribute(eq("lowongan"), eq(lowongan));
        verify(model).addAttribute(eq("userId"), eq("mahasiswa1"));
    }

    @Test
    public void testShowLowonganDetailNotFound() {
        when(lowonganService.findById(lowonganId)).thenThrow(new NoSuchElementException());

        String viewName = pendaftaranController.showLowonganDetail(
                lowonganId, model, principal, redirectAttributes);

        assertEquals("redirect:/lowongan/list", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }
}