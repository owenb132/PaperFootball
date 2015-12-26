package com.ashsidney.paperfootball;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by Ash on 28. 11. 2015.
 */
public class MenuActionRunGame implements MenuAction
{
  public MenuActionRunGame (Menu menu, Game game)
  {
    this.menu = menu;
    this.game = game;
  }

  public boolean execute()
  {
    menu.close();
    game.clear();
    return true;
  }

  public void setup(XmlPullParser parser)
  {}

  protected Menu menu;
  protected Game game;
}
