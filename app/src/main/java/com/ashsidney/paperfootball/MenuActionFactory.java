package com.ashsidney.paperfootball;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

/**
 * Factory pre vytvaranie objektov s interfejsom IMenuAction
 */
public class MenuActionFactory
{
    /**
     * Funkcia pre vytvorenie objektu menu akcie
     * @param parser XML parser pre konfiguracne data
     * @return vytvoreny objekt
     */
  public static MenuAction generate (Menu menu, XmlPullParser parser)
  {
    MenuAction action = null;

    try
    {
      /*switch(XMLHelper.getAttributeValue(parser, "class"))
      {
        case "runGame":
          action = new MenuActionRunGame(menu, currentGame);
          break;
      }*/

      if (action != null)
        action.setup(parser);
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "Menu action load failed:" + e.getMessage());

    }

    return action;
  }

  /**
   * Funkcia pre nastavenie tovarne
   * @param game objekt aktualnej hry
   */
  public static void setup (Game game)
  {
    currentGame = game;
  }

  /// aktualna hra
  static Game currentGame = null;
}
