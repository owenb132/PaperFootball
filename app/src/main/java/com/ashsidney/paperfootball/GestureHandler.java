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
    assert listener != null;
    listeners.add(0, listener);
  }

  public synchronized void remove (Listener listener)
  {
    assert listener != null;
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
