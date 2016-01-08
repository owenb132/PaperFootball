package com.ashsidney.paperfootball;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.text.TextPaint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Zakladna trieda pre prvok ovladania (menu polozka, oznam).
 */
/*public class TouchControl extends TextSprite
{
  public void loadConfiguration (XmlPullParser parser, Resources res)
      throws XmlPullParserException, Resources.NotFoundException, IOException
  {
    itemID = XMLHelper.getAttributeID(parser, "id");
    int initDepth = parser.getDepth();
    for (int eventType = parser.next(); eventType != XmlPullParser.END_TAG || parser.getDepth() > initDepth;
         eventType = parser.next())
      if (eventType == XmlPullParser.START_TAG)
        loadAttribute(parser, res);
  }

  public void draw (Canvas canvas, TextPaint paint, float currTime)
  {
    assert itemID > 0;
    canvas.save();
    super.draw(canvas, currTime);
    float pos[] = { 0.0f, 0.0f };
    fullTransform.mapPoints(pos);
    pos[0] += getWidth() * 0.5f;
    pos[1] += (getHeight() + paint.getTextSize()) * 0.5f;
    canvas.drawText(label, pos[0], pos[1], paint);
    canvas.restore();
  }

  public float measureText (TextPaint paint)
  {
    assert itemID > 0;
    float maxWidth = getWidth() - getHeight();
    return maxWidth / paint.measureText(label);
  }

  public int getColumn (int orientation)
  {
    assert itemID > 0;
    OrientedPosition pos = getPosition(orientation);
    return pos != null ? pos.column : 0;
  }

  public int getRow (int orientation)
  {
    assert itemID > 0;
    OrientedPosition pos = getPosition(orientation);
    return pos != null ? pos.row : 0;
  }

  public class OrientedPosition
  {
    public OrientedPosition (XmlPullParser parser)
    {
      String orientStr = XMLHelper.getAttributeValue(parser, "orientation");
      for (int i = 0; i <= BothOrientations; ++i)
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

    public static final int PortraitOrientation = 1;
    public static final int LandscapeOrientation = 2;
    public static final int BothOrientations = PortraitOrientation | LandscapeOrientation;
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

  protected boolean loadAttribute (XmlPullParser parser, Resources res)
  {
    switch (parser.getName())
    {
      case LabelTag:
        label = res.getString(XMLHelper.getAttributeID(parser, "id"));
        return true;
      case BackgroundTag:
        bitmap = loadBitmap(XMLHelper.getAttributeID(parser, "id"), res);
        return true;
      case PositionTag:
        OrientedPosition pos = new OrientedPosition(parser);
        if (pos.orientation > 0)
          positions.add(pos);
        return true;
    }
    return false;
  }

  public static final String LabelTag = "label";
  public static final String BackgroundTag = "background";
  public static final String PositionTag = "position";

  protected int itemID = 0;
  protected String label = null;
  protected ArrayList<OrientedPosition> positions = new ArrayList<>();
}*/
