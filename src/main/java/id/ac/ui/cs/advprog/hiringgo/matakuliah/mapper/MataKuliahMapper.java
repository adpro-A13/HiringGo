package id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MataKuliahMapper {

    public MataKuliah toEntity(MataKuliahDTO dto) {
        MataKuliah mataKuliah = new MataKuliah(dto.getKode(), dto.getNama(), dto.getDeskripsi());

        // Add dosen pengampu if available
        if (dto.getDosenPengampu() != null) {
            dto.getDosenPengampu().forEach(mataKuliah::addDosenPengampu);
        }

        return mataKuliah;
    }

    public MataKuliahDTO toDto(MataKuliah entity) {
        MataKuliahDTO dto = new MataKuliahDTO();
        dto.setKode(entity.getKode());
        dto.setNama(entity.getNama());
        dto.setDeskripsi(entity.getDeskripsi());
        dto.setDosenPengampu(new ArrayList<>(entity.getDosenPengampu()));

        return dto;
    }

    public List<MataKuliahDTO> toDtoList(List<MataKuliah> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}