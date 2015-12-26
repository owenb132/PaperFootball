package com.ashsidney.paperfootball;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Canvas;
import android.text.TextPaint;


public class MenuItem extends Sprite
{
  public MenuItem (Menu owner, XmlPullParser parser, Resources res) throws XmlPullParserException, NotFoundException, IOException
  {
    itemID = XMLHelper.getAttributeID(parser, "id");
    int initDepth = parser.getDepth();
    for (int eventType = parser.next(); eventType != XmlPullParser.END_TAG || parser.getDepth() > initDepth;
        eventType = parser.next())
    {
      if (eventType == XmlPullParser.START_TAG)
      {
        switch (parser.getName())
        {
          case LabelTag:
            label = res.getString(XMLHelper.getAttributeID(parser, "id"));
            break;
          case BackgroundTag:
            bitmap = loadBitmap(XMLHelper.getAttributeID(parser, "id"), res);
            break;
          case PositionTag:
            OrientedPosition pos = new OrientedPosition(parser);
            if (pos.orientation > 0)
              positions.add(pos);
            break;
          case ActionTag:
            action = MenuActionFactory.generate(owner, parser);
            break;
        }
      }
    }
  }
  
  public void draw (Canvas canvas, TextPaint paint, float currTime)
  {
    canvas.save();
    super.draw(canvas, currTime);
    float pos[] = { 0.0f, 0.0f };
    fullTransform.mapPoints(pos);
    pos[0] += getWidth() * 0.5f;
    pos[1] += (getHeight() + paint.getTextSize()) * 0.5f;
    canvas.drawText(label, pos[0], pos[1], paint);
    canvas.restore();
  }

  public boolean press (float[] point)
  {
    if (point[0] >= 0 && point[0] < getWidth() && point[1] >= 0 && point[1] < getHeight())
      return action.execute();
    return false;
  }
  
  public int getColumn (int orientation)
  {
    OrientedPosition pos = getPosition(orientation);
    return pos != null ? pos.column : 0;
  }
  
  public int getRow (int orientation)
  {
    OrientedPosition pos = getPosition(orientation);
    return pos != null ? pos.row : 0;
  }
  
  
  public class OrientedPosition
  {
    public OrientedPosition (XmlPullParser parser)
    {
      String orientStr = XMLHelper.getAttributeValue(parser, "orientation");
      for (int i = 0; i <= Menu.BothOrientations; ++i)
        if (Menu.OrientationIDs[i] != null && Menu.OrientationIDs[i].equals(orientStr))
        {
          orientation = i;
          break;
        }
      column = XMLHelper.getAttributeInt(parser, "column");
      row = XMLHelper.getAttributeInt(parser, "row");
    }
    
    public int orientation = 0;
    public int column;
    public int row;
  }
  
  protected OrientedPosition getPosition (int orientation)
  {
    for (int i = 0; i < positions.size(); ++i)
    {
      OrientedPosition pos = positions.get(i);
      if ((pos.orientation & orientation) != 0)
        return pos;
    }
    return null;
  }

  public float measureText (TextPaint paint)
  {
    float maxWidth = getWidth() - getHeight();
    return maxWidth / paint.measureText(label);
  }
  
  
  public static final String Tag = "menu_item";
  public static final String LabelTag = "label";
  public static final String BackgroundTag = "background";
  public static final String PositionTag = "position";
  public static final String ActionTag = "action";

  protected int itemID;
  protected String label;
  protected ArrayList<OrientedPosition> positions = new ArrayList<>();
  /**
   * akcia vykonana po zvoleni menu
   */
  protected MenuAction action = null;
}
