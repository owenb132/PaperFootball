package com.ashsidney.paperfootballtest;

import com.ashsidney.paperfootball.BasePlayer;
import com.ashsidney.paperfootball.Game;
import com.ashsidney.paperfootball.GameNode;
import com.ashsidney.paperfootball.Renderer;

import junit.framework.TestCase;

/**
 * Test class for Game class
 */
public class GameTest extends TestCase
{
  protected boolean checkPath (Game game)
  {
    GameNode node = game.getGoal();
    while (node != game.getTest())
    {
      if (node.getPlayer() == 0)
        return false;
      node = node.getNext();
    }
    return node.getPlayer() > 0;
  }

  public void testGamePathDeadEnd() throws Exception
  {
    Renderer rend = new Renderer();
    Game testGame = new Game();
    testGame.setRenderer(rend);

    BasePlayer def = new BasePlayer();
    BasePlayer att = new BasePlayer();
    testGame.reset(def, att, 1);
    float eps = 0.001f;
    assertEquals(0.0, testGame.getBall().getPosition()[0], eps);
    assertEquals(0.0, testGame.getBall().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.playerMove(def, 3);
    testGame.ready();
    assertEquals(0.0, testGame.getBall().getPosition()[0], eps);
    assertEquals(1.0, testGame.getBall().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.playerMove(att, 3);
    testGame.playerMove(att, 3);
    testGame.ready();
    assertEquals(0.0, testGame.getBall().getPosition()[0], eps);
    assertEquals(3.0, testGame.getBall().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.playerMove(def, 0);
    testGame.ready();
    assertEquals(1.0, testGame.getBall().getPosition()[0], eps);
    assertEquals(3.0, testGame.getBall().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.playerMove(att, 0);
    testGame.playerMove(att, 1);
    testGame.ready();
    assertEquals(2.0, testGame.getBall().getPosition()[0], eps);
    assertEquals(2.0, testGame.getBall().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.playerMove(def, 1);
    testGame.ready();
    assertEquals(2.0, testGame.getBall().getPosition()[0], eps);
    assertEquals(1.0, testGame.getBall().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.testMove(2, 2, false);
    assertEquals(1.0, testGame.getTest().getPosition()[0], eps);
    assertEquals(1.0, testGame.getTest().getPosition()[1], eps);
    assertTrue(checkPath(testGame));

    testGame.testMove(2, 3, false);
    assertEquals(1.0, testGame.getTest().getPosition()[0], eps);
    assertEquals(2.0, testGame.getTest().getPosition()[1], eps);
    assertTrue(checkPath(testGame));
  }
}
