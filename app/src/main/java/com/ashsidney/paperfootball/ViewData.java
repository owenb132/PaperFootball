package com.ashsidney.paperfootball;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;


public class ViewData implements GestureHandler.Listener
{
  public ViewData ()
  {
    sizes[0] = 0;
    sizes[1] = 0;
  }

  public synchronized RectF draw (Canvas canvas, float currTime)
  {
    if (animTime > 0.0f)
    {
      animTime -= currTime - lastTime;
      if (animTime < 0.0f)
        animTime = 0.0f;
      currentTransform.set(correctTransform);
      currentTransform.mix(rawTransform, animTime / fullAnimTime);
    }
    lastTime = currTime;

    currentTransform.draw(canvas);
    
    return getVisibleWorld();
  }
  
  public int getWidth ()
  {
    return sizes[0];
  }
  
  public int getHeight ()
  {
    return sizes[1];
  }
  
  public void setSizes (int width, int height)
  {
    animTime = 0.0f;
    currentTransform.sizesChanged(width, height);
    lastTransform.set(currentTransform);
    sizes[0] = width;
    sizes[1] = height;
  }
  
  public ViewTransformation getTransformation ()
  {
    return currentTransform;
  }
  
  public void setGameRect (RectF gameRect)
  {
    this.gameWorld = gameRect;
  }
  
  public RectF getVisibleWorld ()
  {
    return getTransformation().getVisibleWorld();
  }
  
  public void setRenderer (Renderer rend)
  {
    renderer = rend;
  }

  public void reset ()
  {
    currentTransform = new ViewTransformation();
    setSizes(sizes[0], sizes[1]);
  }

  protected boolean isAnimation ()
  {
    return animTime > 0.0f;
  }
  
  @Override
  public synchronized boolean onGesture(GestureEvent event)
  {
    GestureEvent.EventType evType = event.getType();
    if (evType == GestureEvent.EventType.Transform)
    {
      rawTransform.set(lastTransform);
      rawTransform.add(event.getTransformation());
      boolean correct = currentTransform.correctView(rawTransform, gameWorld, correctTransform);
      if (correct || animWas)
      {
        currentTransform.set(correctTransform);
        if (correct)
          animWas = false;
      }
      else
      {
        animTime = fullAnimTime;
        animWas = true;
      }
    }
    if (evType == GestureEvent.EventType.Done)
    {
      lastTransform.set(correctTransform);
      animWas = animWas && animTime > 0.0f;
    }
    
    if (evType == GestureEvent.EventType.Touch)
    {
      event.getTransformation().addToTranslation(currentTransform.getInvertMatrix());
      return false;
    }
    renderer.doRedraw();
    return true;
  }
  
  protected int[] sizes = new int[2];
  protected ViewTransformation lastTransform = new ViewTransformation();
  protected ViewTransformation currentTransform = new ViewTransformation();
  protected Transformation rawTransform = new Transformation();
  protected Transformation correctTransform = new Transformation();
  protected float lastTime = 0.0f;
  protected float animTime = 0.0f;
  protected float fullAnimTime = 1.0f;
  protected boolean animWas = false;
  protected RectF gameWorld = new RectF(-1, -1, 1, 1);
  protected Renderer renderer;
}
