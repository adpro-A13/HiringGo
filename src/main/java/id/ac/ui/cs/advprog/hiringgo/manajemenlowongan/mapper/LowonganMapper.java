package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LowonganMapper {

    private final PendaftaranServiceImpl pendaftaranService;
    private final MataKuliahRepository mataKuliahRepository;

    public LowonganMapper(PendaftaranServiceImpl pendaftaranService, MataKuliahRepository mataKuliahRepository) {
        this.pendaftaranService = pendaftaranService;
        this.mataKuliahRepository = mataKuliahRepository;
    }

    public Lowongan toEntity(LowonganDTO dto) {
        Lowongan lowongan = new Lowongan();

        if (dto.getLowonganId() != null) {
            lowongan.setLowonganId(dto.getLowonganId());
        }

        if (dto.getIdMataKuliah() != null) {
            MataKuliah mataKuliah = mataKuliahRepository.findById(dto.getIdMataKuliah())
                    .orElseThrow(() -> new IllegalArgumentException("MataKuliah dengan kode " + dto.getIdMataKuliah() + " tidak ditemukan"));
            lowongan.setMataKuliah(mataKuliah);
        }

        lowongan.setTahunAjaran(dto.getTahunAjaran());
        lowongan.setSemester((dto.getSemester()));
        lowongan.setStatusLowongan((dto.getStatusLowongan()));
        lowongan.setJumlahAsdosDibutuhkan(dto.getJumlahAsdosDibutuhkan());
        lowongan.setJumlahAsdosDiterima(dto.getJumlahAsdosDiterima());
        lowongan.setJumlahAsdosPendaftar(dto.getJumlahAsdosPendaftar());

        return lowongan;
    }

    public LowonganDTO toDto(Lowongan lowongan) {
        LowonganDTO dto = new LowonganDTO();

        dto.setLowonganId(lowongan.getLowonganId());

        if (lowongan.getMataKuliah() != null) {
            dto.setIdMataKuliah(lowongan.getMataKuliah().getKode());
            dto.setNamaMataKuliah(lowongan.getMataKuliah().getNama());
            dto.setDeskripsiMataKuliah(lowongan.getMataKuliah().getDeskripsi());
        }

        dto.setTahunAjaran(lowongan.getTahunAjaran());
        dto.setSemester(String.valueOf(lowongan.getSemester()));
        dto.setStatusLowongan(String.valueOf(lowongan.getStatusLowongan()));
        dto.setJumlahAsdosDibutuhkan(lowongan.getJumlahAsdosDibutuhkan());
        dto.setJumlahAsdosDiterima(lowongan.getJumlahAsdosDiterima());
        dto.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar());

        List<Pendaftaran> daftarPendaftaran = pendaftaranService.getByLowongan(lowongan.getLowonganId());

        List<UUID> idDaftarPendaftaran = daftarPendaftaran.stream()
                .map(Pendaftaran::getPendaftaranId)
                .collect(Collectors.toList());

        dto.setIdDaftarPendaftaran(idDaftarPendaftaran);

        return dto;
    }

    public List<LowonganDTO> toDtoList(List<Lowongan> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
