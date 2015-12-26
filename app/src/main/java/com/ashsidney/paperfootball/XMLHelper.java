package com.ashsidney.paperfootball;

import org.xmlpull.v1.XmlPullParser;

public class XMLHelper
{
  static int getAndroidID (String stringID)
  {
    if (stringID != null && stringID.length() > 1 && stringID.charAt(0) == '@')
      return Integer.valueOf(stringID.substring(1)).intValue();
    return 0;
  }
  
  static String getAttributeValue (XmlPullParser parser, String attrName)
  {
    int attrCount = parser.getAttributeCount();
    for (int i = 0; i < attrCount; ++i)
      if (parser.getAttributeName(i).equals(attrName))
        return parser.getAttributeValue(i);
    return null;
  }
  
  static int getAttributeID (XmlPullParser parser, String attrName)
  {
    return getAndroidID(getAttributeValue(parser, attrName));
  }
  
  static int getAttributeInt (XmlPullParser parser, String attrName)
  {
    return Integer.valueOf(getAttributeValue(parser, attrName)).intValue();
  }
}
