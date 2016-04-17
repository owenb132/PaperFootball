package com.ashsidney.paperfootball;

/**
 * UserPlayer je trieda pre ludskeho hraca.
 */
public class UserPlayer extends BasePlayer
{
  @Override
  public boolean onGesture (GestureEvent event)
  {
    float[] ballPosition = game.getBall().getPosition();
    float[] touch = event.getTransformation().getTranslation();
    for (int i = 0; i < 2; ++i)
      touch[i] -= ballPosition[i];

    int bestDir = -1;
    float bestVal = 0.0f;
    for (int i = 0; i < 4; ++i)
    {
      float dotVal = touch[0] * directions[i][0] + touch[1] * directions[i][1];
      if (dotVal > bestVal )
      {
        bestDir = i;
        bestVal = dotVal;
      }
    }

    if (bestDir >= 0)
    {
      game.playerMove(this, bestDir);
      return true;
    }
    return false;
  }
}
