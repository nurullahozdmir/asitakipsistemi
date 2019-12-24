package com.example.asitakipson;


public class Asi {

    private String asiId;
    private String asiAdi;
    private String hastahaneAdi;
    private String asiTarih;
    private String asiDurumu;

    public Asi() {
    }

    public Asi(String asiId,String asiAdi,String hastahaneAdi,String asiTarih,String asiDurumu) {
        this.asiId = asiId;
        this.asiAdi=asiAdi;
        this.hastahaneAdi=hastahaneAdi;
        this.asiTarih=asiTarih;
        this.asiDurumu = asiDurumu;
    }
    public String getAsiAdi() {
        return asiAdi;
    }
    public void setAsiAdi(String asiAdi) {
        this.asiAdi = asiAdi;
    }

    public String getHastahaneAdi() {
        return hastahaneAdi;
    }
    public void setHastahaneAdi(String hastahaneAdi) {
        this.hastahaneAdi = hastahaneAdi;
    }


    public String getAsiId() {
        return asiId;
    }
    public void setAsiId(String asiId) {
        this.asiId = asiId;
    }

    public String getAsiTarih() {
        return asiTarih;
    }
    public void setAsiTarih(String asiTarih) {
        this.asiTarih = asiTarih;
    }

    public String getAsiDurumu() {
        return asiDurumu;
    }
    public void setAsiDurumu(String asiDurumu) {
        this.asiDurumu = asiDurumu;
    }
}
