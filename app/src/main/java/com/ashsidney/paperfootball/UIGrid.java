package com.ashsidney.paperfootball;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextPaint;

import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Trieda pre rozmiestnenie UI elementov do mriezky.
 */
public class UIGrid implements Renderer.UILayer, GestureHandler.Listener, XMLHelper.ConfigOwner
{
  @Override
  public int getID ()
  {
    return gridID;
  }

  @Override
  public void load (XMLHelper xml)
      throws XmlPullParserException, Resources.NotFoundException, IOException
  {
    gridID = xml.getAttributeID("id");
    xml.loadChildNodes(this);
  }

  @Override
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

  @Override
  public void draw (Canvas canvas, float currTime)
  {
    TextPaint paint = new TextPaint();
    int orient = canvas.getWidth() > canvas.getHeight()
      ? OrientedPosition.LandscapeOrientation : OrientedPosition.PortraitOrientation;

    if (initLayout)
      initLayout = calcItemPositions(canvas, paint, orient);

    canvas.save();
    canvas.setMatrix(viewMatrix);

    paint.setTextAlign(TextPaint.Align.CENTER);

    for (int i = 0; i < groups.size(); ++i)
      groups.get(i).draw(canvas, paint, currTime, orient);

    canvas.restore();
  }

  @Override
  public boolean onGesture (GestureEvent event)
  {
    for (FontGroup group : groups.values())
      for (Item item : group.items)
        if (item.onGesture(event))
          return true;
    return false;
  }


  public class OrientedPosition implements XMLHelper.ConfigOwner
  {
    public void load (XMLHelper xml) throws IOException, XmlPullParserException
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

      xml.loadChildNodes(this);
    }

    @Override
    public boolean createChild (XMLHelper xml)
      throws IOException, XmlPullParserException
    {
      visualRepresentation = SpriteFactory.create(xml);
      return visualRepresentation != null;
    }

