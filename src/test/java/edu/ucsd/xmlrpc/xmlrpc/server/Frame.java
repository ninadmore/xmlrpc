package edu.ucsd.xmlrpc.xmlrpc.server;

import java.io.Serializable;

import org.opencv.core.Rect;

public class Frame implements Serializable {

  private static final long serialVersionUID = 3868480217185175084L;

  byte[] mat;
  Face[] faces;

  public Frame(byte[] mat, Rect[] faces) {
    this.faces = new Face[faces.length];
    for (int i = 0; i < faces.length; i++) {
      Rect face = faces[i];
      this.faces[i] = new Face(face.width, face.height, face.x, face.y);
    }
  }

  public class Face implements Serializable {

    private static final long serialVersionUID = -8538775893202958406L;

    int width;
    int height;
    int x;
    int y;

    public Face(int width, int height, int x, int y) {
      this.width = width;
      this.height = height;
      this.x = x;
      this.y = y;
    }
  }
}
