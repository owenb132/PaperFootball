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
    }

    @Override
    public void run ()
    {
      int stepCycle = 2 *  player.getStepCount() + (player.isAttacker() ? -1 : 1);
      for (int step = 1; step <= player.getStepCount(); ++step)
      {
        GameNode node = player.game.getBall();
        ArrayList<Integer> bestDirs = new ArrayList<>();
        int bestDist = Integer.MAX_VALUE;
        int bestMod =  3;
        for (int i = 0; i < 4; ++i)
        {
          int dist = node.getNeighbor(i).getDistance();
          if (dist > 0 || node.getNeighbor(i).isStart())
          {
            int distMod = (dist + step) % stepCycle;
            int currMod;
            if (distMod == player.getStepCount())
              currMod = 0;
            else if (distMod == 0)
              currMod = 1;
            else
              currMod = 2;
            if (player.isDefender())
            {
              currMod = (currMod + 1) % 3;
              dist = -dist;
            }

            if (currMod < bestMod || currMod == bestMod && dist < bestDist)
            {
              bestMod = currMod;
              bestDist = dist;
              bestDirs.clear();
            }
            if (currMod == bestMod && dist == bestDist)
              bestDirs.add(Integer.valueOf(i));
          }
        }
        int rndIdx = bestDirs.size() > 1 ? rndGenerator.nextInt(bestDirs.size()) : 0;
        player.game.playerMove(player, bestDirs.get(rndIdx).intValue());
      }
    }

    protected BasePlayer player;
    protected Random rndGenerator = new Random();
  }

  protected SimpleAIWorker createWorker ()
  {
    return new SimpleAIWorker(this);
  }

  protected SimpleAIWorker worker;
}
