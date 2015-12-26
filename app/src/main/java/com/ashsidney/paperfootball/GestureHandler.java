package com.ashsidney.paperfootball;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;


public class GestureHandler
{

  public interface Listener
  {
    boolean onGesture (GestureEvent event);
  }
  
  public boolean onTouchEvent (MotionEvent event)
  {
    return false;
  }
  
  public void addTransformer (Listener listener)
  {
    if (listener != null)
    {
      if (currTransformer != null)
        transformers.add(currTransformer);
      currTransformer = listener;
    }
  }

  public void removeTransformer ()
  {
    if (transformers.isEmpty())
      currTransformer = null;
    else
    {
      int lastIdx = transformers.size() - 1;
      currTransformer = transformers.get(lastIdx);
      transformers.remove(lastIdx);
    }
  }
  
  public void addConsumer (Listener listener)
  {
    if (listener != null)
      consumers.add(listener);
  }
  
  public void removeConsumer (Listener listener)
  {
    int lastIdx = consumers.lastIndexOf(listener);
    if (lastIdx >= 0)
      consumers.remove(lastIdx);
  }

  protected void sendGesture (GestureEvent event)
  {
    if (currTransformer != null)
      currTransformer.onGesture(event);
    for (Listener consumer : consumers)
      consumer.onGesture(event);
  }


  protected Listener currTransformer = null;
  protected ArrayList<Listener> transformers = new ArrayList<>();

  protected ArrayList<Listener> consumers = new ArrayList<>();
}
