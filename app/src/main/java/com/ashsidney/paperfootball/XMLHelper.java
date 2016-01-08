package com.ashsidney.paperfootball;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;


public class XMLHelper
{
  /**
   * Ziskaj identifikator v zdrojoch Androidu
   * @param stringID textova hodnota identifikatora
   * @return cislo identifikatora
   */
  public static int getAndroidID (String stringID)
  {
    if (stringID != null && stringID.length() > 1 && stringID.charAt(0) == '@')
      return Integer.valueOf(stringID.substring(1));
    return 0;
  }

  /**
   * Ziskaj textovu hodnotu atributu aktualneho uzla
   * @param attrName nazov atributu
   * @return textovy retazec s hodnotou atributu
   */
  public String getAttributeValue (String attrName)
  {
    int attrCount = parser.getAttributeCount();
    for (int i = 0; i < attrCount; ++i)
      if (parser.getAttributeName(i).equals(attrName))
        return parser.getAttributeValue(i);
    return null;
  }
  
  public int getAttributeID (String attrName)
  {
    String value = getAttributeValue(attrName);
    return value != null ? getAndroidID(value) : 0;
  }
  
  public int getAttributeInt (String attrName)
  {
    String value = getAttributeValue(attrName);
    return value != null ? Integer.valueOf(value) : Integer.MIN_VALUE;
  }

  public float getAttributeFloat (String attrName)
  {
    String value = getAttributeValue(attrName);
    return value != null ? Float.valueOf(value) : -Float.MAX_VALUE;
  }

  public void loadChildNodes (ConfigOwner owner)
    throws IOException, XmlPullParserException
  {
    int initDepth = parser.getDepth();
    for (int eventType = parser.next(); eventType != XmlPullParser.END_TAG || parser.getDepth() > initDepth;
        eventType = parser.next())
      if (eventType == XmlPullParser.START_TAG)
        owner.createChild(this);
  }

  public void open (int dataID, Resources res)
    throws IOException, XmlPullParserException
  {
    resources = res;
    // otvor parser s datami
    parser = resources.getXml(dataID);
    // preskoc uzol <resources>
    while (parser.next() != XmlPullParser.START_TAG || !parser.getName().equals("resources"));
  }

  public void load (ConfigOwner owner, int dataID, Resources res)
  {
    try
    {
      // otvor parser s datami
      open(dataID, res);
      // nacitaj data
      loadChildNodes(owner);
    }
    catch (Exception e)
    {
      Log.e("PaperFootball", "XML load failed:" + e.getMessage());
    }
  }

  /**
   * Interfejs pre vlastnikov konfigurovatelnych objektov
   */
  public interface ConfigOwner
  {
    /**
     * Vytvor objekt z konfiguracnych udajov a pridaj ho vlastnikovi
     * @param xml XML helper pre pristup k datam
     * @return true, ak bol objekt vytvoreny
     */
    boolean createChild (XMLHelper xml)
        throws IOException, XmlPullParserException;
  }

  /// parser XML udajov
  public XmlPullParser parser = null;
  /// zdroje udajov
  public Resources resources = null;
}
