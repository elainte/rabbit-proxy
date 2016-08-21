package com.helger.rabbit.filter;

import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.helger.commons.url.SMap;
import com.helger.rabbit.http.HttpHeader;
import com.helger.rabbit.proxy.Connection;
import com.helger.rabbit.proxy.HttpProxy;
import com.helger.rabbit.util.PatternHelper;

/**
 * This is a class that makes sure the proxy doesnt filter certain pages. It
 * matches pages based on the URL.
 * <p>
 * It uses the config option <tt>dontFilterURLmatching</tt> with a default value
 * of the empty string. <br>
 * Matching is done with regular expressions, using find on the url.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DontFilterFilter implements IHttpFilter
{
  private Pattern pattern;
  private Pattern uap;

  /**
   * Test if a socket/header combination is valid or return a new HttpHeader. If
   * the request matches a certain criteria dont filter it. This filter is good
   * for pages with broken HTML that would wreck the HTML parser.
   *
   * @param socket
   *        the SocketChannel that made the request.
   * @param header
   *        the actual request made.
   * @param con
   *        the Connection handling the request.
   * @return This filter always returns null
   */
  public HttpHeader doHttpInFiltering (final SocketChannel socket, final HttpHeader header, final Connection con)
  {
    Matcher m;
    if (pattern != null)
    {
      m = pattern.matcher (header.getRequestURI ());
      if (m.find ())
        con.setFilteringNotAllowed ();
    }

    final String ua = header.getHeader ("User-Agent");
    if (ua != null && uap != null)
    {
      m = uap.matcher (ua);
      if (m.find ())
        con.setFilteringNotAllowed ();
    }
    return null;
  }

  /**
   * test if a socket/header combination is valid or return a new HttpHeader.
   *
   * @param socket
   *        the SocketChannel that made the request.
   * @param header
   *        the actual request made.
   * @param con
   *        the Connection handling the request.
   * @return This filter always returns null
   */
  public HttpHeader doHttpOutFiltering (final SocketChannel socket, final HttpHeader header, final Connection con)
  {
    return null;
  }

  public HttpHeader doConnectFiltering (final SocketChannel socket, final HttpHeader header, final Connection con)
  {
    return null;
  }

  /**
   * Setup this class with the given properties.
   *
   * @param properties
   *        the new configuration of this class.
   */
  public void setup (final SMap properties, final HttpProxy proxy)
  {
    final PatternHelper ph = new PatternHelper ();
    pattern = ph.getPattern (properties, "dontFilterURLmatching", "DontFilterFilter: bad pattern: ");
    uap = ph.getPattern (properties, "dontFilterAgentsMatching", "DontFilterFilter: bad user agent pattern: ");
  }
}
