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
    currAct.getGame().reset();
    currAct.getView().reset();
    currAct.openUI(R.id.ovladanie);
    return true;
  }

  @Override
  public void load(XMLHelper xml, Renderer.UILayer layer)
  {
    ownerID = layer.getID();
  }

  protected int ownerID;
}
