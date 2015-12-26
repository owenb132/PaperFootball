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
    renderer.setGoal(new Sprite(R.drawable.goal, 2.0f, getResources()));
    renderer.setBall(new BallSprite(1.0f, getResources()));

    SurfaceView view = (SurfaceView)findViewById(R.id.hraciaPlocha);
    view.getHolder().addCallback(renderer);

    gestureHandler = new ZoomRotateGestureHandler(this, view);
    gestureHandler.addTransformer(viewData);
    gestureHandler.addConsumer(game);

    renderer.startRendering();

    TitleHandler.setActivity(this);

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

  /// nastav menu na zobrazenie alebo ho vypni (0)
  public void setMenu (int menuID)
  {
    // ak je 0, vypni menu
    if (menuID <= 0 && renderer.getMenu() != null)
    {
      // zrus menu v rendereri
      renderer.setMenu(null);
      // odstran aktualne menu zo zoznamu na spracovanie gest
      gestureHandler.removeTransformer();
      return;
    }
    // ak nie su nacitane menu, skonci
    if (menus == null)
      return;

    // najdi menu s pozadovanym id a zobraz ho
    for (int i = 0; i < menus.size(); ++i)
    {
      Menu menu = menus.get(i);
      if (menu.getMenuID() == menuID)
      {
        // nastav menu na zobrazenie
        renderer.setMenu(menu);
        // nastav menu na gesta
        gestureHandler.addTransformer(menu);
        return;
      }
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
      menus = Menu.loadMenus(R.xml.menus, getResources());
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "Menu load failed:" + e.getMessage());
    }
    // nastav menu na zobrazenie
    setMenu(openMenu);
  }

  protected Renderer renderer = new Renderer();
  protected RetainedFragment retainData;
  protected Game game;
  protected ViewData viewData;
  protected GestureHandler gestureHandler;
  protected ArrayList<Menu> menus = null;

  /*@Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_paper_football, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
        return true;
    }

    return super.onOptionsItemSelected(item);
  }*/
}