    public boolean isOriented (int orient)
    {
      return (orientation & orient) != 0;
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


  public class Item implements XMLHelper.ConfigOwner, GestureHandler.Listener
  {
    public void load (XMLHelper xml) throws IOException, XmlPullParserException
    {
      itemID = xml.getAttributeID("id");
      fontGroupID = xml.getAttributeID("fontID");
      String visibleStr = xml.getAttributeValue("visible");
      visible = visibleStr == null || visibleStr.equalsIgnoreCase("true");

      xml.loadChildNodes(this);
    }

    @Override
    public boolean createChild (XMLHelper xml)
        throws IOException, XmlPullParserException
    {
      switch (xml.parser.getName())
      {
        /*case "action":
          xml.parser.next();
          action = ActionFactory.create(xml);
          return action != null;*/
        case "position":
          OrientedPosition position = new OrientedPosition();
          position.load(xml);
          positions.add(position);
          return true;
      }
      return false;
    }

    @Override
    public boolean onGesture (GestureEvent event)
    {
      return false;
    }

    public void draw (Canvas canvas, Paint paint, float currTime, int orient)
    {
      OrientedPosition pos = getPosition(orient);
      pos.visualRepresentation.draw(canvas, paint, currTime);
    }

    public int getItemID ()
    {
      return itemID;
    }

    public int getFontGroupID ()
    {
      return fontGroupID;
    }

    protected OrientedPosition getPosition (int orient)
    {
      for (OrientedPosition pos : positions)
        if (pos.isOriented(orient))
          return pos;
      return null;
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
    public void draw (Canvas canvas, Paint paint, float currTime, int orient)
    {
      paint.setTextSize(fontSize);
      for (Item item : items)
        item.draw(canvas, paint, currTime, orient);
    }

    public int fontGroupID = 0;
    public float fontSize;
    public ArrayList<Item> items = new ArrayList<>();
  }

  protected boolean calcItemPositions (Canvas canvas, Paint paint, int orient)
  {
    // urci rozsahy pozicii poloziek
    int[][] minMax = { { Integer.MAX_VALUE, Integer.MAX_VALUE },
      { Integer.MIN_VALUE, Integer.MIN_VALUE } };
    for (FontGroup group : groups.values())
      for (Item item : group.items)
      {
        OrientedPosition pos = item.getPosition(orient);
        minMax[0][0] = Math.min(minMax[0][0], pos.column);
        minMax[0][1] = Math.min(minMax[0][1], pos.row);
        minMax[1][0] = Math.max(minMax[1][0], pos.column + pos.colCount);
        minMax[1][1] = Math.max(minMax[1][1], pos.row + pos.rowCount);
      }

    // vypocitaj celkove rozmery vrstvy z poloziek v kazdej bunke
    int[] sizes = { minMax[1][0] - minMax[0][0], minMax[1][1] - minMax[0][1] };
    float cellMax[][] = { new float[sizes[0]], new float[sizes[1]] };
    for (FontGroup group : groups.values())
      for (Item item : group.items)
      {
        OrientedPosition pos = item.getPosition(orient);
        if (pos.colCount == 1)
        {
          int colIdx = pos.column - minMax[0][0];
          cellMax[0][colIdx] = Math.max(cellMax[0][colIdx], pos.visualRepresentation.getWidth());
        }
        if (pos.rowCount == 1)
        {
          int rowIdx = pos.row - minMax[0][1];
          cellMax[1][rowIdx] = Math.max(cellMax[1][rowIdx], pos.visualRepresentation.getHeight());
        }
      }
    // zarataj polozky siahajuce cez viacero buniek
    for (FontGroup group : groups.values())
      for (Item item : group.items)
      {
        OrientedPosition pos = item.getPosition(orient);
        if (pos.colCount > 1)
        {
          int colIdx = pos.column - minMax[0][0];
          float colSum = 0.0f;
          for (int i = 0; i < pos.colCount; ++i)
            colSum += cellMax[0][colIdx + i];
          // ak je polozka vacsia ako sucet stlpcov, ktore zabera
          if (pos.visualRepresentation.getWidth() > colSum)
          {
            // zvacsi sa kazdy stlpec pomerne
            float coef = pos.visualRepresentation.getWidth() / colSum;
            for (int i = 0; i < pos.colCount; ++i)
              cellMax[0][colIdx + i] *= coef;
          }
        }
        if (pos.rowCount > 1)
        {
          int rowIdx = pos.row - minMax[0][1];
          float rowSum = 0.0f;
          for (int i = 0; i < pos.rowCount; ++i)
            rowSum += cellMax[1][rowIdx + i];
          // ak je polozka vacsia ako sucet riadkov, ktore zabera
          if (pos.visualRepresentation.getHeight() > rowSum)
          {
            // zvacsi sa kazdy riadok pomerne
            float coef = pos.visualRepresentation.getHeight() / rowSum;
            for (int i = 0; i < pos.rowCount; ++i)
              cellMax[1][rowIdx + i] *= coef;
          }
        }
      }

    float[] layerSize = { 0.0f, 0.0f };
    for (int i = 0; i < 2; ++i)
      for (int idx = 0; idx < sizes[i]; ++idx)
        layerSize[i] += cellMax[i][idx];

    // urci zvacsenie mriezky
    float[] scales = { canvas.getWidth() / layerSize[0], canvas.getHeight() / layerSize[1] };
    float scale = Math.min(scales[0], scales[1]);
    viewMatrix.preScale(scale, scale);

    // urci pozicie stlpcov a riadkov mriezky
    float[][] positions = { new float[sizes[0] + 1], new float[sizes[1] + 1] };
    for (int i = 0; i < 2; ++i)
    {
      positions[i][0] = 0.0f;
      float scaleCoef = scales[i] / scale;
      for (int idx = 0; idx < sizes[i]; ++idx)
        positions[i][idx + 1] = cellMax[i][idx] * scaleCoef + positions[i][idx];
    }

    // nastav poziciu pre kazdy prvok
    //final int[] stretchConsts = {OrientedPosition.StretchHorizontal, OrientedPosition.StretchVertical};
    for (FontGroup group : groups.values())
      for (Item item : group.items)
      {
        OrientedPosition pos = item.getPosition(orient);
        int[] startIdx = { pos.column - minMax[0][0], pos.row - minMax[0][1] };
        int[] endIdx = { startIdx[0] + pos.colCount, startIdx[1] + pos.rowCount };
        float[] visSize = { pos.visualRepresentation.getWidth(), pos.visualRepresentation.getHeight() };
        float[] itemPos = { pos.colPosition, pos.rowPosition };
        for (int i = 0; i < 2; ++i)
        {
          itemPos[i] = (positions[i][endIdx[i]] - positions[i][startIdx[i]] - visSize[i]) * itemPos[i]
              + visSize[i] * 0.5f + positions[i][startIdx[i]];
        }
        pos.visualRepresentation.setPosition(itemPos);
      }

    // zmeraj pomery sirok textov a pozadi v prvkoch menu
    for (FontGroup group : groups.values())
    {
      paint.setTextSize(group.fontSize);
      float minFontSize = Float.MAX_VALUE;
      for (Item item : group.items)
      {
        OrientedPosition pos = item.getPosition(orient);
        if (pos.visualRepresentation instanceof TextSprite)
          minFontSize = Math.min(minFontSize, ((TextSprite)pos.visualRepresentation).getFontSize(paint));
      }
      group.fontSize = minFontSize;
    }

    return false;
  }

  /// identifikacia mriezky pouzivatelskeho rozhrania
  protected int gridID = 0;
  /// zoznam poloziek v mriezke roztiredeny podla skupin fontov
  protected HashMap<Integer, FontGroup> groups = new HashMap<>();
  /// rozlozenie poloziek ma byt inicializovane
  protected boolean initLayout = true;
  /// matica zobrazenia
  Matrix viewMatrix = new Matrix();
}
