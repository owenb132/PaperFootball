package com.ashsidney.paperfootball;

import java.util.HashSet;
import java.util.Iterator;


public class GameNode
{
  public GameNode ()
  {
    position[0] = 0.0f;
    position[1] = 0.0f;
    completeNeighbors();
  }

  public GameNode (GameNode node, int dir)
  {
    for (int i = 0; i < 2; ++i)
      position[i] = node.position[i] + BasePlayer.directions[dir][i];
    // uloz suseda
    addNeighbor(node, dir);
    // index od suseda naproti
    int oDir = invertDir(dir);
    // najdi ostatnych susedov
    for (int i = 1; i < 4; i += 2)
    {
      GameNode nNode = node.findNeighbor(dir, i);
      if (nNode != null)
      {
        int nDir = invertDir(dir + i);
        addNeighbor(nNode, nDir);
        if (neighbors[oDir] == null)
        {
          nNode = nNode.findNeighbor(nDir, i);
          if (nNode != null)
            addNeighbor(nNode, oDir);
        }
      }
    }
  }

  public void clear ()
  {
    if (neighbors != null)
    {
      for (int i = 0; i < 4; ++i)
        neighbors[i] = null;
      next = null;
      previous = null;
    }
  }

  public void clearAll ()
  {
    for (int i = 0; i < 4; ++i)
    {
      int j = (i + 1) % 4;
      GameNode node = getNeighbor(i);
      while (node != null)
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
    clear();
  }

  public float[] getPosition ()
  {
    return position;
  }

  public GameNode getNeighbor (int dir)
  {
    return neighbors[dir];
  }

  public GameNode getNext ()
  {
    return next;
  }

  public void setNext (GameNode next)
  {
    if (this.next != null)
    {
      this.next.previous = null;
      this.next.setPlayer(0);
    }
    this.next = next;
    if (next != null)
    {
      next.previous = this;
      next.completeNeighbors();
    }
  }

  public GameNode getPrevious ()
  {
    return previous;
  }

  public boolean isStart ()
  {
    return position[0] == 0 && position[1] == 0;
  }

  public int getDistance ()
  {
    return distancePlayer >= 0 ? distancePlayer : 0;
  }

  public int getPlayer ()
  {
    return distancePlayer < 0 ? -distancePlayer : 0;
  }

  public void setPlayer (int player)
  {
    if (isStart() && getPlayer() != 0)
      return;

    if (BuildConfig.DEBUG && (player != 0 && getPlayer() != 0 || player == 0 && getPlayer() == 0))
      throw new AssertionError("Invalid player");

    int lastDistance = getNeigborsDistance();
    distancePlayer = -player;

    // zozbieraj uzly pre vypocet vzdialenosti
    HashSet<GameNode> nodes = new HashSet<>();
    if (player == 0)
      nodes.add(this);
    GameNode node = this;
    HashSet<GameNode> chckNodes = new HashSet<>();
    do
    {
      for (int i = 0; i < 4; ++i)
      {
        GameNode nbr = node.getNeighbor(i);
        if (nbr != null && (nbr.distancePlayer > lastDistance
            || nbr.distancePlayer == 0 && !nodes.contains(nbr)))
          chckNodes.add(nbr);
      }
      node = chckNodes.isEmpty() ? null : chckNodes.iterator().next();
      if (node != null)
      {
        lastDistance = node.getDistance();
        node.distancePlayer = 0;
        nodes.add(node);
        chckNodes.remove(node);
      }
    }
    while (node != null);

    // vypocitaj vzdialenost uzlov
    boolean recalc = true;
    while (recalc)
    {
      recalc = false;
      Iterator<GameNode> it = nodes.iterator();
      while (it.hasNext())
        if (it.next().calcDistance())
        {
          it.remove();
          recalc = true;
        }
    }
  }

  public boolean isAbleToPlay (boolean allowStart)
  {
    if (getNext() == null)
      for (int i = 0; i < 4; ++i)
        if (neighbors[i].getDistance() > 0 || allowStart && neighbors[i].isStart())
          return true;
    return false;
  }

  public GameNode canGo (int direction, boolean allowStart)
  {
    GameNode nbr = getNeighbor(direction);
    if (nbr != null && (nbr.getPlayer() == 0 || allowStart && nbr.isStart()))
      return nbr;
    return null;
  }

  public int getNeigborsDistance ()
  {
    int minDistance = getDistance();
    if (minDistance > 0 || isStart())
      return minDistance;

    minDistance = Integer.MAX_VALUE;
    for (int i = 0; i < 4; ++i)
    {
      GameNode nbr = getNeighbor(i);
      if (nbr != null)
      {
        int nbrDist = nbr.getDistance();
        if ((nbrDist > 0 || nbr.isStart()) && nbrDist < minDistance)
          minDistance = nbrDist;
      }
    }
    return minDistance + (minDistance == Integer.MAX_VALUE ? 0 : 1);
  }

  protected int rotateDir (int dir, int rot)
  {
    return (dir + rot) % 4;
  }

  protected int invertDir (int dir)
  {
    return rotateDir(dir, 2);
  }

  protected void addNeighbor (GameNode node, int dir)
  {
    dir = invertDir(dir);
    if (neighbors[dir] != node)
    {
      neighbors[dir] = node;
      node.addNeighbor(this, dir);
    }
  }
  
  protected GameNode findNeighbor (int dir, int rot)
  {
    int i = rotateDir(dir, rot);
    if (neighbors[i] != null)
      return neighbors[i].getNeighbor(dir);
    return null;
  }
    
  protected void completeNeighbors ()
  {
    for (int i = 0; i < 4; ++i)
      if (neighbors[i] == null)
        completeNeighbors(i);
  }

  protected void completeNeighbors (int dir)
  {
    new GameNode(this, dir);
    for (int i = 1; i < 4; i += 2)
    {
      int nDir = rotateDir(dir, i);
      if (neighbors[nDir] != null)
        neighbors[nDir].completeNeighbors(dir, nDir);
    }
  }

  protected void completeNeighbors (int dir, int shift)
  {
    new GameNode(this, dir);
    if (neighbors[shift] != null)
      neighbors[shift].completeNeighbors(dir, shift);
  }

  protected boolean calcDistance ()
  {
    if (distancePlayer == 0)
    {
      int minDist = Integer.MAX_VALUE;
      for (int i = 0; i < 4; ++i)
        if (neighbors[i] != null)
        {
          int dist = neighbors[i].getDistance();
          if (dist < minDist && (dist > 0 || neighbors[i].isStart()))
            minDist = dist;
        }
      if (minDist < Integer.MAX_VALUE)
      {
        distancePlayer = minDist + 1;
        return true;
      }
    }
    return false;
  }


  protected float[] position = new float[2];

  protected GameNode[] neighbors = { null, null, null, null };
  protected GameNode next = null;
  protected GameNode previous = null;

  protected int distancePlayer = 0;
}
