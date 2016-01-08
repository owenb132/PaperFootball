package com.ashsidney.paperfootball;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Canvas;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

/**
 * Trieda pre rozmiestnenie UI elementov do mriezky.
 */
public class UIGrid implements Renderer.UILayer, XMLHelper.ConfigOwner
{
  public void load (XMLHelper xml, Resources res)
      throws XmlPullParserException, Resources.NotFoundException, IOException
  {
    itemID = xml.getAttributeID("id");
    xml.loadChildNodes(this);
  }

  public boolean createChild (XMLHelper xml)
      throws IOException, XmlPullParserException
  {
    switch (xml.parser.getName())
    {
    }
    return false;
  }

  public void draw (Canvas canvas, float currTime)
  {

  }

  public class OrientedPosition implements XMLHelper.ConfigOwner
  {
    public void load (XMLHelper xml)
    {
      String orientStr = xml.getAttributeValue("orientation");
      if (orientStr != null)
        for (int i = 0; i <= BothOrientations; ++i)
          if (OrientationIDs[i] != null && OrientationIDs[i].equals(orientStr))
          {
            orientation = i;
            break;
          }
      column = xml.getAttributeInt("column");
      row = xml.getAttributeInt("row");
      int colCnt = xml.getAttributeInt("colCount");
      if (colCnt > 0)
        colCount = colCnt;
      int rowCnt = xml.getAttributeInt("rowCount");
      if (rowCnt > 0)
        rowCount = rowCnt;
      String stretchStr = xml.getAttributeValue("stretch");
      if (stretchStr != null)
        for (int i = 0; i < StretchBoth; ++i)
          if (StretchIDs[i] != null && StretchIDs[i].equals(stretchStr))
          {
            stretch = i;
            break;
          }
      float colPos = xml.getAttributeFloat("columnPosition");
      if (colPos >= 0.0f && colPos <= 1.0f)
        colPosition = colPos;
      float rowPos = xml.getAttributeFloat("rowPosition");
      if (rowPos >= 0.0f && rowPos <= 1.0f)
        rowPosition = rowPos;
    }

    @Override
    public boolean createChild (XMLHelper xml)
      throws IOException, XmlPullParserException
    {
      visualRepresentation = SpriteFactory.create(xml);
      return visualRepresentation != null;
    }

    public Sprite visualRepresentation = null;

    public int orientation = BothOrientations;
    public int column;
    public int row;
    public int colCount = 1;
    public int rowCount = 1;
    public float colPosition = 0.5f;
    public float rowPosition = 0.5f;
    public int stretch = StretchNone;

    public static final int PortraitOrientation = 1;
    public static final int LandscapeOrientation = 2;
    public static final int BothOrientations = PortraitOrientation | LandscapeOrientation;

    public static final int StretchNone = 0;
    public static final int StretchHorizontal = 1;
    public static final int StretchVertical = 2;
    public static final int StretchBoth = StretchHorizontal | StretchVertical;
  }
  public static final String[] OrientationIDs = { null, "portrait", "landscape", "both" };
  public static final String[] StretchIDs = { "none", "horizontal", "vertical", "both" };

  public class Item implements XMLHelper.ConfigOwner
  {
    public boolean createChild (XMLHelper xml)
        throws IOException, XmlPullParserException
    {
      switch (xml.parser.getName())
      {
        case "orientation":
          OrientedPosition position = new OrientedPosition();
          position.load(xml);
          positions.add(position);
          return true;
      }
      return false;
    }

    protected ArrayList<OrientedPosition> positions = new ArrayList<>();
  }

  protected int itemID = 0;

}
