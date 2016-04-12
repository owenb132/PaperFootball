package com.ashsidney.paperfootball;


import android.os.SystemClock;
import android.util.Log;


public class Game implements GestureHandler.Listener
{
  public Game ()
  {
    reset(NoGame, 0, 0);
  }

  public void reset (int gameType, int moveCount, int compLevel)
  {
    // vytvor herne pole
    if (goalNode != null)
      goalNode.clear();

    goalNode = new GameNode();
    ballNode = null;
    currPlayer = 0;
    setPlayerNode(goalNode);
    this.gameType = gameType >= NoGame && gameType <= ComputerVSComputer ? gameType : NoGame;

    // vytvor hracov
    switch(this.gameType)
    {
      case NoGame:
        players[0] = players[1] = null;
        break;
      case PlayerDefenderVSComputer:
        break;
      case PlayerAtackerVSComputer:
        break;
      case PlayerVSPlayer:
        players[0] = new UserPlayer();
        players[0].init(false, moveCount - 1, R.id.tahObranca);
        players[1] = new UserPlayer();
        players[1].init(true, moveCount, R.id.tahUtocnik);
        break;
      case ComputerVSComputer:
        break;
    }

    setCurrentPlayer(false);
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

  public boolean playerMove (BasePlayer player, int direction)
  {
    if (BuildConfig.DEBUG && player != players[currPlayer])
      throw new AssertionError("Invalid player");

    GameNode destNode = ballNode.getNeighbor(direction);
    if (destNode.getPlayer() == 0 || destNode == goalNode && currMoveCount == 1)
    {
      --currMoveCount;
      if (currMoveCount == 0)
        setCurrentPlayer(true);
      setPlayerNode(destNode);
      return true;
    }
    return false;
  }

  @Override
  public synchronized boolean onGesture(GestureEvent event)
  {
    return event.getType() == GestureEvent.EventType.Touch
      && players[currPlayer].onGesture(event);
  }

  public void ready ()
  {
    if (ballNode == goalNode && ballNode.getPrevious() != null
        || !ballNode.isAbleToPlay(currMoveCount == 1))
      InfoHandler.showInfo(R.id.vysledokOznam, ballNode == goalNode ? R.id.vyhraUtocnik : R.id.vyhraObranca, 5.0f);
  }

  protected void setPlayerNode (GameNode node)
  {
    node.setPlayer(currPlayer + 1, ballNode);
    if (ballNode != null)
      renderer.addAnimation(new BallAnimation(ballNode, node));
    ballNode = node;
  }

  protected void setCurrentPlayer (boolean switchPlayers)
  {
    if (switchPlayers)
    {
      if (players[currPlayer] != null)
        players[currPlayer].setCurrent(null);
      currPlayer = 1 - currPlayer;
    }
    if (players[currPlayer] != null)
      currMoveCount = players[currPlayer].setCurrent(this);
  }

  protected Renderer renderer;

  protected int gameType = NoGame;
  protected GameNode goalNode = null;
  protected GameNode ballNode;
  protected BasePlayer[] players = { null, null };
  protected int currPlayer;
  protected int currMoveCount;

  public static final int NoGame = 0;
  public static final int PlayerDefenderVSComputer = 1;
  public static final int PlayerAtackerVSComputer = 2;
  public static final int PlayerVSPlayer = 3;
  public static final int ComputerVSComputer = 4;
}
