package com.ashsidney.paperfootball;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

import android.util.Log;


public class PaperFootballActivity extends AppCompatActivity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    myActivity = this;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_paper_football);
    // fragment so zachovanymi datami
    FragmentManager fm = getSupportFragmentManager();
    String fragmentName = "data";
    retainData = (RetainedFragment) fm.findFragmentByTag(fragmentName);
    if (retainData == null)
    {
      // ak fragment nenasiel, pridaj novy
      retainData = new RetainedFragment();
      fm.beginTransaction().add(retainData, fragmentName).commit();
    }
    // ziskaj udaje z fragmentu
    game = retainData.getGame();
    viewData = retainData.getViewData();

    game.setContext(this);
    game.setRenderer(renderer);

    renderer.setView(viewData);

    XMLHelper xml = new XMLHelper();
    xml.load(renderer, R.xml.sprites, getResources());

    SurfaceView view = (SurfaceView)findViewById(R.id.hraciaPlocha);
    view.getHolder().addCallback(renderer);

    gestureHandler = new ZoomRotateGestureHandler(this, view);
    gestureHandler.add(game);
    gestureHandler.add(viewData);

    renderer.startRendering();

    loadMenus(R.id.hlavneMenu);
  }

  @Override
  public void onPause ()
  {
    super.onPause();
    if (isFinishing())
        game.clear();
  }

  @Override
  public void onDestroy ()
  {
    super.onDestroy();
    // zastav kreslenie
    renderer.stopRendering();
    // zapis data do fragmentu
    retainData.setViewData(viewData);
    retainData.setGame(game);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    gestureHandler.onTouchEvent(event);
    return super.onTouchEvent(event);
  }

  /// nastav menu na zobrazenie
  public void openMenu (int menuID)
  {
    // najdi menu s pozadovanym id a zobraz ho
    for (Menu menu : menus)
      if (menu.getMenuID() == menuID)
      {
        // nastav menu na zobrazenie
        renderer.addUI(menu);
        // nastav menu na gesta
        gestureHandler.add(menu);
        break;
      }
  }

  public void closeMenu (int menuID)
  {
    // najdi menu s pozadovanym id a odstran ho
    for (Menu menu : menus)
      if (menu.getMenuID() == menuID)
      {
        // nastav menu na zobrazenie
        renderer.removeUI(menu);
        // nastav menu na gesta
        gestureHandler.remove(menu);
        break;
      }
  }

  /**
   * Nacitaj vsetky menu a zobraz menu podla jeho id
   */
  protected void loadMenus (int openMenu)
  {
    MenuActionFactory.setup(game);

    try
    {
      Menu.loadMenus(menus, R.xml.menus, getResources());
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "Menu load failed:" + e.getMessage());
    }
    // nastav menu na zobrazenie
    openMenu(openMenu);
  }

  protected Renderer renderer = new Renderer();
  protected RetainedFragment retainData;
  protected Game game;
  protected ViewData viewData;
  protected GestureHandler gestureHandler;
  protected ArrayList<Menu> menus = new ArrayList<>();

  /**
   * Get current activity.
   */
  public static PaperFootballActivity GetActivity ()
  {
    return myActivity;
  }

  private static PaperFootballActivity myActivity = null;
}
