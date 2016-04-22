package com.ashsidney.paperfootball;

import android.util.Log;

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
    }

    @Override
    public void run ()
    {
      Solutions sols = new Solutions(player.getStepCount());
      findSolution(player.game.getBall(), sols, player.getStepCount());
      ArrayList<Integer> moves = sols.getSolution();
      for (int i = 0; i < moves.size(); ++i)
        player.game.playerMove(player, moves.get(i));
    }

    protected class Solutions
    {
      public Solutions (int steps)
      {
        for (int i = 0; i < steps; ++i)
          currDirections.add(0);
      }

      public void add (int dir)
      {
        currDirections.set(currIndex++, dir);
      }

      public void remove ()
      {
        --currIndex;
      }

      public void check (int dist, int modRating)
      {
        Log.d("PaperFootball", "check " + Integer.toString(modRating) + " " + Integer.toString(dist)
            + " dirs " + Integer.toString(currDirections.get(0)) + " " + Integer.toString(currDirections.get(1)));
        if (modRating > modDistance || modRating == modDistance && dist > distance)
          return;
        if (modRating < modDistance || dist < distance)
        {
          Log.d("PaperFootball", "best match");
          directions.clear();
          modDistance = modRating;
          distance = dist;
        }
        Log.d("PaperFootball", "same match");
        directions.add(new ArrayList<>(currDirections));
      }

      public ArrayList<Integer> getSolution ()
      {
        if (BuildConfig.DEBUG && directions.isEmpty())
          throw new AssertionError("Solution not found");

        Random rnd = new Random();
        return directions.get(rnd.nextInt(directions.size()));
      }

      protected int modDistance = 4;
      protected int distance = Integer.MAX_VALUE;
      protected ArrayList<ArrayList<Integer>> directions = new ArrayList<>();
      protected ArrayList<Integer> currDirections = new ArrayList<>();
      protected int currIndex = 0;
    }

    protected void findSolution (GameNode node, Solutions sols, int steps)
    {
      if (steps > 0)
        for (int i = 0; i < 4; ++i)
        {
          if (player.game.testMove(player.getPlayerID(), i, steps == 1))
          {
            sols.add(i);
            findSolution(node.getNeighbor(i), sols, steps - 1);
            sols.remove();
            player.game.testBack();
          }
        }
      else
        checkSolution(node, sols);
    }

    protected void checkSolution (GameNode node, Solutions sols)
    {
      int stepCycle = 2 *  player.getStepCount() + (player.isAttacker() ? -1 : 1);
      int dist = node.getNeigborsDistance();
      int modDist = (dist + player.getStepCount()) % stepCycle;
      int modRating = 2;
      if (modDist == player.getStepCount())
        modRating = 0;
      else if (modDist == 0)
        modRating = 1;
      if (player.isDefender())
      {
        modRating = (modRating + 1) % 3;
        dist = -dist;
      }
      sols.check(dist, modRating);
    }

    protected BasePlayer player;
  }

  protected SimpleAIWorker createWorker ()
  {
    return new SimpleAIWorker(this);
  }

  protected SimpleAIWorker worker;
}
