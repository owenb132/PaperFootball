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
    currAct.closeUI(layerID);
    currAct.getGame().clear();
    return true;
  }

  @Override
  public void load(XMLHelper xml, Renderer.UILayer layer)
  {
    layerID = layer.getID();
  }

  protected int layerID;
}
