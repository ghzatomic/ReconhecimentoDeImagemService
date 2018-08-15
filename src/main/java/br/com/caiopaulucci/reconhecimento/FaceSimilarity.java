package br.com.caiopaulucci.reconhecimento;

import br.com.caiopaulucci.dto.IdentificacaoFaceDTOIn;
import br.com.caiopaulucci.dto.VerificacaoFaceDTOIn;
import org.apache.commons.codec.binary.Base64;
import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.FacePatchFeature.Extractor;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.feature.comparison.FacialFeatureComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;
import org.openimaj.math.geometry.shape.Rectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

public class FaceSimilarity {


    public static final HaarCascadeDetector detector = HaarCascadeDetector.BuiltInCascade.frontalface_default.load();
    public static final FaceDetector<KEDetectedFace, FImage> kedetector = new FKEFaceDetector(detector);
    public static final Extractor extractor = new Extractor();

    public static final FacialFeatureComparator<FacePatchFeature> comparator =
            new FaceFVComparator<FacePatchFeature, FloatFV>(FloatFVComparison.EUCLIDEAN);

   /*
    // Eigenface
    final FaceDetector<KEDetectedFace, FImage> detector = new FKEFaceDetector(new HaarCascadeDetector(80));
    final EigenFaceRecogniser<KEDetectedFace, String> recogniser = EigenFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.EUCLIDEAN, 0.9f);
    moteurReconnaissance = FaceRecognitionEngine.create(detector, recogniser);

    // Fisherface
    final FaceDetector<KEDetectedFace, FImage> detector = new FKEFaceDetector(new HaarCascadeDetector(80));
    FisherFaceRecogniser<KEDetectedFace, String> recogniser = FisherFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.EUCLIDEAN);
    moteurReconnaissance = FaceRecognitionEngine.create(detector, recogniser);
*/


    public static final FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage> engine =
            new FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage>(kedetector, extractor, comparator);


    public static final double thresholdDefault = 60d;

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
        engine.setTest(image1, "fonte");
        for (String fonte : dto.getFontes()) {
            final FImage image2 = ImageUtilities.readF(new ByteArrayInputStream(Base64.decodeBase64(fonte)));
            engine.setQuery(image2, "busca");
            engine.performTest();
            for (final Map.Entry<String, Map<String, Double>> e : engine.getSimilarityDictionary().entrySet()) {
                double bestScore = Double.MAX_VALUE;
                String best = null;
                for (final Map.Entry<String, Double> matches : e.getValue().entrySet()) {
                    if (matches.getValue() < bestScore) {
                        bestScore = matches.getValue();
                        best = matches.getKey();
                    }
                }
                if (bestScore < dto.getThreshold()) {
                    return true;
                }

            }
        }
        return false;
    }


    public ByteArrayOutputStream identificaImagem(IdentificacaoFaceDTOIn dto) throws IOException {
        if (dto == null) {
            return null;
        }
        if (dto.getFontes() == null || dto.getFontes().isEmpty()) {
            return null;
        }
        final FImage image1 = ImageUtilities.readF(new ByteArrayInputStream(Base64.decodeBase64(dto.getBusca())));
        //DisplayUtilities.display(image1);
        engine.setTest(image1, "busca");
        final FImage imgRet = new FImage(image1.width, image1.height);
        imgRet.drawImage(image1, 0, 0);
        for (String fonte : dto.getFontes()) {
            final FImage image2 = ImageUtilities.readF(new ByteArrayInputStream(Base64.decodeBase64(fonte)));
            //DisplayUtilities.display(image2);
            engine.setQuery(image2, "fonte");
            engine.performTest();
            for (final Map.Entry<String, Map<String, Double>> e : engine.getSimilarityDictionary().entrySet()) {
                double bestScore = Double.MAX_VALUE;
                String best = null;
                for (final Map.Entry<String, Double> matches : e.getValue().entrySet()) {
                    if (matches.getValue() < bestScore) {
                        bestScore = matches.getValue();
                        best = matches.getKey();
                    }
                }
                if (bestScore < dto.getThreshold()) {
                    // and this composites the original two images together, and draws
                    // the matching pair of faces:
                    final Rectangle r = engine.getBoundingBoxes().get(best);
                    imgRet.drawShape(r, 1F);
                }
            }

        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageUtilities.write(imgRet,"jpeg",bout);
        //DisplayUtilities.display(imgRet);
        return bout;
    }

	/*public static void main(String[] args) throws IOException{
		FaceSimilarity fc = new FaceSimilarity();
        IdentificacaoFaceDTOIn dto = new IdentificacaoFaceDTOIn();
        dto.setFontes(new ArrayList<>());
        //dto.getFontes().add(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"obamabusca1.jpg")).toPath())));
        dto.getFontes().add(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"gustavobiba.png")).toPath())));
        dto.setBusca(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"pessoas2.jpg")).toPath())));
        dto.setThreshold(thresholdDefault);
		long tempoInicio = System.currentTimeMillis();
		fc.identificaImagem(dto);
		System.out.println("Tempo1 Total: "+(System.currentTimeMillis()-tempoInicio));
	}*/

    public static void main(String[] args) throws IOException{
        FaceSimilarity fc = new FaceSimilarity();
        VerificacaoFaceDTOIn dto = new VerificacaoFaceDTOIn();
        dto.setFontes(new ArrayList<>());
        //dto.getFontes().add(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"obamabusca1.jpg")).toPath())));
        dto.getFontes().add(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"obamarosto1.jpg")).toPath())));
        dto.setBusca(Base64.encodeBase64String(Files.readAllBytes((new File(caminhoTeste+"gustavobiba.png")).toPath())));
        dto.setThreshold(thresholdDefault);
        long tempoInicio = System.currentTimeMillis();
        System.out.println(fc.verificaImagem(dto));
        System.out.println("Tempo1 Total: "+(System.currentTimeMillis()-tempoInicio));
    }

}