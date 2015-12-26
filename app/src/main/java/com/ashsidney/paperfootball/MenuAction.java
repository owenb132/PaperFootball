package com.ashsidney.paperfootball;

import org.xmlpull.v1.XmlPullParser;

/**
 * Interfejs pre vykonanie akcie menu polozky.
 */
public interface MenuAction
{
  /**
   * Metoda vykonavajuca akciu. Kazda trieda implementujuca tento interfejs musi implementovat.
   */
  boolean execute();

  /**
   * Metoda pre konfigurovanie akcie.
   * @param parser parser konfiguracnych udajov
   */
  void setup (XmlPullParser parser);
}
