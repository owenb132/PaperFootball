package com.ashsidney.paperfootball;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Sprite
{
  public Sprite ()
  {}
  
  public Sprite (int spriteID, float width, Resources res)
  {
    bitmap = loadBitmap(spriteID, res);
    initScale(width);
  }
  
  public void draw (Canvas canvas, float currTime)
  {
    draw(canvas, bitmap);
  }
  
  protected void draw (Canvas canvas, Bitmap bitmap)
  {
    canvas.drawBitmap(bitmap, fullTransform, new Paint());
  }
  
  public void setTransform (Matrix trans)
  {
    transform = trans;
    calcFullTransform();
  }
  
  public void setPosition (float[] position)
  {
    Matrix trans = new Matrix();
    trans.preTranslate(position[0], position[1]);
    setTransform(trans);
  }
  
  public int getWidth ()
  {
    return bitmap != null ? bitmap.getWidth() : 0;
  }
  
  public int getHeight ()
  {
    return bitmap != null ? bitmap.getHeight() : 0;
  }
  
  
  protected Bitmap loadBitmap (int spriteID, Resources res)
  {
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inScaled = false;
    return BitmapFactory.decodeResource(res, spriteID, opts);
  }
  
  protected void initScale (float width)
  {
    float scale = width / bitmap.getWidth();
    scaleMat.preScale(scale, scale);
    scaleMat.preTranslate(-bitmap.getWidth() * 0.5f, -bitmap.getHeight() * 0.5f);
    
    calcFullTransform();
  }
  
  protected void calcFullTransform ()
  {
    fullTransform.set(scaleMat);
    if (transform != null)
      fullTransform.postConcat(transform);
  }
  
  protected Bitmap bitmap = null;
  protected Matrix scaleMat = new Matrix();
  protected Matrix transform = null;
  protected Matrix fullTransform = new Matrix();
}
