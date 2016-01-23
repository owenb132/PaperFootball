package com.ashsidney.paperfootball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Zakladna trieda pre sprajty s textom.
 */
public class TextSprite extends Sprite
{
  @Override
  public boolean createChild (XMLHelper xml) throws IOException, XmlPullParserException
  {
    if (super.createChild(xml))
      return true;
    switch (xml.parser.getName())
    {
      case "text":
        text = xml.getAttributeString("id");
        return true;
      case "border":
        border[0] = xml.getAttributeFloat("width");
        border[1] = xml.getAttributeFloat("height");
        return true;
    }
    return false;
  }

  @Override
  public void draw (Canvas canvas, Paint paint, float currTime)
  {
    super.draw(canvas, paint, currTime);
    float pos[] = { 0.0f, 0.0f };
    fullTransform.mapPoints(pos);
    pos[0] += getWidth() * 0.5f;
    pos[1] += (getHeight() - paint.ascent()) * 0.5f;
    canvas.drawText(text, pos[0], pos[1], paint);
  }

  public float getFontSize (Paint paint)
  {
    if (fontInitialize)
      calcFontSize(paint);
    return fontSize;
  }

  public void setText (String text)
  {
    this.text = text;
    calcFontSize(new Paint());
  }

  protected void calcFontSize (Paint paint)
  {
    fontInitialize = false;
    Rect textSize = new Rect();
    paint.getTextBounds(text, 0, text.length(), textSize);
    float scaleX = (1.0f - border[0]) * getWidth() / textSize.width();
    float scaleY = (1.0f - border[1]) * getHeight() / textSize.height();
    fontSize = paint.getTextSize() * Math.min(scaleX, scaleY);
  }


  protected String text = "";
  protected float border[] = { 0.0f, 0.0f };

  protected boolean fontInitialize = true;
  protected float fontSize;
}
