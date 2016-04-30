package com.ashsidney.paperfootball;

import java.io.IOException;
import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.os.SystemClock;

import org.xmlpull.v1.XmlPullParserException;


public class Renderer extends Thread implements SurfaceHolder.Callback, XMLHelper.ConfigOwner
{
  public void startRendering ()
  {
    setRunning(true);
    start();
  }

  public void stopRendering ()
  {
    setRunning(false);
    doRedraw();
    try
    {
      join();
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "Thread interupted while waiting to join()");
    }
  }

  public synchronized void doRedraw ()
  {
    refreshRequest = true;
    notify();
  }
  
  protected synchronized void waitRedraw ()
  {
    try
    {
      if (isRefresh())
      {
        refreshRequest = false;
        wait(frameWait);
      }
      else
        wait();
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "Thread was interrupted");
    }
  }

  @Override
  public void run ()
  {
    while (isRunning())
    {
      if (isReady())
        redraw();
      waitRedraw();
    }
  }

  protected synchronized void setRunning (boolean running)
  {
    this.running = running;
  }

  public synchronized boolean isRunning ()
  {
    return running;
  }

  protected synchronized void setReady (boolean ready)
  {
    this.ready = ready;
  }

  public synchronized boolean isReady ()
  {
    return ready;
  }

  protected synchronized boolean isRefresh ()
  {
    return view.isAnimation() || !animations.isEmpty() || refreshRequest;
  }

  protected void redraw ()
  {
    Canvas canvas = holder.lockCanvas();
    if (canvas == null)
      return;

    float currTime = SystemClock.uptimeMillis() * 0.001f;

    RectF world = new RectF(view.draw(canvas, currTime));

    int backgroundColor = 0xffffffff;
    int backLineColor = 0xff8080ff;

    canvas.drawColor(backgroundColor);

    Paint paint = new Paint();
    paint.setColor(backLineColor);
        
    for (float x = (float)Math.ceil(world.left); x <= world.right; x += 1.0f)
      canvas.drawLine(x, world.top, x, world.bottom, paint);
    for (float y = (float)Math.ceil(world.top); y <= world.bottom; y += 1.0f)
      canvas.drawLine(world.left, y, world.right, y, paint);

    goal.draw(canvas, paint, currTime);

    GameNode animNode = null;
    float[] animPosition = null;
    synchronized (this)
    {
      while (!animations.isEmpty())
      {
        BallAnimation currAnim = getAnimation();
        animNode = currAnim.getStartNode();
        if (!currAnim.isStarted())
        {
          ball.startAnim(currAnim.getSequence(), currTime, currAnim.getForward(), true);
          currAnim.setAnimTime(currTime, ball.getAnimTime());
        }
        animPosition = currAnim.getPosition(currTime);
        if (currAnim.isEnded(currTime))
        {
          removeAnimation();
          animNode = null;
        }
        else
          break;
      }

      // vykresli drahu hry
      paint.setStrokeWidth(0.1f);
      paint.setStrokeCap(Paint.Cap.ROUND);
      GameNode node = game.getGoal();
      while (node != animNode && node != null)
      {
        paint.setColor(playerColor[node.getPlayer()]);
        GameNode nNode = node.getNext();
        if (nNode != null)
        {
          canvas.drawLine(node.getPosition()[0], node.getPosition()[1], nNode.getPosition()[0], nNode.getPosition()[1], paint);
          if (nNode == game.getBall())
            nNode = null;
        }
        node = nNode;
      }
    }
    if (animNode != null)
    {
      paint.setColor(playerColor[animNode.getPlayer()]);
      canvas.drawLine(animNode.getPosition()[0], animNode.getPosition()[1], animPosition[0], animPosition[1], paint);
    }

    ball.setPosition(animPosition != null ? animPosition : game.getBall().getPosition());
    ball.draw(canvas, paint, currTime);

    synchronized (this)
    {
      for (UILayer uiLayer : uiLayers)
        uiLayer.draw(canvas, currTime);
    }

    holder.unlockCanvasAndPost(canvas);
  }
  
  public void setGame (Game game)
  {
    if (this.game != game)
    {
      this.game = game;
      game.setRenderer(this);
    }
  }

  public void setView (ViewData data)
  {
    if (view != data)
    {
      view = data;
      view.setRenderer(this);
    }
  }

  public boolean createChild (XMLHelper xml) throws IOException,
      XmlPullParserException
  {
    Sprite sprite = SpriteFactory.create(xml);

    if (sprite != null)
      switch (sprite.getID())
      {
        case R.id.branka:
          goal = sprite;
          return true;
        case R.id.lopta:
          ball = (AnimSprite)sprite;
          ball.setPosition(game.getBall().getPosition());
          return true;
      }
    return false;
  }

  public synchronized BallAnimation getAnimation ()
  {
    return animations.isEmpty() ? null : animations.get(0);
  }
  
  public synchronized void addAnimation (BallAnimation anim)
  {
    animations.add(anim);
    doRedraw();
  }

  public synchronized void removeAnimation ()
  {
    if (!animations.isEmpty())
    {
      animations.remove(0);
      if (animations.isEmpty())
        game.ready();
    }
  }

  public interface UILayer
  {
    int getID ();
    void draw (Canvas canvas, float currTime);
    //void close ();
    void load (XMLHelper xml) throws XmlPullParserException, Resources.NotFoundException, IOException;
  }

  public synchronized void addUI (UILayer layer)
  {
    uiLayers.add(layer);
    doRedraw();
  }

  public synchronized void removeUI (UILayer layer)
  {
    uiLayers.remove(layer);
    doRedraw();
  }

  public ArrayList<Integer> getLayers ()
  {
    ArrayList<Integer> layers = new ArrayList<>();
    for (UILayer layer : uiLayers)
      layers.add(layer.getID());
    return layers;
  }

  @Override
  public void surfaceChanged (SurfaceHolder holder, int format, int width, int height)
  {
    this.holder = holder;
    view.setSizes(width, height);
    setReady(true);
    doRedraw();
  }

  @Override
  public void surfaceCreated (SurfaceHolder holder)
  {
    this.holder = holder;
  }

  @Override
  public void surfaceDestroyed (SurfaceHolder holder)
  {
    setReady(false);
    this.holder = null;
  }
  
  
  protected SurfaceHolder holder = null;
  protected ViewData view = null;
  protected Game game = null;
  protected ArrayList<UILayer> uiLayers = new ArrayList<>();

  protected boolean running = false;
  protected boolean ready = false;
  protected boolean refreshRequest = false;
  protected long frameWait = 10;

  private Sprite goal;
  private AnimSprite ball;
  
  private ArrayList<BallAnimation> animations = new ArrayList<>();
  
  private static int[] playerColor = { 0xff000000, 0xff00ff00, 0xffff0000 };
}
