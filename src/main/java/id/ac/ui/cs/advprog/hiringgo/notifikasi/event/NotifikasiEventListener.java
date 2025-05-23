package id.ac.ui.cs.advprog.hiringgo.notifikasi.event;

import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.repository.NotifikasiRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotifikasiEventListener {

    private final NotifikasiRepository notifikasiRepository;

    public NotifikasiEventListener(NotifikasiRepository notifikasiRepository) {
        this.notifikasiRepository = notifikasiRepository;
    }

    @EventListener
    @Async("taskExecutor")
    public void handleNotifikasiEvent(NotifikasiEvent event) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Notifikasi notif = new Notifikasi(
                event.getMahasiswa(),
                event.getMataKuliah(),
                event.getTahunAjaran(),
                event.getSemester(),
                event.getStatus()
        );
        notifikasiRepository.save(notif);
    }

}
