package com.ashsidney.paperfootball;


public class Game implements GestureHandler.Listener
{
  public Game ()
  {
    reset(NoGame);
  }

  public void setRenderer (Renderer rend)
  {
    if (renderer != rend)
    {
      renderer = rend;
      renderer.setGame(this);
    }
  }

  public GameNode getGoal ()
  {
    return goalNode;
  }

  public GameNode getBall ()
  {
    return ballNode;
  }
    
  @Override
  public synchronized boolean onGesture(GestureEvent event)
  {
    GestureEvent.EventType evType = event.getType();    
    if (evType == GestureEvent.EventType.Touch)
    {
      float[] touch = event.getTransformation().getTranslation();
      for (int i = 0; i < 2; ++i)
        touch[i] -= ballNode.getPosition()[i];
      
      int bestIdx = -1;
      float bestVal = 0.0f;
      for (int i = 0; i < 4; ++i)
      {
        float dotVal = touch[0] * directions[i][0] + touch[1] * directions[i][1];
        if (dotVal >= bestVal )
        {
          bestIdx = i;
          bestVal = dotVal;
        }
      }
      
      if (bestIdx >= 0)
      {
        GameNode newNode = ballNode.getNeighbor(bestIdx);
        if (newNode.getPlayer() == 0 || newNode == goalNode && playerMoves == 1)
        {
          --playerMoves;
          if (playerMoves == 0)
          {
            currPlayer = 3 - currPlayer;
            playerMoves = currPlayer;
          }
          newNode.setPlayer(currPlayer, ballNode);
          renderer.addAnimation(new BallAnimation(ballNode, newNode));
          ballNode = newNode;
        }
      }
      return true;
    }
    return false;
  }

  public void reset (int gmType)
  {
    if (goalNode != null)
      goalNode.clear();
    
    goalNode = new GameNode();
    gameType = gmType >= NoGame && gmType <= ComputerVSComputer ? gmType : NoGame;
    currPlayer = 1;
    playerMoves = currPlayer;
    ballNode = goalNode;
    ballNode.setPlayer(currPlayer, null);

    ready();
  }
  
  public void ready ()
  {
    if (ballNode == goalNode && ballNode.getPrevious() != null
        || !ballNode.isAbleToPlay(playerMoves == 1))
      InfoHandler.showInfo(R.id.vysledokOznam, ballNode == goalNode ? R.id.vyhraUtocnik : R.id.vyhraObranca, 5.0f);
    else
      if ((gameType == PlayerVSComputer || gameType == PlayerVSPlayer)
          && currPlayer == playerMoves)
        InfoHandler.showInfo(R.id.stavOznam, currPlayer == 1 ? R.id.tahObranca : R.id.tahUtocnik, 1.0f);
  }
  
  protected Renderer renderer;
  
  protected GameNode goalNode = null;
  protected GameNode ballNode;
  protected int currPlayer;
  protected int playerMoves;
  protected int gameType = NoGame;

  public static int NoGame = 0;
  public static int PlayerVSComputer = 1;
  public static int PlayerVSPlayer = 2;
  public static int ComputerVSComputer = 3;

  public static final float[][] directions = { {1.0f, 0.0f},  {0.0f, -1.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f} };
}
