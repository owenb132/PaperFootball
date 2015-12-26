package com.ashsidney.paperfootball;

public class GestureEvent
{
  public enum EventType { Touch, Transform, Done };
  
  public GestureEvent (EventType evType, Transformation transform)
  {
    eventType = evType;
    this.transform = transform;
  }
  
  public EventType getType ()
  {
    return eventType;
  }
  
  public Transformation getTransformation ()
  {
    return transform;
  }
  
  
  protected EventType eventType;
  protected Transformation transform;
}
