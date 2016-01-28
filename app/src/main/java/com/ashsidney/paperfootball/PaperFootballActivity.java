package com.ashsidney.paperfootball;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;


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

    uiLayers = UIFactory.loadUILayers(R.xml.uilayers, getResources());
    openUI(R.id.hlavneMenu);

    game.ready();
  }

  @Override
  public void onPause ()
  {
    super.onPause();
    if (isFinishing())
      game.reset(Game.NoGame);
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

  /**
   * Spristupni objekt renderera
   *
   * @return renderer
   */
  public Renderer getRenderer ()
  {
    return renderer;
  }

  /**
   * Spristupni objekt hry
   *
   * @return objekt hry
   */
  public Game getGame ()
  {
    return game;
  }

  /**
   * Spristupni pohlad
   * @return objekt pohladu
   */
  public ViewData getView ()
  {
    return viewData;
  }

  /// nastav menu na zobrazenie
  public boolean openUI (int layerID)
  {
    // najdi vrstvu s pozadovanym id a zobraz ju
    for (Renderer.UILayer layer : uiLayers)
      if (layer.getID() == layerID)
      {
        // nastav vrstvu na zobrazenie
        renderer.addUI(layer);
        // nastav vrstvu na vstup od pouzivatela
        gestureHandler.add((GestureHandler.Listener)layer);
        return true;
      }
    return false;
  }

  public void closeUI (int layerID)
  {
    // najdi vrstvu s pozadovanym id a zobraz ju
    for (Renderer.UILayer layer : uiLayers)
      if (layer.getID() == layerID)
      {
        // nastav vrstvu na zobrazenie
        renderer.removeUI(layer);
        // nastav vrstvu na vstup od pouzivatela
        gestureHandler.remove((GestureHandler.Listener) layer);
        break;
      }
  }

  /**
   * Pristup k UI vrstve.
   *
   * @param layerID identifikacia vrstvy
   * @return pozadovana vrstva
   */
  public Renderer.UILayer getUILayer (int layerID)
  {
    for (Renderer.UILayer layer : uiLayers)
      if (layer.getID() == layerID)
        return layer;
    return null;
  }


  protected Renderer renderer = new Renderer();
  protected RetainedFragment retainData;
  protected Game game;
  protected ViewData viewData;
  protected GestureHandler gestureHandler;
  protected ArrayList<Renderer.UILayer> uiLayers = new ArrayList<>();


  /**
   * Poskytni sucasnu aktivitu.
   */
  public static PaperFootballActivity GetActivity ()
  {
    return myActivity;
  }

  private static PaperFootballActivity myActivity = null;
}
