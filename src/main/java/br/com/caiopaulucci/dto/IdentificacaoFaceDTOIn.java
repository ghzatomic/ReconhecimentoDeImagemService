package br.com.caiopaulucci.dto;

import java.util.List;

public class IdentificacaoFaceDTOIn {

    private List<String> fontes;

    private String busca;

    private double threshold;

    public List<String> getFontes() {
        return fontes;
    }

    public void setFontes(List<String> fontes) {
        this.fontes = fontes;
    }

    public String getBusca() {
        return busca;
    }

    public void setBusca(String busca) {
        this.busca = busca;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
