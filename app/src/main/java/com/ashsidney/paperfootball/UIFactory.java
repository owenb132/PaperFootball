package com.ashsidney.paperfootball;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Trieda pre tvorbu UI vrstiev.
 */
public class UIFactory
{
  /**
   * Funkcia na vytvorenie UI vrstvy z konfiguracnych udajov.
   */
  public static Renderer.UILayer createUILayer (XMLHelper xml)
      throws IOException, XmlPullParserException
  {
    Renderer.UILayer layer = null;

    switch (xml.parser.getName())
    {
      case "grid":
        layer = new UIGrid();
        break;
    }

    if (layer != null)
      layer.load(xml);

    return layer;
  }

  /**
   * Funkcia na vytvorenie UI akcie z konfiguracnych udajov.
   */
  /*public static Renderer.UILayer createAction (XMLHelper xml)
      throws IOException, XmlPullParserException
  {
    Renderer.UILayer layer = null;

    switch (xml.parser.getName())
    {
      case "grid":
        layer = new UIGrid();
        break;
    }

    if (layer != null)
      layer.load(xml);

    return layer;
  }*/
}
