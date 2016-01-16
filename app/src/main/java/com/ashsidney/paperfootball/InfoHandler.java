package com.ashsidney.paperfootball;


import android.graphics.Canvas;

public class InfoHandler //implements Renderer.UILayer
{
  public static void setText (String message)
  {
    //PaperFootballActivity.GetActivity().runOnUiThread(new InfoHandler(message));
  }
  
  public static void restore ()
  {
    //setTitle((String)activity.getResources().getString(R.string.app_name));
  }
  
  public InfoHandler (String msg)
  {
    message = msg;
  }

  /*@Override
  public void run()
  {
    activity.getSupportActionBar().setTitle(message);
  }
  */
  
  private String message;
  
  private static PaperFootballActivity activity;

  //@Override
  public void draw (Canvas canvas, float currTime)
  {

  }
}
