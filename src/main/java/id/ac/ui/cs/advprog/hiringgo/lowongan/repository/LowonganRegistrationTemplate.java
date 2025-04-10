package id.ac.ui.cs.advprog.hiringgo.lowongan.service;

import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Pendaftar;

public abstract class LowonganRegistrationTemplate {

    // Metode template final: mengatur alur pendaftaran
    public final void register(Lowongan lowongan, Pendaftar pendaftar) {
        validate(pendaftar);
        processRegistration(lowongan, pendaftar);
        updateQuota(lowongan);
        notifyUser(pendaftar);
    }

    // Langkah-langkah abstrak yang wajib diimplementasikan di subclass
    protected abstract void validate(Pendaftar pendaftar);
    protected abstract void processRegistration(Lowongan lowongan, Pendaftar pendaftar);
    protected abstract void updateQuota(Lowongan lowongan);
    protected abstract void notifyUser(Pendaftar pendaftar);
}
