package com.ashsidney.paperfootball;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Tovaren na vyrobu sprajtov.
 */
public class SpriteFactory
{
  /**
   * Funkcia na vytvorenie sprajtu z konfiguracnych udajov.
   */
  public static Sprite create (XMLHelper xml)
      throws IOException, XmlPullParserException
  {
    Sprite sprite = null;

    switch (xml.parser.getName())
    {
      case "sprite":
        sprite = new Sprite();
        break;
      case "animSprite":
        sprite = new AnimSprite();
        break;
      case "textSprite":
        sprite = new TextSprite();
        break;
      case "empty":
        sprite = new EmptySprite();
    }

    if (sprite != null)
      sprite.load(xml);

    return sprite;
  }
}
