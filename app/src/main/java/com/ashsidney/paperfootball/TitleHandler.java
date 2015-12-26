package com.ashsidney.paperfootball;


public class TitleHandler implements Runnable
{
  public static void setActivity (PaperFootballActivity act)
  {
    activity = act;
  }
  
  public static void setTitle (String message)
  {
    activity.runOnUiThread(new TitleHandler(message));
  }
  
  public static void restore ()
  {
    setTitle((String)activity.getResources().getString(R.string.app_name));
  }
  
  public TitleHandler (String msg)
  {
    message = msg;
  }

  @Override
  public void run()
  {
    activity.getSupportActionBar().setTitle(message);
  }

  
  private String message;
  
  private static PaperFootballActivity activity;
}
