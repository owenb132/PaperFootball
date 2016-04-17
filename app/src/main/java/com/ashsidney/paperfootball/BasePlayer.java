package com.ashsidney.paperfootball;

/**
 * Base player class
 */
public class BasePlayer
{
  protected void init (boolean attacker, int steps, int info)
  {
    this.attacker = attacker;
    moveStepCount = steps;
    moveInfo = info;
  }

  public int setCurrent (Game game)
  {
    this.game = game;
    if (game != null)
    {
      if (moveInfo != 0)
        InfoHandler.showInfo(R.id.stavOznam, moveInfo, 1.0f);
      startCalc();
    }
    return moveStepCount;
  }

  public boolean onGesture (GestureEvent event)
  {
    return false;
  }

  public boolean isAttacker ()
  {
    return attacker;
  }

  public boolean isDefender ()
  {
    return !attacker;
  }

  public int getStepCount ()
  {
    return moveStepCount;
  }

  protected void startCalc ()
  {}

  private boolean attacker;
  private int moveStepCount = 0;
  private int moveInfo = 0;

  protected Game game = null;

  public static final float[][] directions = { {1.0f, 0.0f},  {0.0f, -1.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f} };
}
