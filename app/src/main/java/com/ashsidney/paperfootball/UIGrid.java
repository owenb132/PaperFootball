package com.ashsidney.paperfootball;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.text.TextPaint;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Trieda pre rozmiestnenie UI elementov do mriezky.
 */
public class UIGrid implements Renderer.UILayer, XMLHelper.ConfigOwner
{
  public void load (XMLHelper xml, Resources res)
      throws XmlPullParserException, Resources.NotFoundException, IOException
  {
    gridID = xml.getAttributeID("id");
    xml.loadChildNodes(this);
  }

  public boolean createChild (XMLHelper xml)
      throws IOException, XmlPullParserException
  {
    switch (xml.parser.getName())
    {
      case "item":
        Item item = new Item();
        item.load(xml);
        int groupID = item.getFontGroupID();
        if (groups.containsKey(groupID))
          groups.get(groupID).items.add(item);
        else
        {
          FontGroup newGroup = new FontGroup();
          newGroup.fontGroupID = groupID;
          newGroup.items.add(item);
          groups.put(groupID, newGroup);
        }
        return true;
    }
    return false;
  }

  public void draw (Canvas canvas, float currTime)
  {
    TextPaint paint = new TextPaint();
    int orient = canvas.getWidth() > canvas.getHeight()
      ? OrientedPosition.LandscapeOrientation : OrientedPosition.PortraitOrientation;

    if (initLayout)
      initLayout = calcItemPositions(canvas, paint);

    canvas.save();
    canvas.setMatrix(viewMatrix);

    paint.setTextAlign(TextPaint.Align.CENTER);

    for (int i = 0; i < groups.size(); ++i)
      groups.get(i).draw(canvas, paint, currTime, orient);

    canvas.restore();
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
    public void load (XMLHelper xml) throws IOException, XmlPullParserException
    {
      itemID = xml.getAttributeID("id");
      fontGroupID = xml.getAttributeID("fontID");
      String visibleStr = xml.getAttributeValue("visible");
      visible = visibleStr == null || visibleStr.equalsIgnoreCase("true");
      xml.loadChildNodes(this);
    }

    public boolean createChild (XMLHelper xml)
        throws IOException, XmlPullParserException
    {
      switch (xml.parser.getName())
      {
        /*case "action":
          xml.parser.next();
          action = ActionFactory.create(xml);
          return action != null;*/
        case "orientation":
          OrientedPosition position = new OrientedPosition();
          position.load(xml);
          positions.add(position);
          return true;
      }
      return false;
    }

    public void draw (Canvas canvas, TextPaint paint, float currTime, int orient)
    {
    }

    public int getItemID ()
    {
      return itemID;
    }

    public int getFontGroupID ()
    {
      return fontGroupID;
    }

    /// identifikator polozky menu
    protected int itemID;
    /// akcia vykonana, ked je polozka UI aktivovana
    //protected Action action;
    /// pozicie polozky v roznych orientaciach
    protected ArrayList<OrientedPosition> positions = new ArrayList<>();
    /// cislo skupiny velkosti fontu
    protected int fontGroupID;
    /// ci je polozka viditelna alebo nie
    protected boolean visible;
  }

  /**
   * Struktura pre zoznam poloziek s jednym typom pisma
   */
  class FontGroup
  {
    public void draw (Canvas canvas, TextPaint paint, float currTime, int orient)
    {
      paint.setTextSize(fontSize);
      for (Item item : items)
        item.draw(canvas, paint, currTime, orient);
    }

    public int fontGroupID = 0;
    public float fontSize;
    public ArrayList<Item> items = new ArrayList<>();
  }

  protected boolean calcItemPositions (Canvas canvas, TextPaint paint)
  {
    return false;
  }

  /// identifikacia mriezky pouzivatelskeho rozhrania
  protected int gridID = 0;
  /// zoznam poloziek v mriezke roztiredeny podla skupin fontov
  protected HashMap<Integer, FontGroup> groups = new HashMap<>();
  /// rozlozenie poloziek ma byt inicializovane
  protected boolean initLayout = true;
  /// matica zobrazenia
  Matrix viewMatrix;
}
