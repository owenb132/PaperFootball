package com.ashsidney.paperfootball;


import java.util.ArrayList;
import java.util.Random;

/**
 * Zakladna trieda pre hraca umelej inteligencie.
 */
public class SimpleAIPlayer extends BasePlayer
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
        preferDistance = dist > 0 && rnd.nextInt(dist) < dist - stepCycle;
      }
      for (int i = 0; i < player.getStepCount(); ++i)
        currDirections.add(0);
    }

    @Override
    public void run ()
    {
      findSolution(player.game.getBall(), player.getStepCount());
      ArrayList<Integer> moves = getSolution();
      for (int i = 0; i < moves.size(); ++i)
        player.game.playerMove(player, moves.get(i));
    }

    protected void findSolution (GameNode node, int steps)
    {
      if (steps > 0)
        for (int i = 0; i < 4; ++i)
        {
          if (player.game.testMove(player.getPlayerID(), i, steps == 1))
          {
            currDirections.set(currIndex++, i);
            findSolution(node.getNeighbor(i), steps - 1);
            --currIndex;
            player.game.testBack();
          }
        }
      else
      {
        int dist = node.getNeigborsDistance();
        int modDist = (dist + player.getStepCount()) % stepCycle;
        int modRating = player.isAttacker() ? 2 : 0;
        if (modDist == player.getStepCount())
          modRating = player.isAttacker() ? 0 : 1;
        else if (modDist == 0)
          modRating = player.isAttacker() ? 1 : 2;
        if (dist == Integer.MAX_VALUE)
          modRating = player.isAttacker() ? 3 : 0;
        if (player.isDefender())
          dist = -dist;
        if (preferDistance)
        {
          int tmp = modRating;
          modRating = dist;
          dist = tmp;
        }
        if (modRating < firstOrder || modRating == firstOrder && dist < secondOrder)
        {
          directions.clear();
          firstOrder = modRating;
          secondOrder = dist;
        }
        if (modRating == firstOrder && dist == secondOrder)
          directions.add(new ArrayList<>(currDirections));
      }
    }

    protected ArrayList<Integer> getSolution ()
    {
      if (BuildConfig.DEBUG && directions.isEmpty())
        throw new AssertionError("Solution not found");

      return directions.get(rnd.nextInt(directions.size()));
    }

    protected BasePlayer player;
    protected int stepCycle;
    protected boolean preferDistance = false;
    Random rnd = new Random();

    protected int firstOrder = Integer.MAX_VALUE;
    protected int secondOrder = Integer.MAX_VALUE;
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
