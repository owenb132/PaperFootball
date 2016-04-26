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
    currAct.getGame().reset(defender, attacker, defenderSteps);
    currAct.getView().reset();
    currAct.openUI(R.id.ovladanie);
    return true;
  }

  @Override
  public void load(XMLHelper xml, Renderer.UILayer layer)
  {
    ownerID = layer.getID();
    String gameType = xml.getAttributeValue("gameType");
    switch (gameType)
    {
      case "defenderComputer":
        defender = new UserPlayer();
        attacker = new SimpleAIPlayer();
      case "playerPlayer":
        defender = new UserPlayer();
        attacker = new UserPlayer();
      default:
        defender = attacker = null;
    }
  }

  protected int ownerID = 0;
  protected BasePlayer defender = null;
  protected BasePlayer attacker = null;
  protected int defenderSteps = 1;
}
