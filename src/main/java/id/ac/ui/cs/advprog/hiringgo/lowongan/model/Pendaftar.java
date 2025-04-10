package id.ac.ui.cs.advprog.hiringgo.lowongan.model;

public class Pendaftar {
    private String nama;
    private double ipk;
    private int sks;

    public Pendaftar() {
    }

    public Pendaftar(String nama, double ipk, int sks) {
        this.nama = nama;
        this.ipk = ipk;
        this.sks = sks;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getIpk() {
        return ipk;
    }

    public void setIpk(double ipk) {
        this.ipk = ipk;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        this.sks = sks;
    }
}
