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
    int level = xml.getAttributeInt("level");
    switch (gameType)
    {
      case "attackerComputer":
        defender = new BasicAIPlayer();
        attacker = new UserPlayer();
        break;
      case "defenderComputer":
        defender = new UserPlayer();
        switch (level)
        {
          case 1:
            attacker = new AttackAIPlayer();
            break;
          default:
            attacker = new BasicAIPlayer();
            break;
        }
        break;
      case "playerPlayer":
        defender = new UserPlayer();
        attacker = new UserPlayer();
        break;
      default:
        defender = attacker = null;
        break;
    }
  }

  protected int ownerID = 0;

  protected BasePlayer defender = null;
  protected BasePlayer attacker = null;
  protected int defenderSteps = 1;
}
