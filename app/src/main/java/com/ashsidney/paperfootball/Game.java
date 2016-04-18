package com.ashsidney.paperfootball;


public class Game implements GestureHandler.Listener
{
  public Game ()
  {
    reset(NoGame, 0, 0);
  }

  public void reset (int gameType, int moveCount, int compLevel)
  {
    // vymaz herne pole
    if (goalNode != null)
    {
      for (int i = 0; i < 4; ++i)
      {
        int j = (i + 1) % 4;
        GameNode node = goalNode.getNeighbor(i);
        while (iNode != null)
        {
          GameNode iNode = node.getNeighbor(i);
          while (node != null)
          {
            GameNode jNode = node.getNeighbor(j);
            node.clear();
            node = jNode;
          }
          node = iNode;
        }
      }
      goalNode.clear();
    }

    // vytvor herne pole
    goalNode = new GameNode();
    ballNode = goalNode;

    this.gameType = gameType >= NoGame && gameType <= ComputerVSComputer ? gameType : NoGame;

    // vytvor hracov
    switch(this.gameType)
    {
      case NoGame:
        players[0] = players[1] = null;
        break;
      case PlayerDefenderVSComputer:
        players[0] = new UserPlayer();
        players[0].init(false, moveCount - 1, R.id.tahObranca);
        players[1] = new SimpleAIPlayer();
        players[1].init(true, moveCount, 0);
        break;
      case PlayerAtackerVSComputer:
        players[0] = new SimpleAIPlayer();
        players[0].init(false, moveCount - 1, 0);
        players[1] = new UserPlayer();
        players[1].init(true, moveCount, R.id.tahUtocnik);
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

    currPlayer = 0;
    currMoveCount = 0;
    setPlayerNode();
    setCurrentPlayer();
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

  public synchronized int playerMove (BasePlayer player, int direction)
  {
    if (BuildConfig.DEBUG && player != players[currPlayer])
      throw new AssertionError("Invalid player");

    GameNode destNode = ballNode.getNeighbor(direction);
    if (destNode.getPlayer() == 0 || destNode == goalNode && currMoveCount == 1)
    {
      renderer.addAnimation(new BallAnimation(ballNode, destNode));
      ballNode.setNext(destNode);
      ballNode = destNode;
      --currMoveCount;
      if (currMoveCount > 0)
        setPlayerNode();
    }
    return currMoveCount;
  }

  @Override
  public synchronized boolean onGesture(GestureEvent event)
  {
    return event.getType() == GestureEvent.EventType.Touch
      && currMoveCount > 0 && players[currPlayer].onGesture(event);
  }

  public synchronized void ready ()
  {
    if (currMoveCount == 0)
    {
      if (players[currPlayer] != null)
        players[currPlayer].setCurrent(null);
      currPlayer = 1 - currPlayer;
      setPlayerNode();
    }
    if ((ballNode != goalNode || ballNode.getPrevious() == null)
        && ballNode.isAbleToPlay(currMoveCount == 1))
    {
      if (currMoveCount == 0)
        setCurrentPlayer();
    }
    else if (gameType != NoGame)
      InfoHandler.showInfo(R.id.vysledokOznam, ballNode == goalNode ? R.id.vyhraUtocnik : R.id.vyhraObranca, 5.0f);
  }

  protected void setCurrentPlayer ()
  {
    if (players[currPlayer] != null)
      currMoveCount = players[currPlayer].setCurrent(this);
  }

  protected void setPlayerNode ()
  {
    ballNode.setPlayer(currPlayer + 1);
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
