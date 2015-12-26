package com.ashsidney.paperfootball;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;


public class AnimSprite extends Sprite
{
  public AnimSprite (int spriteID, float width, Resources res)
  {
    super(spriteID, width, res);
  }
  
  protected class Animation
  {
    public Animation (int[] spriteID, Resources res, float time)
    {
      animTime = time;
      for (int i = 0; i < spriteID.length; ++i)
        bitmaps.add(loadBitmap(spriteID[i], res));
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
    
    protected ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    protected float animTime;
  }
  
  public void addAnim (int[] spriteID, Resources res, float animTime)
  {
    animation.add(new Animation(spriteID, res, animTime));
  }
  
  @Override
  public void draw (Canvas canvas, float currTime)
  {
    if (currTime > startTime + animTime && noRepeat)
      super.draw(canvas, currTime);
    else
    {
      int animSize = animation.get(animSequence).getBitmapSize();
      int currIdx = (int)((currTime - startTime) / animTime * (animSize + 1)) % (animSize + 1);
      if (currIdx == 0)
        super.draw(canvas, currTime);
      else
      {
        if (forward)
          --currIdx;
        else
          currIdx = animSize - currIdx;
        draw(canvas, animation.get(animSequence).getBitmap(currIdx));
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
  
  protected ArrayList<Animation> animation = new ArrayList<Animation>();
  
  protected float startTime = 0.0f;
  protected float animTime = 0.0f;
  protected int animSequence = 0;
  protected boolean forward = true;
  protected boolean noRepeat = true;
}
