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

  /**
   * Vrat zoznam poloziek
   * @return zoznam poloziek mriezky
   */
  public ArrayList<UIGrid.Item> getItems ()
  {
    return items;
  }

  @Override
  public void load (XMLHelper xml)
      throws XmlPullParserException, Resources.NotFoundException, IOException
  {
    gridID = xml.getAttributeID("id");
    float scale = xml.getAttributeFloat("maxScale");
    if (scale > 0.0f)
      maxScale = scale;
    exclusiveGesture = xml.getAttributeBool("exclusive", exclusiveGesture);

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
        item.load(xml, this);
        items.add(item);
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
    orientation = canvas.getWidth() > canvas.getHeight()
      ? OrientedPosition.LandscapeOrientation : OrientedPosition.PortraitOrientation;

    if (initLayout)
      initLayout = calcItemPositions(canvas, paint);

    canvas.save();
    canvas.setMatrix(viewMatrix);

    paint.setTextAlign(TextPaint.Align.CENTER);

    for (FontGroup group : groups.values())
      group.draw(canvas, paint, currTime, orientation);

    canvas.restore();
  }

  @Override
  public boolean onGesture (GestureEvent event)
  {
    if (event.getType() == GestureEvent.EventType.Touch && !initLayout)
    {
      GestureEvent gridEvent = event.clone();
      gridEvent.getTransformation().addToTranslation(invMatrix);
      for (FontGroup group : groups.values())
        for (Item item : group.items)
          if (item.onGesture(gridEvent))
            return true;
    }
    return exclusiveGesture;
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


  public class Item implements XMLHelper.ConfigOwner
  {
    public void load (XMLHelper xml, UIGrid owner) throws IOException, XmlPullParserException
    {
      itemID = xml.getAttributeID("id");
      this.owner = owner;
      fontGroupID = Math.max(xml.getAttributeInt("fontID"), 0);
      visible = xml.getAttributeBool("visible", visible);

      xml.loadChildNodes(this);
    }

    @Override
    public boolean createChild (XMLHelper xml)
        throws IOException, XmlPullParserException
    {
      UIFactory.UIAction newAction = UIFactory.createAction(xml, owner);
      if (newAction != null)
      {
        action = newAction;
        return true;
      }

      switch (xml.parser.getName())
      {
        case "position":
          OrientedPosition position = new OrientedPosition();
          position.load(xml);
          positions.add(position);
          return true;
      }
      return false;
    }

    public boolean onGesture (GestureEvent event)
    {
      OrientedPosition pos = getPosition(owner.orientation);
      float[] evPos = event.getTransformation().getTranslation();
      float[] minPos = pos.visualRepresentation.getPosition();
      float[] maxPos = { minPos[0] + pos.visualRepresentation.getWidth(),
        minPos[1] + pos.visualRepresentation.getHeight() };
      if (evPos[0] >= minPos[0] && evPos[0] < maxPos[0]
          && evPos[1] >= minPos[1] && evPos[1] < maxPos[1])
      {
        if (action != null && action.execute())
          return true;
      }
      return false;
    }

    public void draw (Canvas canvas, Paint paint, float currTime, int orient)
    {
      if (visible)
      {
        OrientedPosition pos = getPosition(orient);
        pos.visualRepresentation.draw(canvas, paint, currTime);
      }
    }

    public int getItemID ()
    {
      return itemID;
    }

    public int getFontGroupID ()
    {
      return fontGroupID;
    }

    public void setVisible (boolean vis)
    {
      visible = vis;
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
    /// vlastnik polozky menu
    protected UIGrid owner = null;
    /// akcia vykonana, ked je polozka UI aktivovana
    protected UIFactory.UIAction action = null;
    /// pozicie polozky v roznych orientaciach
    protected ArrayList<OrientedPosition> positions = new ArrayList<>();
    /// cislo skupiny velkosti fontu
    protected int fontGroupID = 0;
    /// ci je polozka viditelna alebo nie
    protected boolean visible = true;
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
    public float fontSize = 32.0f;
    public ArrayList<Item> items = new ArrayList<>();
  }

  protected boolean calcItemPositions (Canvas canvas, Paint paint)
  {
    // urci rozsahy pozicii poloziek
    int[][] minMax = { { Integer.MAX_VALUE, Integer.MAX_VALUE },
      { Integer.MIN_VALUE, Integer.MIN_VALUE } };
    for (FontGroup group : groups.values())
      for (Item item : group.items)
      {
        OrientedPosition pos = item.getPosition(orientation);
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
        OrientedPosition pos = item.getPosition(orientation);
        pos.visualRepresentation.initDraw(canvas);
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
        OrientedPosition pos = item.getPosition(orientation);
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
    scale = Math.min(scale, maxScale);
    viewMatrix = new Matrix();
    viewMatrix.preScale(scale, scale);
    invMatrix = new Matrix();
    viewMatrix.invert(invMatrix);

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
        OrientedPosition pos = item.getPosition(orientation);
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
        OrientedPosition pos = item.getPosition(orientation);
        if (pos.visualRepresentation instanceof TextSprite)
          minFontSize = Math.min(minFontSize, ((TextSprite)pos.visualRepresentation).getFontSize(paint));
      }
      group.fontSize = minFontSize;
    }

    return false;
  }

  /// identifikacia mriezky pouzivatelskeho rozhrania
  protected int gridID = 0;
  /// zoznam poloziek mriezky
  protected ArrayList<Item> items = new ArrayList<>();
  /// zoznam poloziek v mriezke roztriedeny podla skupin fontov
  protected HashMap<Integer, FontGroup> groups = new HashMap<>();
  /// maximalna hodnota skalovania vrstvy
  float maxScale = 1.0f;
  /// exkluzivny pristup k ovladaniu
  boolean exclusiveGesture = false;

  /// rozlozenie poloziek ma byt inicializovane
  protected boolean initLayout = true;
  /// matica zobrazenia
  Matrix viewMatrix;
  /// inverzna matica zobrazenia pre preklad suradnic ovladania
  Matrix invMatrix;
  /// aktualna orientacia displeja
  int orientation;
}
