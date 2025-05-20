package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LowonganMapper {

    private final MataKuliahRepository mataKuliahRepository;

    public LowonganMapper(MataKuliahRepository mataKuliahRepository) {
        this.mataKuliahRepository = mataKuliahRepository;
    }

    public Lowongan toEntity(LowonganDTO dto) {
        Lowongan lowongan = new Lowongan();

        if (dto.getLowonganId() != null) {
            lowongan.setLowonganId(dto.getLowonganId());
        }

        // Convert idMataKuliah (kode) ke MataKuliah entity
        if (dto.getIdMataKuliah() != null) {
            MataKuliah mataKuliah = mataKuliahRepository.findById(dto.getIdMataKuliah())
                    .orElseThrow(() -> new IllegalArgumentException("MataKuliah dengan kode " + dto.getIdMataKuliah() + " tidak ditemukan"));
            lowongan.setMataKuliah(mataKuliah);
        }

        lowongan.setTahunAjaran(dto.getTahunAjaran());
        lowongan.setSemester(String.valueOf(dto.getSemester()));
        lowongan.setStatusLowongan(String.valueOf(dto.getStatusLowongan()));
        lowongan.setJumlahAsdosDibutuhkan(dto.getJumlahAsdosDibutuhkan());
        lowongan.setJumlahAsdosDiterima(dto.getJumlahAsdosDiterima());
        lowongan.setJumlahAsdosPendaftar(dto.getJumlahAsdosPendaftar());

        return lowongan;
    }

    public LowonganDTO toDto(Lowongan entity) {
        LowonganDTO dto = new LowonganDTO();

        dto.setLowonganId(entity.getLowonganId());
        dto.setIdMataKuliah(entity.getMataKuliah() != null ? entity.getMataKuliah().getKode() : null);
        dto.setTahunAjaran(entity.getTahunAjaran());
        dto.setSemester(entity.getSemester());
        dto.setStatusLowongan(entity.getStatusLowongan());
        dto.setJumlahAsdosDibutuhkan(entity.getJumlahAsdosDibutuhkan());
        dto.setJumlahAsdosDiterima(entity.getJumlahAsdosDiterima());
        dto.setJumlahAsdosPendaftar(entity.getJumlahAsdosPendaftar());

        List<UUID> daftarPendaftaranIds = entity.getDaftarPendaftaran()
                .stream()
                .map(Pendaftaran::getPendaftaranId)
                .collect(Collectors.toList());
        dto.setIdDaftarPendaftaran(daftarPendaftaranIds);

        return dto;
    }

    public List<LowonganDTO> toDtoList(List<Lowongan> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
