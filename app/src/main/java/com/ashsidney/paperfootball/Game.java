package com.ashsidney.paperfootball;

import android.content.Context;
import android.widget.Toast;

public class Game implements GestureHandler.Listener
{
  public Game ()
  {
    clear();
  }
  
  public void setContext (Context context)
  {
    this.context = context;
    
    // napln mena hracov
    if (playerNames == null)
    {
      playerNames = new String[2];
      playerNames[0] = (String)context.getResources().getText(R.string.defender);
      playerNames[1] = (String)context.getResources().getText(R.string.striker);
    }
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
      if (ballNode == goalNode || !ballNode.isAbleToPlay(playerMoves == 1))
        InfoHandler.showInfo(ballNode == goalNode ? R.id.vyhraUtocnik : R.id.vyhraObranca, 10.0f);
      else
        moveReady();
        
      return true;
    }
    return false;
  }

  protected void clear ()
  {
    if (goalNode != null)
      goalNode.clear();
    
    goalNode = new GameNode();
    currPlayer = 1;
    playerMoves = currPlayer;
    ballNode = goalNode;
    ballNode.setPlayer(currPlayer, null);
  }
  
  protected void moveReady ()
  {
    //InfoHandler.setTitle((String) context.getResources().getText(R.string.move) + playerNames[currPlayer - 1]);
  }
  
  protected Context context;
  protected Renderer renderer;
  
  protected GameNode goalNode = null;
  protected GameNode ballNode;
  protected int currPlayer;
  protected int playerMoves;
  
  public static final float[][] directions = { {1.0f, 0.0f},  {0.0f, -1.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f} };
  protected static String[] playerNames = null;
}
