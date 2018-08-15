package br.com.caiopaulucci.reconhecimento;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.alignment.RotateScaleAligner;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.recognition.EigenFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.util.pair.IndependentPair;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FaceRecognitionTest {
    private static FKEFaceDetector faceDetector = new FKEFaceDetector(new HaarCascadeDetector());
    private static EigenFaceRecogniser<KEDetectedFace, String> faceRecognizer = EigenFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.CORRELATION, 0.9f);
    private static FaceRecognitionEngine<KEDetectedFace, String> faceEngine = FaceRecognitionEngine.create(faceDetector, faceRecognizer);
    private static int id = 0;

    public static void main(String[] args) throws IOException {
        recognizeFace("lenna.png");
        recognizeFace("lenna.png");
    }

    public static void recognizeFace(String path) throws IOException {
        FImage fimg = ImageUtilities.readF(new File(path));
        List<KEDetectedFace> faces = faceEngine.getDetector().detectFaces(fimg);

        for (KEDetectedFace face : faces) {
            String person = null;
            try {
                List<IndependentPair<KEDetectedFace, ScoredAnnotation<String>>> rfaces = faceEngine.recogniseBest(face.getFacePatch());   // crashes here
                ScoredAnnotation<String> score = rfaces.get(0).getSecondObject();
                if (score != null)
                    person = score.annotation;
            } catch (Exception e) {
            }

            if (person == null) {
                person = new String(String.valueOf(id++));
                System.out.println("Identified new person: " + person);
                faceEngine.train(person, face.getFacePatch());                    // necessary?
            } else {
                System.out.println("Identified existing person: " + person);
            }
        }
    }
}