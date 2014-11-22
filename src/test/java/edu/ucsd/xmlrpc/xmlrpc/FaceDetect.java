package edu.ucsd.xmlrpc.xmlrpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.opencv.core.Core;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetect {

  public static void main(String[] args) {

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    System.out.println("\nRunning FaceDetector");

    Pattern pattern;
    if(args.length < 1){
        pattern = Pattern.compile("/.*face.*.*");
    } else{
        pattern = Pattern.compile(args[0]);
    }
    final Collection<String> list = FaceDetect.getResources(pattern);
    for(final String name : list){
        System.out.println(name);
    }

    CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
//    CascadeClassifier faceDetector = new CascadeClassifier(FaceDetect.class.getResource("haarcascade_frontalface_alt.xml").getPath());
    /*
        Mat image = Highgui
                .imread(FaceDetect.class.getResource("shekhar.JPG").getPath());

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }

        String filename = "ouput.png";
        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, image);
     */
  }

  public static Collection<String> getResources(
      final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<String>();
    final String classPath = System.getProperty("java.class.path", ".");
    final String[] classPathElements = classPath.split(":");
    for(final String element : classPathElements){
      retval.addAll(getResources(element, pattern));
    }
    return retval;
  }

  private static Collection<String> getResources(
      final String element,
      final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<String>();
    final File file = new File(element);
    if(file.isDirectory()){
      retval.addAll(getResourcesFromDirectory(file, pattern));
    } else{
      retval.addAll(getResourcesFromJarFile(file, pattern));
    }
    return retval;
  }

  private static Collection<String> getResourcesFromJarFile(
      final File file,
      final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<String>();
    ZipFile zf;
    try{
      zf = new ZipFile(file);
    } catch(final ZipException e){
      throw new Error(e);
    } catch(final IOException e){
      throw new Error(e);
    }
    final Enumeration e = zf.entries();
    while(e.hasMoreElements()){
      final ZipEntry ze = (ZipEntry) e.nextElement();
      final String fileName = ze.getName();
      final boolean accept = pattern.matcher(fileName).matches();
      if(accept){
        retval.add(fileName);
      }
    }
    try{
      zf.close();
    } catch(final IOException e1){
      throw new Error(e1);
    }
    return retval;
  }

  private static Collection<String> getResourcesFromDirectory(
      final File directory,
      final Pattern pattern){
    final ArrayList<String> retval = new ArrayList<String>();
    final File[] fileList = directory.listFiles();
    for(final File file : fileList){
      if(file.isDirectory()){
        retval.addAll(getResourcesFromDirectory(file, pattern));
      } else{
        try{
          final String fileName = file.getCanonicalPath();
          final boolean accept = pattern.matcher(fileName).matches();
          if(accept){
            retval.add(fileName);
          }
        } catch(final IOException e){
          throw new Error(e);
        }
      }
    }
    return retval;
  }
}