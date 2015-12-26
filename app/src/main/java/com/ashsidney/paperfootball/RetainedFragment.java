package com.ashsidney.paperfootball;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public class RetainedFragment extends Fragment
{

  private Game game = new Game();
  private ViewData viewData = new ViewData();

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
}
