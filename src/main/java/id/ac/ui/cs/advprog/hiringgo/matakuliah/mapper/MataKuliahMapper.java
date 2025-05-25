package id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.exception.DosenEmailNotFoundException;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MataKuliahMapper {

    private final UserRepository userRepository;

    public MataKuliahMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MataKuliah toEntity(MataKuliahDTO dto) {
        MataKuliah mataKuliah = new MataKuliah(dto.getKode(), dto.getNama(), dto.getDeskripsi());

        if (dto.getDosenPengampuEmails() != null) {
            for (String email : dto.getDosenPengampuEmails()) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new DosenEmailNotFoundException("Pengguna dengan email " + email + " tidak ditemukan"));

                if (!(user instanceof Dosen dosen)) {
                    throw new DosenEmailNotFoundException("Pengguna dengan email " + email + " bukan seorang dosen");
                }
                mataKuliah.addDosenPengampu(dosen);
            }
        }

        return mataKuliah;
    }

    public MataKuliahDTO toDto(MataKuliah entity) {
        MataKuliahDTO dto = new MataKuliahDTO();
        dto.setKode(entity.getKode());
        dto.setNama(entity.getNama());
        dto.setDeskripsi(entity.getDeskripsi());

        List<String> dosenEmails = entity.getDosenPengampu().stream()
                .map(Dosen::getUsername)
                .toList();

        dto.setDosenPengampuEmails(dosenEmails);

        return dto;
    }

    public List<MataKuliahDTO> toDtoList(List<MataKuliah> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}