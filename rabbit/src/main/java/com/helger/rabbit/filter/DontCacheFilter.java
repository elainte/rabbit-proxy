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
 * This is a class that makes sure the proxy doesnt caches certain pages. It
 * features two types of filtering: either on the URL or the mime type.
 * <p>
 * <font size=+1><b>URL based matching.</b></font><br>
 * It uses the config option <tt>dontCacheURLmatching</tt> with a default value
 * of the empty string. This value is a regex.<br>
 * This method should be used on requests, that is append this class to
 * <tt>httpinfilters</tt> in <tt>rabbit.conf</tt> to use URL matching.
 * </p>
 * <p>
 * <font size=+1><b>Mime type based matching.</b></font><br>
 * It uses the config option <tt>dontCacheMimematching</tt> with a default value
 * of the empty string. This value is a regexp.<br>
 * This method should be used on responses, that is append this class to
 * <tt>httpoutfilters</tt> in <tt>rabbit.conf</tt> to use mime type matching.
 * </p>
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DontCacheFilter implements IHttpFilter
{
  private Pattern dontCacheUrls;
  private Pattern onlyCacheUrls;
  private Pattern dontCacheMime;
  private Pattern onlyCacheMime;

  /**
   * Test if a socket/header combination is valid or return a new HttpHeader. If
   * the request matches a certain criteria dont cache it. This filter is good
   * for pages that done send cache-control headers correctly. This method uses
   * the URL of the request to determine if the resource may be cached.
   *
   * @param socket
   *        the SocketChannel that made the request.
   * @param header
   *        the actual request made.
   * @param con
   *        the Connection handling the request.
   * @return This method always returns null.
   */
  public HttpHeader doHttpInFiltering (final SocketChannel socket, final HttpHeader header, final Connection con)
  {
    final String uri = header.getRequestURI ();
    dontCache (con, dontCacheUrls, uri);
    onlyCache (con, onlyCacheUrls, uri);
    return null;
  }

  /**
   * Test if a socket/header combination is valid or return a new HttpHeader. If
   * the request matches a certain criteria dont cache it. This filter is good
   * for pages that done send cache-control headers correctly. This method uses
   * the mime type of the response to determine if the resource may be cached.
   *
   * @param socket
   *        the SocketChannel that made the request.
   * @param header
   *        the actual request made.
   * @param con
   *        the Connection handling the request.
   * @return null
   */
  public HttpHeader doHttpOutFiltering (final SocketChannel socket, final HttpHeader header, final Connection con)
  {
    final String mimetype = header.getHeader ("content-type");
    if (mimetype == null)
      return null;
    dontCache (con, dontCacheMime, mimetype);
    onlyCache (con, onlyCacheMime, mimetype);
    return null;
  }

  public HttpHeader doConnectFiltering (final SocketChannel socket, final HttpHeader header, final Connection con)
  {
    return null;
  }

  /**
   * If the string toCheck match the given pattern, then do not cache this
   * resource.
   *
   * @param con
   *        the Connection handling the request.
   * @param pat
   *        the pattern to use for finding.
   * @param toCheck
   *        the String to check.
   */
  private void dontCache (final Connection con, final Pattern pat, final String toCheck)
  {
    if (pat == null)
      return;
    final Matcher m = pat.matcher (toCheck);
    if (m.find ())
      con.setMayCache (false);
  }

  /**
   * if the string toCheck match the given pattern then cacheing of this
   * resource is allowed.
   *
   * @param con
   *        the Connection handling the request.
   * @param pat
   *        the pattern we are looking for.
   * @param toCheck
   *        the String to check.
   */
  private void onlyCache (final Connection con, final Pattern pat, final String toCheck)
  {
    if (pat == null)
      return;
    final Matcher m = pat.matcher (toCheck);
    if (m.find ())
      return;
    con.setMayCache (false);
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
    dontCacheUrls = ph.getPattern (properties, "dontCacheURLmatching", "DontCacheFilter bad url match: ");
    onlyCacheUrls = ph.getPattern (properties, "onlyCacheURLmatching", "DontCacheFilter bad url match: ");
    dontCacheMime = ph.getPattern (properties, "dontCacheMimematching", "DontCacheFilter bad mime match: ");
    onlyCacheMime = ph.getPattern (properties, "onlyCacheMimematching", "DontCacheFilter bad mime match: ");
  }
}
