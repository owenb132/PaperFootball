package com.ashsidney.paperfootball;

import android.graphics.Matrix;
import android.graphics.RectF;


public class ViewTransformation extends Transformation
{
  public ViewTransformation ()
  {
    super();
    init();
  }
  
  public ViewTransformation (float transX, float transY, float zoom, float rotation)
  {
    super(transX, transY, zoom, rotation);
    init();
  }
  
  protected void init ()
  {
    sizes[0] = 0;
    sizes[1] = 0;
    zoomLimits[0] = 32.0f;
    zoomLimits[1] = 4096.0f;
  }
  
  public ViewTransformation (ViewTransformation transform)
  {
    set(transform);
  }
  
  public void set (ViewTransformation transform)
  {
    super.set(transform);
    sizes[0] = transform.sizes[0];
    sizes[1] = transform.sizes[1];
    zoomLimits[0] = transform.zoomLimits[0];
    zoomLimits[1] = transform.zoomLimits[1];
  }
   
  public void sizesChanged (int width, int height)
  {
    translation[0] += (width - sizes[0]) * 0.5f;
    translation[1] += (height - sizes[1]) * 0.5f;
    sizes[0] = width;
    sizes[1] = height;
    zoomLimits[1] = (width > height ? height : width) * 0.4f;
  }
 
  public RectF getVisibleWorld ()
  {
    Matrix transform = getInvertMatrix();
    RectF world = new RectF(0.0f, 0.0f, sizes[0], sizes[1]);
    transform.mapRect(world);
    return world;
  }
  
  public boolean correctView (Transformation rawTrans, RectF area, Transformation corrTrans)
  {
    corrTrans.set(rawTrans);
    boolean valid = corrTrans.limitZoom(zoomLimits);

    // rohove body hracej plochy
    float[] corners = { area.left, area.top, area.left, area.bottom,
      area.right, area.bottom, area.right, area.top, area.left, area.top };
    corrTrans.getMatrix().mapPoints(corners);
    
    // kontrola, ci su body hracej plochy viditelne
    float[] borders = { 0.0f, 0.0f, sizes[1], sizes[0] };
    for (int i = 0; i < 8; i += 2)
      if (corners[i] >= borders[1] && corners[i] <= borders[3]
          && corners[i + 1] >= borders[0] && corners[i + 1] <= borders[2])
        return valid;
    
    // vypocet vektorov okrajov hracej plochy
    float[] vectors = new float[8];
    for (int i = 0; i < 8; ++i)
      vectors[i] = corners[i + 2] - corners[i];
    
    // kontrola, ci su viditelne okraje hracej plochy
    float[] params = new float[2];
    for (int bi = 0; bi < 4; ++bi)
    {
      int si = bi % 2;
      params[0] = Float.MAX_VALUE;
      params[1] = -Float.MAX_VALUE;
      for (int ci = 0; ci < 4; ++ci)
      {
        int cix = 2 * ci + si;
        int ciy = 2 * ci + 1 - si;
        if (vectors[ciy] != 0.0f)
        {
          float t = (borders[bi] - corners[ciy]) / vectors[ciy];
          float u = (vectors[cix] * t + corners[cix]) / sizes[si];
          if (t >= 0.0f && t <= 1.0f)
          {
            if (u < params[0])
              params[0] = u;
            if (u > params[1])
              params[1] = u;
          }
        }
      }
      if (params[0] <= 1.0f && params[1] >= 0.0f)
        return valid;
    }

    // vypocitaj posun k okraju displeja
    float[] shift = { 0.0f, 0.0f, Float.MAX_VALUE };
    float[] points = new float[8];
    int[] iBords = { 1, 0, 1, 2, 3, 2, 3, 0, 1, 0 };
    for (int ci = 0; ci < 8; ci += 2)
    {
      for (int i = 0; i < 4; ++i)
        points[i] = corners[i + ci];
      for (int bi = 0; bi < 8; bi += 2)
      {
        for (int i = 0; i < 4; ++i)
          points[i + 4] = borders[iBords[i + bi]];
        calcDistance(points, shift);
      }
    }
    for (int i = 0; i < 2; ++ i)
      corrTrans.getTranslation()[i] += shift[i];

    return false;
  }

  private static void calcDistance (float[] points, float[] result)
  {
    float[] vecs = { points[2] - points[0], points[3] - points[1], points[6] - points[4], points[7] - points[5] };
    for (int lineIdx = 0; lineIdx < 8; lineIdx += 4)
    {
      int vecIdx = lineIdx / 2;
      float sign = 1.0f - vecIdx;
      for (int i = 0; i < 4; i += 2)
      {
        int pntIdx = i + (lineIdx + 4) % 8;
        float[] diff = { points[pntIdx] - points[lineIdx], points[pntIdx + 1] - points[lineIdx + 1] };
        float vv = vecs[vecIdx] * vecs[vecIdx] + vecs[vecIdx + 1] * vecs[vecIdx + 1];
        if (vv > 0.0f)
        {
          float t = (diff[0] * vecs[vecIdx] + diff[1] * vecs[vecIdx + 1]) / vv;
          if (t > 0.0f)
          {
            if (t > 1.0f)
              t = 1.0f;
            for (int j = 0; j < 2; ++j)
              diff[j] -= t * vecs[vecIdx + j];
          }
        }
        float dist = (float)Math.sqrt(diff[0] * diff[0] + diff[1] * diff[1]);
        if (dist < result[2])
        {
          for (int j = 0; j < 2; ++j)
            result[j] = sign * diff[j];
          result[2] = dist;
        }
      }
    }
  }


  protected int[] sizes = new int[2];
  protected float[] zoomLimits = new float[2];
}
