package com.ashsidney.paperfootball;

import java.io.IOException;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.xmlpull.v1.XmlPullParserException;


public class AnimSprite extends Sprite
{
  @Override
  public boolean createChild (XMLHelper xml)
    throws IOException, XmlPullParserException
  {
    if (super.createChild(xml))
      return true;
    switch (xml.parser.getName())
    {
      case AnimationTag:
        Animation anim = new Animation();
        anim.load(xml);
        animation.add(anim);
        return true;
    }
    return false;
  }

  protected class Animation implements XMLHelper.ConfigOwner
  {
    public void load (XMLHelper xml)
      throws IOException, XmlPullParserException
    {
      animTime = xml.getAttributeFloat("time");
      xml.loadChildNodes(this);
    }

    @Override
    public boolean createChild (XMLHelper xml)
      throws IOException, XmlPullParserException
    {
      switch (xml.parser.getName())
      {
        case BitmapTag:
          bitmaps.add(loadBitmap(xml.getAttributeID("id"), xml.resources));
          return true;
      }
      return false;
    }
    
    public float getAnimTime ()
    {
      return animTime;
    }
    
    public int getBitmapSize ()
    {
      return bitmaps.size();
    }
    
    public Bitmap getBitmap (int index)
    {
      return bitmaps.get(index);
    }
    
    protected ArrayList<Bitmap> bitmaps = new ArrayList<>();
    protected float animTime;
  }

  @Override
  public void draw (Canvas canvas, Paint paint, float currTime)
  {
    if (currTime > startTime + animTime && noRepeat)
      super.draw(canvas, paint, currTime);
    else
    {
      int animSize = animation.get(animSequence).getBitmapSize();
      int currIdx = (int)((currTime - startTime) / animTime * (animSize + 1)) % (animSize + 1);
      if (currIdx == 0)
        super.draw(canvas, paint, currTime);
      else
      {
        if (forward)
          --currIdx;
        else
          currIdx = animSize - currIdx;
        draw(canvas, animation.get(animSequence).getBitmap(currIdx), paint);
      }
    }
  }
  
  public boolean startAnim (int seq, float start, boolean fwd, boolean noRep)
  {
    if (seq < animation.size())
    {
      animSequence = seq;
      animTime = animation.get(animSequence).getAnimTime();
      startTime = start;
      forward = fwd;
      noRepeat = noRep;
      return true;
    }
    return false;
  }
  
  public float getAnimTime ()
  {
    return animTime;
  }
  
  protected ArrayList<Animation> animation = new ArrayList<>();
  
  protected float startTime = 0.0f;
  protected float animTime = 0.0f;
  protected int animSequence = 0;
  protected boolean forward = true;
  protected boolean noRepeat = true;

  public static final String AnimationTag = "animation";

}
