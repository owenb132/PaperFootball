package com.ashsidney.paperfootball;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;


public class Transformation
{
  public Transformation ()
  {
    translation[0] = 0.0f;
    translation[1] = 0.0f;
  }
  
  public Transformation (float transX, float transY, float zoom, float rotation)
  {
    translation[0] = transX;
    translation[1] = transY;
    this.zoom = zoom;
    this.rotation = rotation;
  }
  
  public Transformation (Transformation transform)
  {
    set(transform);
  }

  public float[] getTranslation ()
  {
    return translation;
  }
  
  public float getZoom ()
  {
    return zoom;
  }
  
  public float getRotation ()
  {
    return rotation;
  }
  
  public void set (Transformation transform)
  {
    translation[0] = transform.translation[0];
    translation[1] = transform.translation[1];
    zoom = transform.zoom;
    rotation = transform.rotation;
  }
  
  public void set (Matrix mat)
  {
    float vals[] = new float[9];
    mat.getValues(vals);
    translation[0] = vals[2];
    translation[1] = vals[5];
    setZoomRotation(vals[0], vals[3]);
  }
  
  public void add (Transformation transform)
  {
    add(transform.getMatrix());
  }
  
  public void add (Matrix mat)
  {
    Matrix thisMat = getMatrix();
    thisMat.postConcat(mat);
    set(thisMat);
  }

  public void addToTranslation (Matrix mat)
  {
    mat.mapPoints(translation);
  }
  
  public Matrix getMatrix ()
  {
    Matrix mat = new Matrix();
    mat.preTranslate(translation[0], translation[1]);
    mat.preScale(zoom, zoom);
    mat.preRotate(rotation);
    return mat;
  }
  
  public Matrix getInvertMatrix ()
  {
    Matrix invMat = new Matrix();
    invMat.postTranslate(-translation[0], -translation[1]);
    invMat.postScale(1.0f / zoom, 1.0f / zoom);
    invMat.postRotate(-rotation);
    return invMat;
  }
  
  public boolean set (float pntSX, float pntSY, float pntEX, float pntEY)
  {
    translation[0] = pntEX - pntSX;
    translation[1] = pntEY - pntSY;
    zoom = 1.0f;
    rotation = 0.0f;
    return true;
  }
  
  public boolean set (float pntS1X, float pntS1Y, float pntE1X, float pntE1Y,
    float pntS2X, float pntS2Y, float pntE2X, float pntE2Y)
  {
    float difSX = pntS1X - pntS2X;
    float difSY = pntS1Y - pntS2Y;
    float difEX = pntE1X - pntE2X;
    float difEY = pntE1Y - pntE2Y;
    float dotDifS = difSX * difSX + difSY * difSY;
    if (dotDifS <= 0.0f)
      return false;
    float zoomCos = (difSX * difEX + difSY * difEY) / dotDifS;
    float zoomSin;
    if (Math.abs(difSX) > Math.abs(difSY))
      zoomSin = (difEY - difSY * zoomCos) / difSX;
    else
      zoomSin = (difSX * zoomCos - difEX) / difSY;
    translation[0] = pntE1X - zoomCos * pntS1X + zoomSin * pntS1Y;
    translation[1] = pntE1Y - zoomSin * pntS1X - zoomCos * pntS1Y;
    setZoomRotation(zoomCos, zoomSin);
    return true;
  }
  
  public void draw (Canvas canvas)
  {
    canvas.translate(translation[0], translation[1]);
    canvas.scale(zoom, zoom);
    canvas.rotate(rotation);
  }
  
  public boolean limitZoom (float[] limits)
  {
    if (zoom < limits[0])
    {
      zoom = limits[0];
      return false;
    }
    else if (zoom > limits[1])
    {
      zoom = limits[1];
      return false;
    }
    return true;
  }
  
  public void mix (Transformation other, float param)
  {
    float difRot = other.rotation - rotation;
    if (difRot < -180.0f)
      difRot += 360.0f;
    else if (difRot > 180.0f)
      difRot -= 360.0f;
    
    translation[0] += param * (other.translation[0] - translation[0]);
    translation[1] += param * (other.translation[1] - translation[1]);
    zoom += param * (other.zoom - zoom);
    rotation += param * difRot;
  }
  
  protected void setZoomRotation (float zoomCos, float zoomSin)
  {
    zoom = (float)Math.sqrt(zoomCos * zoomCos + zoomSin * zoomSin);
    rotation = (float)(Math.atan2(zoomSin, zoomCos) * 180.0f / Math.PI);
  }

  public void log ()
  {
    Log.d("PaperFootball", "loc:" + Float.toString(translation[0])
        + " " + Float.toString(translation[1])
        + " rot:" + Float.toString(rotation)
        + " zoom:" + Float.toString(zoom));
  }

  
  protected float[] translation = new float[2];
  protected float zoom = 32.0f;
  protected float rotation = 0.0f;
}
