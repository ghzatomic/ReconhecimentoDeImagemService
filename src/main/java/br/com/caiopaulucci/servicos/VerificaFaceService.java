package br.com.caiopaulucci.servicos;

import br.com.caiopaulucci.dto.VerificacaoFaceDTOIn;
import br.com.caiopaulucci.dto.VerificacaoFaceDTOOut;
import br.com.caiopaulucci.reconhecimento.FaceSimilarity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class VerificaFaceService {

    public static final FaceSimilarity fs = new FaceSimilarity();

    @RequestMapping(value="/verificaFace",method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    VerificacaoFaceDTOOut verificaFace(@RequestBody VerificacaoFaceDTOIn in) throws IOException {
        return new VerificacaoFaceDTOOut(fs.verificaImagem(in));
    }



}
