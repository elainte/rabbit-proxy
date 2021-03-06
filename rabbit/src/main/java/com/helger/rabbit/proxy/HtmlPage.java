package com.helger.rabbit.proxy;

import com.helger.commons.url.SMap;
import com.helger.rabbit.http.EStatusCode;

/**
 * This class is intended to be used as a template for metapages. It provides
 * methods to get different part of the HTML-page so we can get a consistent
 * interface.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HtmlPage
{

  private static final String BASICLOGO = "http://www.khelekore.org/rabbit/images/smallRabbIT4.png";

  private static SMap config = setup ();

  // No dont instanciate this.
  private HtmlPage ()
  {
    // empty
  }

  /**
   * Return a simple HTMLheader.
   *
   * @return a HTMLHeader.
   */
  public static String getPageHeader ()
  {
    return ("<html><head><title>?</title></head>\n" +
            "<body bgcolor=\"" +
            config.get ("bodybgcolor") +
            "\" text=\"" +
            config.get ("bodytext") +
            "\" link=\"" +
            config.get ("bodylink") +
            "\" alink=\"" +
            config.get ("bodyalink") +
            "\" vlink=\"" +
            config.get ("bodyvlink") +
            "\">\n");
  }

  /**
   * Return a HTMLheader.
   *
   * @param con
   *        the Connection handling the request
   * @param type
   *        the StatusCode of the request
   * @return a HTMLHeader.
   */
  public static String getPageHeader (final Connection con, final EStatusCode type)
  {
    return getPageHeader (con, type.getDescription ());
  }

  /**
   * Return a HTMLheader.
   *
   * @param con
   *        the Connection creating the page
   * @param title
   *        the title of this page.
   * @return a HTMLHeader.
   */
  public static String getPageHeader (final Connection con, final String title)
  {
    int idx;
    final HttpProxy proxy = con.getProxy ();
    String basiclogo = proxy.getConfig ().getProperty (proxy.getClass ().getName (), "logo", BASICLOGO);
    while ((idx = basiclogo.indexOf ("$proxy")) > -1)
    {
      basiclogo = basiclogo.substring (0, idx) +
                  proxy.getHost ().getHostName () +
                  ":" +
                  proxy.getPort () +
                  basiclogo.substring (idx + "$proxy".length ());
    }

    return ("<html><head><title>" +
            title +
            "</title></head>\n" +
            "<body bgcolor=\"" +
            config.get ("bodybgcolor") +
            "\" text=\"" +
            config.get ("bodytext") +
            "\" link=\"" +
            config.get ("bodylink") +
            "\" alink=\"" +
            config.get ("bodyalink") +
            "\" vlink=\"" +
            config.get ("bodyvlink") +
            "\">\n" +
            "<img src=\"" +
            basiclogo +
            "\" alt=\"RabbIT logo\" align=\"right\">\n" +
            "<h1>" +
            title +
            "</h1>\n");
  }

  /**
   * Return a table header with given width (int %) and given borderwidth.
   *
   * @param width
   *        the width of the table
   * @param border
   *        the width of the border in pixels
   * @return a html table header
   */
  public static String getTableHeader (final int width, final int border)
  {
    return ("<table border=\"" +
            border +
            "\" " +
            "width=\"" +
            width +
            "%\" " +
            "bgcolor=\"" +
            config.get ("tablebgcolor") +
            "\">\n");
  }

  /**
   * Return a table topic row
   *
   * @return a html table topic row
   */
  public static String getTableTopicRow ()
  {
    return "<tr bgcolor=\"" + config.get ("tabletopicrow") + "\">";
  }

  /**
   * Setup this class for usage
   *
   * @return some default properties with color codes
   */
  public static SMap setup ()
  {
    config = new SMap ();
    config.put ("bodybgcolor", "WHITE");
    config.put ("bodytext", "BLACK");
    config.put ("bodylink", "BLUE");
    config.put ("bodyalink", "RED");
    config.put ("bodyvlink", "#AA00AA");
    config.put ("tablebgcolor", "#DDDDFF");
    config.put ("tabletopicrow", "#DD6666");
    return config;
  }
}
