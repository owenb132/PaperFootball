package com.ashsidney.paperfootball;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.text.TextPaint;

public class Menu //implements Renderer.UILayer, GestureHandler.Listener
{
  public Menu (XMLHelper xml) throws XmlPullParserException, IOException
  {
    menuID = xml.getAttributeID("id");
    /*for (int eventType = parser.next(); eventType != XmlPullParser.END_TAG; eventType = parser.next())
      if (eventType == XmlPullParser.START_TAG && parser.getName().equals(MenuItem.Tag))
      {
        MenuItem item = new MenuItem(this);
        item.loadConfiguration(parser, res);
        items.add(item);
      }*/
  }
  
  public void draw (Canvas canvas, float currTime)
  {
    TextPaint paint = new TextPaint();
    if (positionReset)
    {
      calcMenuPosition(canvas, paint);
      positionReset = false;
    }
    paint.setTextSize(fontSize);
    paint.setTextAlign(TextPaint.Align.CENTER);

    canvas.save();
    canvas.setMatrix(matrix);
    /*for (int i = 0; i < items.size(); ++i)
      items.get(i).draw(canvas, paint, currTime);*/
    canvas.restore();
  }

  //@Override
  public boolean onGesture (GestureEvent event)
  {
    // ak doslo ku stlaceniu
    if (event.eventType == GestureEvent.EventType.Touch)
    {
      // transformuj poziciu stlacenia
      float[] point = event.getTransformation().getTranslation();
      matrix.mapPoints(point);
      // zisti, ktora polozka menu bola stlacena
      /*for (int i = 0; i < items.size(); ++i)
        if (items.get(i).press(point))
          return true;*/
    }
    return false;
  }

  /// vrat menu ID
  public int getMenuID ()
  {
    return menuID;
  }

  public void setRenderer (Renderer rend)
  {
    renderer = rend;
  }

  public void close ()
  {
    //renderer.setMenu(null);
  }


  protected void calcMenuPosition (Canvas canvas, TextPaint paint)
  {
    /*int orientation = canvas.getWidth() > canvas.getHeight() ? Menu.LandscapeOrientation : Menu.PortraitOrientation;

    // urci rozsahy menu
    int menuOffset[] = { Integer.MAX_VALUE, Integer.MAX_VALUE };
    int menuSize[] = { Integer.MIN_VALUE, Integer.MIN_VALUE };
    for (int i = 0; i < items.size(); ++i)
    {
      MenuItem item = items.get(i);
      int pos[] = { item.getColumn(orientation), item.getRow(orientation) };
      for (int mi = 0; mi < 2; ++mi)
      {
        if (menuOffset[mi] > pos[mi])
          menuOffset[mi] = pos[mi];
        else if (menuSize[mi] < pos[mi])
          menuSize[mi] = pos[mi];
      }
    }

    // vypocitaj celkove rozmery menu
    for (int i = 0; i < 2; ++i)
      menuSize[i] -= menuOffset[i] - 1;
    int columnMax[] = new int[menuSize[0]];
    int rowMax[] = new int[menuSize[1]];
    for (int i = 0; i < items.size(); ++i)
    {
      MenuItem item = items.get(i);
      int colIdx = item.getColumn(orientation) - menuOffset[0];
      int width = item.getWidth();
      if (columnMax[colIdx] < width)
        columnMax[colIdx] = width;
      int rowIdx = item.getRow(orientation) - menuOffset[1];
      int height = item.getHeight();
      if (rowMax[rowIdx] < height)
        rowMax[rowIdx] = height;
    }    
    float colSum = 0.0f;
    for (int i = 0; i < menuSize[0]; ++i)
      colSum += columnMax[i];
    float rowSum = 0.0f;
    for (int i = 0; i < menuSize[1]; ++i)
      rowSum += rowMax[i];

    // urci zvacsenie menu
    float scales[] = { canvas.getWidth() / colSum, canvas.getHeight() / rowSum };
    float scale = scales[0] < scales[1] ? scales[0] : scales[1];
    matrix.preScale(scale, scale);

    // urci pozicie stlpcov a riadkov menu
    float prevPos = 0.0f;
    float stepCoef = scales[0] / scale;
    float offCoef = (stepCoef - 1.0f) * 0.5f;
    float columnPos[] = new float[menuSize[0]];
    for (int i = 0; i < menuSize[0]; ++i)
    {
      columnPos[i] = columnMax[i] * offCoef + prevPos;
      prevPos += columnMax[i] * stepCoef;
    }
    prevPos = 0.0f;
    stepCoef = scales[1] / scale;
    offCoef = (stepCoef - 1.0f) * 0.5f;
    float rowPos[] = new float[menuSize[1]];
    for (int i = 0; i < menuSize[1]; ++i)
    {
      rowPos[i] = rowMax[i] * offCoef + prevPos;
      prevPos += rowMax[i] * stepCoef;
    }

    // nastav poziciu pre kazdy prvok menu
    for (int i = 0; i < items.size(); ++i)
    {
      MenuItem item = items.get(i);
      int colIdx = item.getColumn(orientation) - menuOffset[0];
      int rowIdx = item.getRow(orientation) - menuOffset[1];
      float itemPos[] = { columnPos[colIdx], rowPos[rowIdx] };
      item.setPosition(itemPos);
    }

    // zmeraj pomery sirok textov a pozadi v prvkoch menu
    paint.setTextSize(fontSize);
    float minRatio = 1000.0f;
    for (int i = 0; i < items.size(); ++i)
    {
      float ratio = items.get(i).measureText(paint);
      if (ratio < minRatio)
        minRatio = ratio;
    }
    fontSize *= minRatio;*/
  }

  
  static public void loadMenus (ArrayList<Menu> menus, int menusID, Resources res)
    throws XmlPullParserException, IOException
  {
   /* XmlPullParser parser = XMLHelper.openXML(menusID, res);

    for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next())
      if (eventType == XmlPullParser.START_TAG && parser.getName().equals(Tag))
        menus.add(new Menu(parser, res));*/
  }
  
  
  
  public static final String Tag = "menu";

  
  protected int menuID;
  //protected ArrayList<MenuItem> items = new ArrayList<>();

  protected Renderer renderer;
  
  protected Matrix matrix = new Matrix();
  protected float fontSize = 32.0f;

  protected boolean positionReset = true;
}
