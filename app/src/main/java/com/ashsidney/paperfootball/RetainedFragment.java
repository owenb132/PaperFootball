package com.ashsidney.paperfootball;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;


public class RetainedFragment extends Fragment
{

  private Game game = new Game();
  private ViewData viewData = new ViewData();
  private ArrayList<Integer> layerIDs = new ArrayList<>();

  public RetainedFragment ()
  {
    layerIDs.add(R.id.hlavneMenu);
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    // retain this fragment
    setRetainInstance(true);
  }

  public Game getGame()
  {
    return game;
  }

  public void setGame(Game data)
  {
    game = data;
  }

  public ViewData getViewData()
  {
    return viewData;
  }

  public void setViewData(ViewData data)
  {
    viewData = data;
  }

  public ArrayList<Integer> getLayerIDs ()
  {
    return layerIDs;
  }

  public void setLayerID (ArrayList<Integer> layerIDs)
  {
    this.layerIDs = layerIDs;
  }

  public void addLayerID (int layerID)
  {
    layerIDs.add(layerID);
  }

  public void removeLayerID (int layerID)
  {
    int idx = layerIDs.indexOf(layerID);
    if (idx >= 0)
      layerIDs.remove(idx);
  }
}
