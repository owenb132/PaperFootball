package com.ashsidney.paperfootball;

public class BallAnimation
{
  public BallAnimation (GameNode start, GameNode end)
  {
    startNode = start;
    endNode = end;
    float[] diff = { endNode.getPosition()[0] - startNode.getPosition()[0],
      endNode.getPosition()[1] - startNode.getPosition()[1] };
    sequence = (Math.abs(diff[0]) > Math.abs(diff[1])) ? 0 : 1;
    forward = (diff[sequence] > 0.0f) == (sequence == 0); 
  }
  
  public GameNode getStartNode ()
  {
    return startNode;
  }
  
  public boolean isStarted ()
  {
    return started;
  }
  
  public boolean isEnded (float currTime)
  {
    return started && currTime > startTime + animTime;
  }
  
  public void setAnimTime (float startTime, float animTime)
  {
    if (animTime > 0.0f)
    {
      this.startTime = startTime;
      this.animTime = animTime;
      started = true;
    }
  }
  
  float[] getPosition (float currTime)
  {
    currTime = (currTime - startTime) / animTime;
    if (currTime > 1.0f)
      currTime = 1.0f;
    float[] pos = new float[2];
    for (int i = 0; i < 2; ++i)
      pos[i] = startNode.getPosition()[i] * (1.0f - currTime) + endNode.getPosition()[i] * currTime; 
    return pos;
  }
  
  public int getSequence ()
  {
    return sequence;
  }
  
  public boolean getForward ()
  {
    return forward;
  }
  
  protected GameNode startNode;
  protected GameNode endNode;
  
  protected float startTime = 0.0f;
  protected float animTime = 1.0f;
  protected boolean started = false;
  
  protected int sequence;
  protected boolean forward;
}
