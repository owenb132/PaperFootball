package com.ashsidney.paperfootball;


public class Game implements GestureHandler.Listener
{
  public Game ()
  {
    reset(null, null, 0);
  }

  public void reset (BasePlayer defender, BasePlayer attacker, int defSteps)
  {
    // vymaz herne pole
    if (goalNode != null)
      goalNode.clearAll();

    // vytvor herne pole
    goalNode = new GameNode();
    ballNode = goalNode;
    testNode = ballNode;

    players[0] = defender;
    if (players[0] != null)
      players[0].init(false, defSteps);
    players[1] = attacker;
    if (players[1] != null)
      players[1].init(true, defSteps + 1);

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

  public GameNode getTest ()
  {
    return testNode;
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
    else if (players[0] != null && players[1] != null)
      InfoHandler.showInfo(R.id.vysledokOznam, ballNode == goalNode ? R.id.vyhraUtocnik : R.id.vyhraObranca, 5.0f);
  }

  public synchronized int playerMove (BasePlayer player, int direction)
  {
    if (BuildConfig.DEBUG && player != players[currPlayer])
      throw new AssertionError("Invalid player");

    GameNode destNode = ballNode.canGo(direction, currMoveCount == 1);
    if (destNode != null)
    {
      testRollBack();
      renderer.addAnimation(new BallAnimation(ballNode, destNode));
      ballNode.setNext(destNode);
      ballNode = destNode;
      testNode = ballNode;
      --currMoveCount;
      if (currMoveCount > 0)
        setPlayerNode();
    }
    return currMoveCount;
  }

  public synchronized boolean testMove (int playerID, int direction, boolean allowStart)
  {
    GameNode destNode = testNode.canGo(direction, allowStart);
    if (destNode != null)
    {
      testNode.setNext(destNode);
      testNode = destNode;
      testNode.setPlayer(playerID);
      return true;
    }
    return false;
  }

  public synchronized void testBack ()
  {
    if (testNode != ballNode)
    {
      GameNode prevNode = testNode.getPrevious();
      prevNode.setNext(null);
      testNode = prevNode;
    }
  }

  protected void testRollBack ()
  {
    while (testNode != ballNode)
      testBack();
  }

  @Override
  public synchronized boolean onGesture(GestureEvent event)
  {
    return event.getType() == GestureEvent.EventType.Touch
      && currMoveCount > 0 && players[currPlayer].onGesture(event);
  }

  protected void setCurrentPlayer ()
  {
    if (players[currPlayer] != null)
      currMoveCount = players[currPlayer].setCurrent(this);
  }

  protected void setPlayerNode ()
  {
    if (players[currPlayer] != null)
      ballNode.setPlayer(players[currPlayer].getPlayerID());
  }

  protected Renderer renderer;

  protected GameNode goalNode = null;
  protected GameNode ballNode;
  protected BasePlayer[] players = { null, null };
  protected int currPlayer;
  protected int currMoveCount;

  protected GameNode testNode = null;
}
