package com.ashsidney.paperfootball;

import android.view.MotionEvent;

import java.util.ArrayList;


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
  
  public synchronized void add (Listener listener)
  {
    if (BuildConfig.DEBUG && listener == null)
      throw new AssertionError("Null listener");

    listeners.add(0, listener);
  }

  public synchronized void remove (Listener listener)
  {
    if (BuildConfig.DEBUG && listener == null)
      throw new AssertionError("Null listener");

    listeners.remove(listener);
  }

  protected synchronized void sendGesture (GestureEvent event)
  {
    for (Listener lst : listeners)
      if (lst.onGesture(event))
        break;
  }

  protected ArrayList<Listener> listeners = new ArrayList<>();
}
