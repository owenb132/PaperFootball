package com.ashsidney.paperfootball;


import java.util.ArrayList;
import java.util.Random;

/**
 * Zakladna trieda pre hraca umelej inteligencie.
 */
public class BasicAIPlayer extends BasePlayer
{
  @Override
  protected void startCalc ()
  {
    worker = createWorker();
    worker.start();
  }

  // vlozena trieda pre vypocet krokov hraca
  protected class SimpleAIWorker extends Thread
  {
    public SimpleAIWorker (BasePlayer player)
    {
      this.player = player;
      stepCycle = 2 * player.getStepCount() + (player.isAttacker() ? -1 : 1);
      if (player.isAttacker())
      {
        int dist = player.game.getBall().getNeigborsDistance();
        preferDistance = 2 * stepCycle + rnd.nextInt(stepCycle) < dist;
      }
      for (int i = 0; i < player.getStepCount(); ++i)
        currDirections.add(0);
    }

    @Override
    public void run ()
    {
      findSolution(player.getStepCount());
      ArrayList<Integer> moves = getSolution();
      for (int i = 0; i < moves.size(); ++i)
        player.game.playerMove(player, moves.get(i));
    }

    protected void findSolution (int steps)
    {
      if (steps > 0)
        for (int i = 0; i < 4; ++i)
        {
          if (player.game.testMove(player.getPlayerID(), i, steps == 1))
          {
            currDirections.set(currIndex++, i);
            findSolution(steps - 1);
            --currIndex;
            player.game.testBack();
          }
        }
      else
      {
        PositionRating currRat = new PositionRating(player.game.getTest().getNeigborsDistance(),
            player.getStepCount(), stepCycle, player.isAttacker());

        processRating(currRat);

        if (currRat.better(rating))
        {
          directions.clear();
          rating.set(currRat);
        }
        if (currRat.equal(rating))
          directions.add(new ArrayList<>(currDirections));
      }
    }

    protected ArrayList<Integer> getSolution ()
    {
      if (BuildConfig.DEBUG && directions.isEmpty())
        throw new AssertionError("Solution not found");

      return directions.get(rnd.nextInt(directions.size()));
    }

    protected void processRating (PositionRating rat)
    {
      if (preferDistance)
        rat.swap();
    }

    protected class PositionRating
    {
      public PositionRating ()
      {
        rating1 = rating2 = Integer.MAX_VALUE;
      }

      public PositionRating (int dist, int steps, int stepCycle, boolean isAttacker)
      {
        int modDist = (dist + steps) % stepCycle;
        int modRating = isAttacker ? 2 : 0;
        if (modDist == steps)
          modRating = isAttacker ? 0 : 1;
        else if (modDist == 0)
          modRating = isAttacker ? 1 : 2;
        if (dist == Integer.MAX_VALUE)
          modRating = isAttacker ? 3 : 0;
        else if (dist == 0 && !isAttacker)
          modRating = 3;
        if (!isAttacker)
          dist = -dist;
        rating1 = modRating;
        rating2 = dist;
      }

      public void set (PositionRating obj)
      {
        rating1 = obj.rating1;
        rating2 = obj.rating2;
      }

      public boolean equal (PositionRating obj)
      {
        return rating1 == obj.rating1 && rating2 == obj.rating2;
      }

      public boolean better (PositionRating obj)
      {
        return rating1 < obj.rating1 || rating1 == obj.rating1 && rating2 < obj.rating2;
      }

      public void setRating1 (int rat)
      {
        rating1 = rat;
      }

      public void swap ()
      {
        int tmp = rating1;
        rating1 = rating2;
        rating2 = tmp;
      }

      protected int rating1;
      protected int rating2;
    }

    protected BasePlayer player;
    protected int stepCycle;
    protected boolean preferDistance = false;
    Random rnd = new Random();

    protected PositionRating rating = new PositionRating();
    protected ArrayList<ArrayList<Integer>> directions = new ArrayList<>();

    protected ArrayList<Integer> currDirections = new ArrayList<>();
    protected int currIndex = 0;
  }

  protected SimpleAIWorker createWorker ()
  {
    return new SimpleAIWorker(this);
  }

  protected SimpleAIWorker worker;
}
