package id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.exception.DosenEmailNotFoundException;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class MataKuliahMapper {

    private final UserRepository userRepository;

    public MataKuliahMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MataKuliah toEntity(MataKuliahDTO dto) {
        MataKuliah mataKuliah = new MataKuliah(dto.getKode(), dto.getNama(), dto.getDeskripsi());

        List<String> dosenEmails = dto.getDosenPengampuEmails();
        if (dosenEmails != null && !dosenEmails.isEmpty()) {
            List<User> dosenPengampu = userRepository.findAllByEmailIn(dosenEmails);
            Set<String> found = dosenPengampu.stream().map(User::getUsername).collect(toSet());
            for (String email : dosenEmails) {
                if (!found.contains(email)){
                    throw new DosenEmailNotFoundException("Pengguna dengan email " + email + " tidak ditemukan");
                }
            }
            for (User user : dosenPengampu) {
                if (!(user instanceof Dosen)) {
                    throw new DosenEmailNotFoundException("Pengguna dengan email " + user.getUsername()+ " bukan DOSEN");
                }
                mataKuliah.addDosenPengampu((Dosen) user);
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