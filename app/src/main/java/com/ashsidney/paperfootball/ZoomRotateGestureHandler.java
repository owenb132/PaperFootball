package com.ashsidney.paperfootball;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ZoomRotateGestureHandler extends GestureHandler implements GestureDetector.OnGestureListener
{
  public ZoomRotateGestureHandler (Context context, View view)
  {
    detector = new GestureDetector(context, this);
    this.view = view;
  }
  
  
  protected GestureDetector detector;
  protected View view = null;
  protected Point viewOffset = null;
  
  protected float[][] position = new float[4][2];
  protected int currentIndex = 0;
  protected int endIndex = 0;
  protected boolean touchEnabled = true;
  
    
  @Override
  public boolean onDown (MotionEvent e)
  {
    currentIndex = 0;
    return true;
  }
  
  @Override
  public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX,
      float velocityY)
  {
    return false;
  }
  
  @Override
  public void onLongPress (MotionEvent e)
  {
  }
  
  @Override
  public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX,
      float distanceY)
  {
    if (currentIndex == 1 && endIndex == 2)
    {
      Transformation transform = new Transformation();
      transform.set(position[0][0], position[0][1],position[1][0], position[1][1]);
      GestureEvent gesture = new GestureEvent(GestureEvent.EventType.Transform, transform);
      sendGesture(gesture);
      return true;
    }
    return false;
  }
  
  @Override
  public void onShowPress (MotionEvent event)
  {
  }
  
  @Override
  public boolean onSingleTapUp (MotionEvent event)
  {
    if (event.getPointerCount() > 0 && touchEnabled)
    {
      Transformation transform = new Transformation(event.getX(0) - viewOffset.x, event.getY(0) - viewOffset.y, 1.0f, 0.0f);
      GestureEvent gesture = new GestureEvent(GestureEvent.EventType.Touch, transform);
      sendGesture(gesture);
      /*ABLog.d ("show press " + Float.toString( transform.getTranslation()[0]) + " " + Float.toString( transform.getTranslation()[1]));*/
      return true;
    }
    touchEnabled = true;
    return false;
  }

  @Override
  public boolean onTouchEvent (MotionEvent event)
  {
    // check view offset
    if (viewOffset == null)
    {
      Rect rect = new Rect();
      viewOffset = new Point();
      view.getGlobalVisibleRect(rect, viewOffset);
    }
	  // ziskaj typ akcie
    int action = event.getActionMasked();
    // spracuj len pohybove akcie
    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_MOVE)
    {
      //printSamples(event);
      // ziskaj nove suradnice
      int filled = 0;
      for (int i = 0; i < event.getPointerCount(); ++i)
      {
        int pIdx = event.getPointerId(i);
        if (pIdx < 2)
        {
          position[currentIndex + filled][0] = event.getX(i) - viewOffset.x;
          position[currentIndex + filled][1] = event.getY(i) - viewOffset.y;
          ++filled;
        }
      }
      // ukonci predosly pohyb, ak sa zmenil pocet dotykov
      if (currentIndex > 0 && (currentIndex != filled || action == MotionEvent.ACTION_UP))
      {
        sendGesture(new GestureEvent(GestureEvent.EventType.Done, null));
        for (int i = 0; i < filled; ++i)
          for (int j = 0; j < 2; ++j)
            position[i][j] = position[i + currentIndex][j];
        touchEnabled = currentIndex < 2;
        currentIndex = 0;
      }
      endIndex = currentIndex + filled;
      // over prijate udaje, ak ide o dva dotyky, vytvor transformaciu
      if (currentIndex == 0)
      {
        currentIndex = filled;
      }
      else if (currentIndex == 2 && endIndex == 4)
      {
        Transformation transform = new Transformation();
        transform.set(position[0][0], position[0][1], position[2][0], position[2][1],
          position[1][0], position[1][1], position[3][0], position[3][1]);
        GestureEvent gesture = new GestureEvent(filled < 2 ? GestureEvent.EventType.Done : GestureEvent.EventType.Transform, transform);
        sendGesture(gesture);
      }
    }
    return detector.onTouchEvent(event);
  }

  void printSamples(MotionEvent ev) {
    Log.d("PaperFootball", "event content");
    final int historySize = ev.getHistorySize();
    final int pointerCount = ev.getPointerCount();
    for (int h = 0; h < historySize; h++) {
        Log.d("PaperFootball", "At time:" + ev.getHistoricalEventTime(h));
        for (int p = 0; p < pointerCount; p++) {
            Log.d("PaperFootball", "  pointer " +
                ev.getPointerId(p) + ":" + ev.getHistoricalX(p, h) + " " + ev.getHistoricalY(p, h));
        }
    }
    Log.d("PaperFootball", "At time " + ev.getEventTime());
    for (int p = 0; p < pointerCount; p++) {
      Log.d("PaperFootball", "  pointer " +
            ev.getPointerId(p) + ":" + ev.getX(p) + " " + ev.getY(p));
    }
  }
}
