package com.ashsidney.paperfootball;


/**
 * Akcia vykonavajuca spustenie hry.
 */
public class UIActionRunGame implements UIFactory.UIAction
{
  @Override
  public boolean execute()
  {
    PaperFootballActivity currAct = PaperFootballActivity.GetActivity();
    currAct.closeUI(ownerID);
    currAct.getGame().reset(gameType, 2, 0);
    currAct.getView().reset();
    currAct.openUI(R.id.ovladanie);
    return true;
  }

  @Override
  public void load(XMLHelper xml, Renderer.UILayer layer)
  {
    ownerID = layer.getID();
    String gameTypeStr = xml.getAttributeValue("gameType");
    gameType = Game.NoGame;
    for (int i = 0; i < gameTypes.length; ++i)
      if (gameTypeStr.equalsIgnoreCase(gameTypes[i]))
      {
        gameType = i;
        break;
      }
  }

  protected int ownerID;
  protected int gameType;

  protected static final String[] gameTypes = { "noGame", "defenderComputer", "attackerComputer", "playerPlayer", "computerComputer" };
}
