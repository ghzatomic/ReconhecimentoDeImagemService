package br.com.caiopaulucci.reconhecimento;

import br.com.caiopaulucci.dto.VerificacaoFaceDTOIn;
import org.apache.commons.codec.binary.Base64;
import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.FacePatchFeature.Extractor;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Example showing how to use the {@link FaceSimilarityEngine} class to compare
 * faces detected in two images.
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
public class FaceSimilarity {


    public static final HaarCascadeDetector detector = HaarCascadeDetector.BuiltInCascade.frontalface_alt2.load();
    public static final FKEFaceDetector kedetector = new FKEFaceDetector(detector);
    public static final Extractor extractor = new Extractor();

    public static final FaceFVComparator<FacePatchFeature, FloatFV> comparator =
            new FaceFVComparator<FacePatchFeature, FloatFV>(FloatFVComparison.EUCLIDEAN);

    public static final FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage> engine =
            new FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage>(kedetector, extractor, comparator);


    public static String caminhoTeste = "/home/ghzatomic/Imagens/";

    public FaceSimilarity() {
    }

    public boolean verificaImagem(VerificacaoFaceDTOIn dto) throws IOException {
        if (dto == null) {
            return false;
        }
        if (dto.getFontes() == null || dto.getFontes().isEmpty()) {
            return false;
        }
        final FImage image1 = ImageUtilities.readF(new ByteArrayInputStream(Base64.decodeBase64(dto.getBusca())));
        engine.setQuery(image1, "fonte");
        for (String fonte : dto.getFontes()) {
            engine.setTest(ImageUtilities.readF(new ByteArrayInputStream(Base64.decodeBase64(dto.getBusca()))), "busca");
            engine.performTest();
            if (!engine.getSimilarityDictionary().isEmpty()) {
                return true;
            }
        }
        return false;
    }

	public static void main(String[] args) throws IOException{
		FaceSimilarity fc = new FaceSimilarity();
        VerificacaoFaceDTOIn dto = new VerificacaoFaceDTOIn();
        dto.setFontes(new ArrayList<>());
        dto.getFontes().add(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"obamabusca1.jpg")).toPath())));
        dto.getFontes().add(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"obamabusca2.jpg")).toPath())));
        dto.setBusca(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"obamarosto1.jpg")).toPath())));
		long tempoInicio = System.currentTimeMillis();
		System.out.println(fc.verificaImagem(dto));
		System.out.println("Tempo1 Total: "+(System.currentTimeMillis()-tempoInicio));
		tempoInicio = System.currentTimeMillis();
        System.out.println(fc.verificaImagem(dto));
		System.out.println("Tempo2 Total: "+(System.currentTimeMillis()-tempoInicio));
	}

}