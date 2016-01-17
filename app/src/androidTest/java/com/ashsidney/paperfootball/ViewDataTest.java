package com.ashsidney.paperfootball;

import junit.framework.TestCase;

/**
 * Testy triedy ViewData.
 */
public class ViewDataTest extends TestCase
{
  public void testTouchEventTransformation() throws Exception
  {
    ViewData testObj = new ViewData();
    testObj.setRenderer(new Renderer());
    ViewTransformation testTrans = testObj.getTransformation();
    testTrans.set(new Transformation(0.0f, 0.0f, 50.0f, 0.0f));

    GestureEvent evt = new GestureEvent(GestureEvent.EventType.Touch, new Transformation(20.0f, 40.0f, 1.0f, 0.0f));
    testObj.onGesture(evt);

    assertEquals(0.4f, evt.getTransformation().getTranslation()[0], 0.001f);
    assertEquals(0.8f, evt.getTransformation().getTranslation()[1], 0.001f);
    assertEquals(1.0f, evt.getTransformation().getZoom(), 0.001f);
    assertEquals(0.0f, evt.getTransformation().getRotation(), 0.001f);
  }
}
