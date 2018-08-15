package br.com.caiopaulucci.dto;

import java.util.List;

public class VerificacaoFaceDTOOut {

    public boolean resposta;

    public boolean isResposta() {
        return resposta;
    }

    public void setResposta(boolean resposta) {
        this.resposta = resposta;
    }

    public VerificacaoFaceDTOOut(boolean resposta) {
        this.resposta = resposta;
    }
}
