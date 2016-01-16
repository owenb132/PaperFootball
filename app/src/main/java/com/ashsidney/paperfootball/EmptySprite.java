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
        return true;
    }
    return false;
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
}
