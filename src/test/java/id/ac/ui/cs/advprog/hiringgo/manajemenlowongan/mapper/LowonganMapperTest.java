package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
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
class LowonganMapperTest {

    @Mock
    private PendaftaranServiceImpl pendaftaranService;

    @Mock
    private MataKuliahRepository mataKuliahRepository;

    @InjectMocks
    private LowonganMapper lowonganMapper;

    private UUID lowonganId;
    private String mataKuliahKode;
    private MataKuliah testMataKuliah;
    private Lowongan testLowongan;
    private LowonganDTO testLowonganDTO;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();
        mataKuliahKode = "CS101";

        // Setup MataKuliah using the constructor with parameters
        testMataKuliah = new MataKuliah(mataKuliahKode, "Advanced Programming", "Programming course description");

        // Setup Lowongan
        testLowongan = new Lowongan();
        testLowongan.setLowonganId(lowonganId);
        testLowongan.setMataKuliah(testMataKuliah);
        testLowongan.setTahunAjaran("2023/2024");
        testLowongan.setSemester("GENAP"); // Use String instead of enum
        testLowongan.setStatusLowongan("DIBUKA"); // Use String instead of enum
        testLowongan.setJumlahAsdosDibutuhkan(5);
        testLowongan.setJumlahAsdosDiterima(2);
        testLowongan.setJumlahAsdosPendaftar(3);

