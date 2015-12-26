package com.ashsidney.paperfootball;

import java.util.HashSet;
import java.util.Iterator;


public class GameNode
{
  public GameNode ()
  {
    position[0] = 0.0f;
    position[1] = 0.0f;
  }
  
  public GameNode (GameNode node, int dir)
  {
    for (int i = 0; i < 2; ++i)
      position[i] = node.position[i] + Game.directions[dir][i];
    // uloz suseda
    addNeighbor(node, dir);
    // index od suseda naproti
    int oDir = (dir + 2) % 4;
    // najdi ostatnych susedov
    for (int i = 1; i < 4; i += 2)
    {
      GameNode nNode = node.findNeighbor(dir, i);
      if (nNode != null)
      {
        int nDir = (dir + i + 2) % 4;
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
      GameNode[] nbrs = neighbors;
      neighbors = null;
      for (int i = 0; i < 4; ++i)
        if (nbrs[i] != null)
          nbrs[i].clear();
      next = null;
      previous = null;
    }
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
    this.next = next;
  }
  
  public GameNode getPrevious ()
  {
    return previous;
  }
  
  public boolean isStart ()
  {
    return getPlayer() == 1 && getPrevious() == null;
  }

  public int getDistance ()
  {
    return distancePlayer >= 0 ? distancePlayer : 0;
  }

  public int getPlayer ()
  {
    return distancePlayer < 0 ? -distancePlayer : 0;
  }

  public void setPlayer (int player, GameNode prev)
  {
    previous = prev;
    if (prev != null)
      prev.setNext(this);
    if (getPlayer() == 0)
    {
      int lastDistance = distancePlayer;
      distancePlayer = -player;
      completeNeighbors();

      // zozbieraj uzly pre vypocet vzdialenosti
      HashSet<GameNode> nodes = new HashSet<GameNode>();
      for (int i = 0; i < 4; ++i)
        if (neighbors[i] != null)
          neighbors[i].collectForDistance(nodes, lastDistance);
      // vypocitaj vzdialenost uzlov
      boolean recalc = true;
      while (recalc)
      {
        recalc = false;
        Iterator<GameNode> it = nodes.iterator();
        while (it.hasNext())
          if(it.next().calcDistance())
          {
            it.remove();
            recalc = true;
          }
      }
    }
  }

  public boolean isAbleToPlay (boolean goalAllowed)
  {
    if (getPlayer() > 0 && getNext() == null)
      for (int i = 0; i < 4; ++i)
        if (neighbors[i].getDistance() > 0 || goalAllowed && neighbors[i].isStart())
          return true;
    return false;
  }
  
  protected void addNeighbor (GameNode node, int dir)
  {
    dir = (dir + 2) % 4;
    if (neighbors[dir] != node)
    {
      neighbors[dir] = node;
      node.addNeighbor(this, dir);
    }
  }
  
  protected GameNode findNeighbor (int dir, int rot)
  {
    int i = (dir + rot) % 4;
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
      int nDir = (dir + i) % 4;
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
  
  protected void collectForDistance (HashSet<GameNode> nodes, int distance)
  {
    if (distancePlayer > distance)
    {
      distance = distancePlayer;
      distancePlayer = 0;
    }
    if (distancePlayer == 0 && nodes.add(this))
      for (int i = 0; i < 4; ++i)
        if (neighbors[i] != null)
          neighbors[i].collectForDistance(nodes, distance);
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
