package edu.ucsd.xmlrpc.xmlrpc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FrameHandler {

  public static final boolean SHOW_RECTANGLE = false;

  private static final int TRAILING_FRAME_CUTOFF = 3;
  private static final double SIMILARITY_THRESHOLD = .2;
  private static final int SURROUNDING_LOOK_SIZE = 10;
  private static final int SURROUNDING_FACE_THRESHOLD = 1;

  private static final double FACE_EXPAND = 1.4;
  private static CascadeClassifier faceDetector;

  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    faceDetector = new CascadeClassifier("lbpcascade_frontalface.xml");
  }

  private ArrayList<Mat> processedFrames;
  private Mat[] unprocessedFrames;

  private int frameWidth;
  private int frameHeight;

  public byte[] processFrame(byte[] m0, byte[] m1, byte[] m2, byte[] m3, byte[] m4,
      byte[] m5, byte[] m6, byte[] m7, byte[] m8, byte[] m9) {

    return null;
  }

/*
  public Frame put(byte[] array) {
    MatOfRect faceDetections = new MatOfRect();
    // TODO: objdetect.CV_HAAR_SCALE_IMAGE
    faceDetector.detectMultiScale(unprocessedFrames[i], faceDetections, 1.1, 2, 2,
        new Size(minFaceSize, minFaceSize), new Size());
  }
*/

  public byte[] get(byte[] ans) {
    System.out.println("get");
    return ans;
  }

  public byte[] simpleDetect(byte[] rawFace) {
    Mat mat = resultToMat(rawFace);
    frameWidth = mat.cols();
    frameHeight = mat.rows();
    int minFaceSize = (int) (mat.rows() * 0.2);
    MatOfRect faceDetections = new MatOfRect();
    // TODO: objdetect.CV_HAAR_SCALE_IMAGE
    faceDetector.detectMultiScale(mat, faceDetections, 1.1, 2, 2,
        new Size(minFaceSize, minFaceSize), new Size());
    Mat blurred = mat.clone();
    for (Rect face : faceDetections.toArray()) {
      Rect rect = expandRect(face);
//      int xStart = Math.max(0, rect.x);
//      int yStart = Math.max(0, rect.y);
//      int xEnd = Math.min(blurred.cols(), rect.x + rect.width);
//      int yEnd = Math.min(blurred.rows(), rect.y + rect.height);

      Mat mask = blurred.submat(rect);
      Imgproc.GaussianBlur(mask, mask, new Size(55, 55), 55);
      if (SHOW_RECTANGLE) {
        Core.rectangle(blurred, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
            new Scalar(0, 255, 0));
      }
    }
    return matToBytes(blurred);
  }

  public ArrayList<Mat> processFrames() {
    processedFrames = new ArrayList<Mat>(unprocessedFrames.length);
    if (unprocessedFrames.length > 0) {
      frameWidth = unprocessedFrames[0].cols();
      frameHeight = unprocessedFrames[0].rows();
      int minFaceSize = (int) (unprocessedFrames[0].rows() * 0.2);

      // get all faces from frames
      Rect[][] faces = new Rect[unprocessedFrames.length][];
      for (int i = 0; i < unprocessedFrames.length; i++) {
        MatOfRect faceDetections = new MatOfRect();
        // TODO: objdetect.CV_HAAR_SCALE_IMAGE
        faceDetector.detectMultiScale(unprocessedFrames[i], faceDetections, 1.1, 2, 2,
            new Size(minFaceSize, minFaceSize), new Size());
        faces[i] = faceDetections.toArray();
      }

      // prune face list
      ArrayList<Rect>[] processedFaces = processFaces(faces);

      // blur frames
      for (int i = 0; i < unprocessedFrames.length - TRAILING_FRAME_CUTOFF; i++) {
        Mat blurred = unprocessedFrames[i].clone();
        for (Rect face : processedFaces[i]) {
          Rect rect = expandRect(face);
//          int xStart = Math.max(0, rect.x);
//          int yStart = Math.max(0, rect.y);
//          int xEnd = Math.min(blurred.cols(), rect.x + rect.width);
//          int yEnd = Math.min(blurred.rows(), rect.y + rect.height);

          Mat mask = blurred.submat(rect);
          Imgproc.GaussianBlur(mask, mask, new Size(55, 55), 55);
          if (SHOW_RECTANGLE) {
            Core.rectangle(blurred, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                new Scalar(0, 255, 0));
          }
        }
        processedFrames.add(blurred);
      }
    }
    return processedFrames;
  }

  private Rect expandRect(Rect face) {
    int width = (int) (face.width * FACE_EXPAND);
    int height = (int) (face.height * FACE_EXPAND);
    int x = Math.max(0, face.x - (width - face.width) / 2);
    int y = Math.max(0, face.y - (height - face.height) / 2);
    width = Math.min(frameWidth - x, width);
    height = Math.min(frameHeight - y, height);
    return new Rect(x, y, width, height);
  }

  private ArrayList<Rect>[] processFaces(Rect[][] faces) {
    @SuppressWarnings("unchecked")
    ArrayList<Rect>[] result = new ArrayList[faces.length];
    for (int i = 0; i < faces.length; i++) {
      Map<Rect, Integer> faceConfidencesBefore = new HashMap<>();
      for (int j = Math.max(0, i - SURROUNDING_LOOK_SIZE); j < i; j++) {
        for (Rect face : faces[j]) {
          addFace(faceConfidencesBefore, face);
        }
      }
      Map<Rect, Integer> faceConfidencesAfter = new HashMap<>();
      for (int j = Math.min(faces.length - 1, i + SURROUNDING_LOOK_SIZE); j > i; j--) {
        for (Rect face : faces[j]) {
          addFace(faceConfidencesAfter, face);
        }
      }
      getRealFaces(faceConfidencesBefore);
      getRealFaces(faceConfidencesAfter);
      result[i] = getFinalFaces(faces[i], faceConfidencesBefore, faceConfidencesAfter);
    }
    return result;
  }

  private void getRealFaces(Map<Rect, Integer> faceConfidences) {
    ArrayList<Rect> toRemove = new ArrayList<Rect>();
    for (Map.Entry<Rect, Integer> faceEntry : faceConfidences.entrySet()) {
      Rect face = faceEntry.getKey();
      double minThreshold = face.width * SURROUNDING_FACE_THRESHOLD * frameHeight / 3.0;
      if (faceEntry.getValue() * (1 + SIMILARITY_THRESHOLD) < minThreshold) {
        toRemove.add(face);
      }
    }
    for (Rect face : toRemove) {
      faceConfidences.remove(face);
    }
  }

  private ArrayList<Rect> getFinalFaces(Rect[] faces, Map<Rect, Integer> faceConfidencesBefore,
      Map<Rect, Integer> faceConfidencesAfter) {
    ArrayList<Rect> finalFaces = new ArrayList<>();

    // remove extra faces
    for (Rect face : faces) {
      Rect before = similarFace(faceConfidencesBefore, face);
      Rect after = similarFace(faceConfidencesAfter, face);
      if (before != null || after != null) {
        finalFaces.add(face);
        faceConfidencesBefore.remove(before);
        faceConfidencesAfter.remove(after);
      }
    }

    // add missing faces
    for (Map.Entry<Rect, Integer> beforeEntry : faceConfidencesBefore.entrySet()) {
      for (Map.Entry<Rect, Integer> afterEntry : faceConfidencesAfter.entrySet()) {
        if (similarFace(beforeEntry.getKey(), afterEntry.getKey(), 2)) {
          finalFaces.add(mergeFaces(beforeEntry.getKey(), afterEntry.getKey()));
        }
      }
    }
    return finalFaces;
  }

  private Rect mergeFaces(Rect face1, Rect face2) {
    int x = (face1.x + face2.x) / 2;
    int y = (face1.y + face2.y) / 2;
    int width = (face1.width + face2.width) / 2;
    int height = (face1.height + face2.height) / 2;
    return new Rect(x, y, width, height);
  }

  private void addFace(Map<Rect, Integer> faceConfidences, Rect face) {
    for (Map.Entry<Rect, Integer> faceEntry : faceConfidences.entrySet()) {
      if (similarFace(faceEntry.getKey(), face, 1)) {
        faceConfidences.remove(faceEntry.getKey());
        faceConfidences.put(face, face.height * face.width + faceEntry.getValue());
        return;
      }
    }
    faceConfidences.put(face, face.height * face.width);
  }

  private boolean similarFace(Rect faceEntry, Rect face, double similarityMultiplier) {
    double simThresh = SIMILARITY_THRESHOLD * similarityMultiplier;
    double widthSimilarity = Math.abs(((double) faceEntry.width) / face.width - 1);
    double heightSimilarity = Math.abs(((double) faceEntry.height) / face.height - 1);
    if (widthSimilarity < simThresh && heightSimilarity < simThresh) {
      simThresh*=5;
      double xSimilarity = Math.abs((faceEntry.x - face.x) / ((double) face.width));
      double ySimilarity = Math.abs((faceEntry.y - face.y) / ((double) face.height));
      return xSimilarity < simThresh && ySimilarity < simThresh;
    }
    return false;
  }

  private Rect similarFace(Map<Rect, Integer> faceConfidences, Rect face) {
    for (Map.Entry<Rect, Integer> faceEntry : faceConfidences.entrySet()) {
      if (similarFace(faceEntry.getKey(), face, 1)) {
        return faceEntry.getKey();
      }
    }
    return null;
  }

  private Mat resultToMat(Object pResult) {
    byte[] rawData = (byte[]) pResult;
    Mat result = new Mat(frameHeight, frameWidth, CvType.CV_8UC4);
    result.put(0, 0, rawData);
    return result;
  }

  private byte[] matToBytes(Mat mat) {
    byte[] imageInBytes = new byte[(int) (mat.total() * mat.channels())];
    mat.get(0, 0, imageInBytes);
    return imageInBytes;
  }
}
