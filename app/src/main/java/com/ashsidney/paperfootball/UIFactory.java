package com.ashsidney.paperfootball;

import android.content.res.Resources;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Trieda pre tvorbu UI vrstiev.
 */
public class UIFactory
{
  /**
   * Interfejs pre akcie vyvolane pouzivatelom.
   */
  public interface UIAction
  {
    /**
     * Metoda vykonavajuca akciu. Kazda trieda implementujuca tento interfejs musi implementovat.
     */
    boolean execute ();
    /**
     * Metoda pre konfiguraciu akcie.
     *
     * \param xml konfiguracne udaje
     */
    void load (XMLHelper xml, Renderer.UILayer layer);
  }

  /**
   * Funkcia na nacitanie UI vrstiev.
   */
  public static ArrayList<Renderer.UILayer> loadUILayers (int dataID, Resources res)
  {
    ArrayList<Renderer.UILayer> layers = new ArrayList<>();

    try
    {
      XMLHelper xml = new XMLHelper();
      xml.open(dataID, res);

      int initDepth = xml.parser.getDepth();
      for (int eventType = xml.parser.next(); eventType != XmlPullParser.END_TAG || xml.parser.getDepth() > initDepth;
          eventType = xml.parser.next())
        if (eventType == XmlPullParser.START_TAG)
          layers.add(createLayer(xml));
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "UILayers load failed:" + e.getMessage());
    }
    return layers;
  }

  /**
   * Funkcia na vytvorenie UI vrstvy z konfiguracnych udajov.
   */
  public static Renderer.UILayer createLayer (XMLHelper xml)
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
  public static UIAction createAction (XMLHelper xml, Renderer.UILayer layer)
    throws IOException, XmlPullParserException
  {
    UIAction action = null;

    switch (xml.parser.getName())
    {
      case "grid":
        layer = new UIGrid();
        break;*/
    }

    if (action != null)
      action.load(xml, layer);

    return action;
  }
}
