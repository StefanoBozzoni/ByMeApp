package com.mastersoft.steb.bymeapp.model;

public class Prova {

    private String testo;
    private String provaId;
    private int ciccio;

    public Prova() {
    }

    public Prova(String provaId,String testo, int ciccio) {
        super();
        this.testo  = testo;
        this.provaId= provaId;
        this.ciccio = ciccio;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getTesto() {
        return testo;
    }

    public void setProvaId(String testo) {
        this.provaId = testo;
    }

    public String getProvaId() {
        return provaId;
    }

    public void setCiccio(int ciccio) {
        this.ciccio = ciccio;
    }

    public int getCiccio() {
        return ciccio;
    }

}
