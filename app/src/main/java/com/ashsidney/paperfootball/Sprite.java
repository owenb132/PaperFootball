package com.ashsidney.paperfootball;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Sprite implements XMLHelper.ConfigOwner
{
  public void load (XMLHelper xml)
      throws XmlPullParserException, Resources.NotFoundException, IOException
  {
    itemID = xml.getAttributeID("id");
    xml.loadChildNodes(this);
  }

  public boolean createChild (XMLHelper xml)
    throws IOException, XmlPullParserException
  {
    switch (xml.parser.getName())
    {
      case BitmapTag:
        bitmap = loadBitmap(xml.getAttributeID("id"), xml.resources);
        return true;
      case WidthTag:
        initScale(xml.getAttributeFloat("value"));
        return true;
    }
    return false;
  }

  public void draw (Canvas canvas, float currTime)
  {
    draw(canvas, bitmap);
  }
  
  protected void draw (Canvas canvas, Bitmap bitmap)
  {
    canvas.drawBitmap(bitmap, fullTransform, new Paint());
  }

  public int getID ()
  {
    return itemID;
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
  
  public float getWidth ()
  {
    return bitmap != null ? bitmap.getWidth() * getScale() : 0;
  }
  
  public float getHeight ()
  {
    return bitmap != null ? bitmap.getHeight() * getScale() : 0;
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

  protected float getScale ()
  {
    float vec[] = { 1.0f, 1.0f };
    scaleMat.mapVectors(vec);
    return vec[0];
  }

  protected void calcFullTransform ()
  {
    fullTransform.set(scaleMat);
    if (transform != null)
      fullTransform.postConcat(transform);
  }

  protected int itemID = 0;
  protected Bitmap bitmap = null;
  protected Matrix scaleMat = new Matrix();
  protected Matrix transform = null;
  protected Matrix fullTransform = new Matrix();

  public static final String BitmapTag = "bitmap";
  public static final String WidthTag = "width";
}