        // Setup LowonganDTO
        testLowonganDTO = new LowonganDTO();
        testLowonganDTO.setLowonganId(lowonganId);
        testLowonganDTO.setIdMataKuliah(mataKuliahKode);
        testLowonganDTO.setNamaMataKuliah("Advanced Programming");
        testLowonganDTO.setDeskripsiMataKuliah("Programming course description");
        testLowonganDTO.setTahunAjaran("2023/2024");
        testLowonganDTO.setSemester("GENAP");
        testLowonganDTO.setStatusLowongan("DIBUKA");
        testLowonganDTO.setJumlahAsdosDibutuhkan(5);
        testLowonganDTO.setJumlahAsdosDiterima(2);
        testLowonganDTO.setJumlahAsdosPendaftar(3);
    }

    @Test
    void testConstructor_initializesCorrectly() {
        // When
        LowonganMapper mapper = new LowonganMapper(pendaftaranService, mataKuliahRepository);

        // Then
        assertNotNull(mapper);
    }

    @Test
    void testToEntity_withValidDTO_returnsLowongan() {
        // Given
        when(mataKuliahRepository.findById(mataKuliahKode))
                .thenReturn(Optional.of(testMataKuliah));

        // When
        Lowongan result = lowonganMapper.toEntity(testLowonganDTO);

        // Then
        assertNotNull(result);
        assertEquals(lowonganId, result.getLowonganId());
        assertEquals(testMataKuliah, result.getMataKuliah());
        assertEquals("2023/2024", result.getTahunAjaran());
        assertEquals(Semester.GENAP, result.getSemester());
        assertEquals(StatusLowongan.DIBUKA, result.getStatusLowongan());
        assertEquals(5, result.getJumlahAsdosDibutuhkan());
        assertEquals(2, result.getJumlahAsdosDiterima());
        assertEquals(3, result.getJumlahAsdosPendaftar());

        verify(mataKuliahRepository).findById(mataKuliahKode);
    }

    @Test
    void testToEntity_withNullLowonganId_doesNotSetId() {
        // Given
        testLowonganDTO.setLowonganId(null);
        when(mataKuliahRepository.findById(mataKuliahKode))
                .thenReturn(Optional.of(testMataKuliah));

        // When
        Lowongan result = lowonganMapper.toEntity(testLowonganDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getLowonganId());
        assertEquals(testMataKuliah, result.getMataKuliah());
    }

    @Test
    void testToEntity_withNullMataKuliahId_doesNotSetMataKuliah() {
        // Given
        testLowonganDTO.setIdMataKuliah(null);

        // When
        Lowongan result = lowonganMapper.toEntity(testLowonganDTO);

        // Then
        assertNotNull(result);
        assertEquals(lowonganId, result.getLowonganId());
        assertNull(result.getMataKuliah());

        verifyNoInteractions(mataKuliahRepository);
    }

    @Test
    void testToEntity_withInvalidMataKuliahId_throwsException() {
        // Given
        String invalidKode = "INVALID";
        testLowonganDTO.setIdMataKuliah(invalidKode);
        when(mataKuliahRepository.findById(invalidKode))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> lowonganMapper.toEntity(testLowonganDTO));
        assertEquals("MataKuliah dengan kode " + invalidKode + " tidak ditemukan", ex.getMessage());

        verify(mataKuliahRepository).findById(invalidKode);
    }

    @Test
    void testToDto_withValidLowongan_returnsDTO() {
        // Given
        List<UUID> pendaftaranIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId))
                .thenReturn(pendaftaranIds);

        // When
        LowonganDTO result = lowonganMapper.toDto(testLowongan);

        // Then
        assertNotNull(result);
        assertEquals(lowonganId, result.getLowonganId());
        assertEquals(mataKuliahKode, result.getIdMataKuliah());
        assertEquals("Advanced Programming", result.getNamaMataKuliah());
        assertEquals("Programming course description", result.getDeskripsiMataKuliah());
        assertEquals("2023/2024", result.getTahunAjaran());
        assertEquals("GENAP", result.getSemester());
        assertEquals("DIBUKA", result.getStatusLowongan());
        assertEquals(5, result.getJumlahAsdosDibutuhkan());
        assertEquals(2, result.getJumlahAsdosDiterima());
        assertEquals(3, result.getJumlahAsdosPendaftar());
        assertEquals(pendaftaranIds, result.getIdDaftarPendaftaran());

        verify(pendaftaranService).getPendaftaranIdsByLowongan(lowonganId);
    }

    @Test
    void testToDto_withNullMataKuliah_doesNotSetMataKuliahFields() {
        // Given
        testLowongan.setMataKuliah(null);
        List<UUID> pendaftaranIds = Arrays.asList(UUID.randomUUID());
        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId))
                .thenReturn(pendaftaranIds);

        // When
        LowonganDTO result = lowonganMapper.toDto(testLowongan);

        // Then
        assertNotNull(result);
        assertEquals(lowonganId, result.getLowonganId());
        assertNull(result.getIdMataKuliah());
        assertNull(result.getNamaMataKuliah());
        assertNull(result.getDeskripsiMataKuliah());
        assertEquals("2023/2024", result.getTahunAjaran());
        assertEquals(pendaftaranIds, result.getIdDaftarPendaftaran());

        verify(pendaftaranService).getPendaftaranIdsByLowongan(lowonganId);
    }

    @Test
    void testToDto_withEmptyPendaftaranList_returnsEmptyList() {
        // Given
        List<UUID> emptyList = Collections.emptyList();
        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId))
                .thenReturn(emptyList);

        // When
        LowonganDTO result = lowonganMapper.toDto(testLowongan);

        // Then
        assertNotNull(result);
        assertEquals(emptyList, result.getIdDaftarPendaftaran());
        assertTrue(result.getIdDaftarPendaftaran().isEmpty());

        verify(pendaftaranService).getPendaftaranIdsByLowongan(lowonganId);
    }

    @Test
    void testToDtoList_withValidList_returnsDTOList() {
        // Given
        Lowongan lowongan2 = new Lowongan();
        UUID lowonganId2 = UUID.randomUUID();
        lowongan2.setLowonganId(lowonganId2);
        lowongan2.setMataKuliah(testMataKuliah);
        lowongan2.setTahunAjaran("2024/2025");
        lowongan2.setSemester("GANJIL"); // Use String
        lowongan2.setStatusLowongan("DITUTUP"); // Use String
        lowongan2.setJumlahAsdosDibutuhkan(3);
        lowongan2.setJumlahAsdosDiterima(1);
        lowongan2.setJumlahAsdosPendaftar(2);

        List<Lowongan> lowonganList = Arrays.asList(testLowongan, lowongan2);

        List<UUID> pendaftaranIds1 = Arrays.asList(UUID.randomUUID());
        List<UUID> pendaftaranIds2 = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId))
                .thenReturn(pendaftaranIds1);
        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId2))
                .thenReturn(pendaftaranIds2);

        // When
        List<LowonganDTO> result = lowonganMapper.toDtoList(lowonganList);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify first DTO
        LowonganDTO dto1 = result.get(0);
        assertEquals(lowonganId, dto1.getLowonganId());
        assertEquals("2023/2024", dto1.getTahunAjaran());
        assertEquals("GENAP", dto1.getSemester());
        assertEquals("DIBUKA", dto1.getStatusLowongan());
        assertEquals(pendaftaranIds1, dto1.getIdDaftarPendaftaran());

        // Verify second DTO
        LowonganDTO dto2 = result.get(1);
        assertEquals(lowonganId2, dto2.getLowonganId());
        assertEquals("2024/2025", dto2.getTahunAjaran());
        assertEquals("GANJIL", dto2.getSemester());
        assertEquals("DITUTUP", dto2.getStatusLowongan());
        assertEquals(pendaftaranIds2, dto2.getIdDaftarPendaftaran());

        verify(pendaftaranService).getPendaftaranIdsByLowongan(lowonganId);
        verify(pendaftaranService).getPendaftaranIdsByLowongan(lowonganId2);
    }

    @Test
    void testToDtoList_withEmptyList_returnsEmptyList() {
        // Given
        List<Lowongan> emptyList = Collections.emptyList();

        // When
        List<LowonganDTO> result = lowonganMapper.toDtoList(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(pendaftaranService);
    }

    @Test
    void testToDtoList_withSingleItem_returnsSingleItemList() {
        // Given
        List<Lowongan> singleItemList = Collections.singletonList(testLowongan);
        List<UUID> pendaftaranIds = Arrays.asList(UUID.randomUUID());

        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId))
                .thenReturn(pendaftaranIds);

        // When
        List<LowonganDTO> result = lowonganMapper.toDtoList(singleItemList);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(lowonganId, result.get(0).getLowonganId());
        assertEquals(pendaftaranIds, result.get(0).getIdDaftarPendaftaran());

        verify(pendaftaranService).getPendaftaranIdsByLowongan(lowonganId);
    }

    @Test
    void testToDto_withDifferentEnumValues_convertsCorrectly() {
        // Given
        testLowongan.setSemester("GANJIL"); // Use String
        testLowongan.setStatusLowongan("DITUTUP"); // Use String

        when(pendaftaranService.getPendaftaranIdsByLowongan(lowonganId))
                .thenReturn(Collections.emptyList());

        // When
        LowonganDTO result = lowonganMapper.toDto(testLowongan);

        // Then
        assertNotNull(result);
        assertEquals("GANJIL", result.getSemester());
        assertEquals("DITUTUP", result.getStatusLowongan());
    }

    @Test
    void testToEntity_withAllFieldsSet_mapsAllFields() {
        // Given
        when(mataKuliahRepository.findById(mataKuliahKode))
                .thenReturn(Optional.of(testMataKuliah));

        // When
        Lowongan result = lowonganMapper.toEntity(testLowonganDTO);

        // Then
        assertNotNull(result);
        assertNotNull(result.getLowonganId());
        assertNotNull(result.getMataKuliah());
        assertNotNull(result.getTahunAjaran());
        assertNotNull(result.getSemester());
        assertNotNull(result.getStatusLowongan());
        assertEquals(5, result.getJumlahAsdosDibutuhkan());
        assertEquals(2, result.getJumlahAsdosDiterima());
        assertEquals(3, result.getJumlahAsdosPendaftar());
    }
}