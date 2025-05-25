package id.ac.ui.cs.advprog.hiringgo.dashboard.service.data;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationDataServiceTest {

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @Mock
    private LowonganMapper lowonganMapper;

    @InjectMocks
    private ApplicationDataService service;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    private List<Pendaftaran> createSampleApplicationsForStatusTests() {
        List<Pendaftaran> applications = new ArrayList<>();

        Pendaftaran app1 = mock(Pendaftaran.class);
        when(app1.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        applications.add(app1);

        Pendaftaran app2 = mock(Pendaftaran.class);
        when(app2.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);
        applications.add(app2);

        Pendaftaran app3 = mock(Pendaftaran.class);
        when(app3.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        applications.add(app3);

        return applications;
    }

    private List<Pendaftaran> createSampleApplicationsForLowonganTests() {
        List<Pendaftaran> applications = new ArrayList<>();

        Pendaftaran app1 = mock(Pendaftaran.class);
        Lowongan lowongan1 = mock(Lowongan.class);
        when(app1.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(app1.getLowongan()).thenReturn(lowongan1);
        applications.add(app1);

        Pendaftaran app2 = mock(Pendaftaran.class);
        when(app2.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);
        applications.add(app2);

        Pendaftaran app3 = mock(Pendaftaran.class);
        Lowongan lowongan3 = mock(Lowongan.class);
        when(app3.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(app3.getLowongan()).thenReturn(lowongan3);
        applications.add(app3);

        return applications;
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        PendaftaranRepository mockRepo = mock(PendaftaranRepository.class);
        LowonganMapper mockMapper = mock(LowonganMapper.class);
        ApplicationDataService newService = new ApplicationDataService(mockRepo, mockMapper);
        assertNotNull(newService);
    }

    @Test
    void getAllApplications_withValidUserId_shouldReturnApplications() {
        List<Pendaftaran> sampleApplications = Arrays.asList(
                mock(Pendaftaran.class),
                mock(Pendaftaran.class),
                mock(Pendaftaran.class)
        );
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(sampleApplications);

        List<Pendaftaran> result = service.getAllApplications(userId);

        assertEquals(3, result.size());
        verify(pendaftaranRepository).findByKandidatId(userId);
    }

    @Test
    void getAllApplications_withEmptyResult_shouldReturnEmptyList() {
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(Collections.emptyList());

        List<Pendaftaran> result = service.getAllApplications(userId);

        assertTrue(result.isEmpty());
        verify(pendaftaranRepository).findByKandidatId(userId);
    }

    @Test
    void countApplicationsByStatus_withDiterimaStatus_shouldReturnCorrectCount() {
        List<Pendaftaran> sampleApplications = createSampleApplicationsForStatusTests();

        int result = service.countApplicationsByStatus(sampleApplications, StatusPendaftaran.DITERIMA);

        assertEquals(2, result);
    }

    @Test
    void countApplicationsByStatus_withDitolakStatus_shouldReturnCorrectCount() {
        List<Pendaftaran> sampleApplications = createSampleApplicationsForStatusTests();

        int result = service.countApplicationsByStatus(sampleApplications, StatusPendaftaran.DITOLAK);

        assertEquals(1, result);
    }

    @Test
    void countApplicationsByStatus_withNullList_shouldReturnZero() {
        int result = service.countApplicationsByStatus(null, StatusPendaftaran.DITERIMA);
        assertEquals(0, result);
    }

    @Test
    void countApplicationsByStatus_withEmptyList_shouldReturnZero() {
        int result = service.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITERIMA);
        assertEquals(0, result);
    }

    @Test
    void getAcceptedLowongan_withValidUserId_shouldReturnLowonganDTO() {
        List<Pendaftaran> sampleApplications = createSampleApplicationsForLowonganTests();
        LowonganDTO dto1 = mock(LowonganDTO.class);
        LowonganDTO dto2 = mock(LowonganDTO.class);

        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(sampleApplications);
        when(lowonganMapper.toDto(any())).thenReturn(dto1).thenReturn(dto2);

        List<LowonganDTO> result = service.getAcceptedLowongan(userId);

        assertEquals(2, result.size());
        verify(pendaftaranRepository).findByKandidatId(userId);
        verify(lowonganMapper, times(2)).toDto(any());
    }

    @Test
    void getAcceptedLowongan_withNoAcceptedApplications_shouldReturnEmptyList() {
        List<Pendaftaran> rejectedApps = Arrays.asList(
                createMockPendaftaran(StatusPendaftaran.DITOLAK),
                createMockPendaftaran(StatusPendaftaran.BELUM_DIPROSES)
        );

        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(rejectedApps);

        List<LowonganDTO> result = service.getAcceptedLowongan(userId);

        assertTrue(result.isEmpty());
        verify(pendaftaranRepository).findByKandidatId(userId);
        verify(lowonganMapper, never()).toDto(any());
    }

    @Test
    void getAcceptedLowongan_withEmptyApplications_shouldReturnEmptyList() {
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(Collections.emptyList());

        List<LowonganDTO> result = service.getAcceptedLowongan(userId);

        assertTrue(result.isEmpty());
        verify(pendaftaranRepository).findByKandidatId(userId);
    }

    private Pendaftaran createMockPendaftaran(StatusPendaftaran status) {
        Pendaftaran app = mock(Pendaftaran.class);
        when(app.getStatus()).thenReturn(status);
        if (status == StatusPendaftaran.DITERIMA) {
            when(app.getLowongan()).thenReturn(mock(Lowongan.class));
        }
        return app;
    }
}