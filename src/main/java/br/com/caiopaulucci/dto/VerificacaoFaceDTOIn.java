package br.com.caiopaulucci.dto;

import java.util.List;

public class VerificacaoFaceDTOIn {

    public List<String> fontes;

    public String busca;

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
}
