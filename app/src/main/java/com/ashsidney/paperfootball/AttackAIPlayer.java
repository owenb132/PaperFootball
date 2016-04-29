package com.ashsidney.paperfootball;

/**
 * Lepsia AI pre utocnika.
 */
public class AttackAIPlayer extends BasicAIPlayer
{
  // vlozena trieda pre vypocet krokov utocnika - pridane vyhybanie sa slepym ulickam
  protected class AttackAIWorker extends SimpleAIWorker
  {
    public AttackAIWorker (BasePlayer player)
    {
      super(player);
    }

    @Override
    protected void processRating (PositionRating rat)
    {
      super.processRating(rat);

      if (player.game.getTest().atDeadEnd() && player.isAttacker())
        rat.setRating1(Integer.MAX_VALUE);
    }
  }

  protected SimpleAIWorker createWorker ()
  {
    return new AttackAIWorker(this);
  }
}
