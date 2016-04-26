package com.ashsidney.paperfootball;

/**
 * Base player class
 */
public class BasePlayer
{
  protected void init (boolean attacker, int steps)
  {
    this.attacker = attacker;
    moveStepCount = steps;
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

  public int getPlayerID () { return attacker ? 2 : 1; }

  protected void startCalc ()
  {}

  protected boolean attacker;
  protected int moveStepCount = 0;
  protected int moveInfo = 0;

  protected Game game = null;

  public static final float[][] directions = { {1.0f, 0.0f},  {0.0f, -1.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f} };
}
