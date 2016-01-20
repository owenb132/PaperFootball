package com.ashsidney.paperfootball;


public class InfoHandler extends Thread
{
  /**
   * Funkcia na zobrazenie spravy na pozadovany cas
   * @param messageID
   * @param timeout
   */
  public static void showInfo (int messageID, float timeout)
  {
    // zhasni aktualny oznam
    if (currInfo != null)
      currInfo.close();
    // vytvor novy oznam
    currInfo = new InfoHandler(messageID, timeout);
    // spusti oznam
    currInfo.start();
  }

  /**
   * Konstruktor info objektu
   * @param messageID identifikator polozky so spravou
   * @param timeout casovy interval zobrazenia spravy
   */
  protected InfoHandler (int messageID, float timeout)
  {
    this.messageID = messageID;
    this.timeout = timeout;
  }

  /**
   * Metoda pre cinnost v samostatnom vlakne
   */
  @Override
  public void run ()
  {
    // prejdi vsetky polozky v mriezke a nastav viditelnost vybranej sprave
    PaperFootballActivity act = PaperFootballActivity.GetActivity();
    UIGrid grid = (UIGrid)act.getUILayer(infoGridID);
    if (grid != null)
      for (UIGrid.Item item : grid.getItems())
        item.setVisible(item.getItemID() == messageID);
    // zobraz oznam
    opened = act.openUI(infoGridID);
    // nechaj ho svietit po zvoleny cas
    try
    {
      sleep((long)(1000 * timeout));
    }
    catch (Exception e) {}
    // zavri oznam
    close();
  }

  public synchronized void close()
  {
    // zavri oznam
    if (opened)
      PaperFootballActivity.GetActivity().closeUI(infoGridID);
    opened = false;
  }

  protected int messageID;
  protected float timeout;
  protected boolean opened = false;

  protected static InfoHandler currInfo = null;

  protected static int infoGridID = R.id.infoLayer;
}
