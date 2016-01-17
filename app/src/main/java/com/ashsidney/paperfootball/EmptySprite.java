package com.ashsidney.paperfootball;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Prazdne miesto tvariace sa ako sprajt.
 */
public class EmptySprite extends Sprite
{
  @Override
  public boolean createChild (XMLHelper xml)
      throws IOException, XmlPullParserException
  {
    switch (xml.parser.getName())
    {
      case "gap":
        width = xml.getAttributeFloat("width");
        height = xml.getAttributeFloat("height");
        baseWidth = width;
        baseHeight = height;
        screenWidth = xml.getAttributeFloat("screenWidth");
        screenHeight = xml.getAttributeFloat("screenHeight");
        widthCoef = xml.getAttributeFloat("widthCoef");
        heightCoef = xml.getAttributeFloat("heightCoef");
        return true;
    }
    return false;
  }

  @Override
  public void initDraw (Canvas canvas)
  {
    if (screenWidth > 0.0f && widthCoef > 0.0f)
      width = widthCoef * (canvas.getWidth() - screenWidth) + baseWidth;
    if (screenHeight > 0.0f && heightCoef > 0.0f)
      height = heightCoef * (canvas.getHeight() - screenHeight) + baseHeight;
  }

  @Override
  public void draw (Canvas canvas, Paint paint, float currTime)
  {}

  @Override
  public float getWidth ()
  {
    return width;
  }

  @Override
  public float getHeight ()
  {
    return height;
  }

  protected float width = 0.0f;
  protected float height = 0.0f;
  protected float baseWidth = 0.0f;
  protected float baseHeight = 0.0f;
  protected float screenWidth = 0.0f;
  protected float screenHeight = 0.0f;
  protected float widthCoef = 0.0f;
  protected float heightCoef = 0.0f;
}
