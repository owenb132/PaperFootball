package com.ashsidney.paperfootball;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;

import org.xmlpull.v1.XmlPullParser;
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
        int initDepth = xml.parser.getDepth();
        for (int eventType = xml.parser.next(); eventType != XmlPullParser.END_TAG || xml.parser.getDepth() > initDepth;
            eventType = xml.parser.next())
          if (eventType == XmlPullParser.TEXT)
            text += xml.parser.getText();
        return true;
      case "border":
        border[0] = xml.getAttributeFloat("width");
        border[1] = xml.getAttributeFloat("height");
        return true;
    }
    return false;
  }

  @Override
  public void draw (Canvas canvas, float currTime)
  {
    TextPaint paint = new TextPaint();
    if (fontInitialize)
      calcFontSize(paint);
    paint.setTextSize(fontSize);
    super.draw(canvas, currTime);
    float pos[] = { 0.0f, 0.0f };
    fullTransform.mapPoints(pos);
    pos[0] += getWidth() * 0.5f;
    pos[1] += (getHeight() + paint.getTextSize()) * 0.5f;
    canvas.drawText(text, pos[0], pos[1], paint);
  }

  public float getFontSize (TextPaint paint)
  {
    if (fontInitialize)
      calcFontSize(paint);
    return fontSize;
  }

  public void setText (String text)
  {
    this.text = text;
    calcFontSize(new TextPaint());
  }

  protected void calcFontSize (TextPaint paint)
  {
    fontInitialize = true;
    Rect textSize = new Rect();
    paint.getTextBounds(text, 0, text.length(), textSize);
    float scaleX = (1.0f - border[0]) * getWidth() / textSize.width();
    float scaleY = (1.0f - border[1]) * getHeight() / textSize.height();
    fontSize = paint.getTextSize() * (scaleX > scaleY ? scaleY : scaleX);
  }


  protected String text = "";
  protected float border[] = { 0.0f, 0.0f };

  protected boolean fontInitialize = true;
  protected float fontSize;
}
