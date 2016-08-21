package com.helger.rabbit.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.helger.commons.url.SMap;
import com.helger.rabbit.http.HttpHeader;
import com.helger.rabbit.meta.MetaHandler;
import com.helger.rabbit.util.TrafficLogger;

class MetaHandlerHandler
{

  /**
   * Handle a meta page.
   *
   * @param con
   *        the Connection serving the request
   * @param header
   *        the request being made.
   * @param tlProxy
   *        the TrafficLogger for proxy traffic
   * @param tlClient
   *        the TrafficLogger for the client traffic
   * @throws IOException
   *         if the actual meta handler fails to handle the request
   */
  public void handleMeta (final Connection con,
                          final HttpHeader header,
                          final TrafficLogger tlProxy,
                          final TrafficLogger tlClient) throws IOException
  {
    con.getCounter ().inc ("Meta pages requested");
    URL url;
    try
    {
      url = new URL (header.getRequestURI ());
    }
    catch (final MalformedURLException e)
    {
      // this should not happen since HTTPBaseHandler managed to do it...
      con.doError (500, "Failed to create url: " + e);
      return;
    }
    String file = url.getFile ().substring (1); // remove initial '/'
    if (file.length () == 0)
      file = "FileSender/";

    int index;
    String args = "";
    if ((index = file.indexOf ("?")) >= 0)
    {
      args = file.substring (index + 1);
      file = file.substring (0, index);
    }
    final SMap htab = splitArgs (args);
    if ((index = file.indexOf ("/")) >= 0)
    {
      final String fc = file.substring (index + 1);
      file = file.substring (0, index);
      htab.put ("argstring", fc);
    }
    String error = null;
    try
    {
      if (file.startsWith ("favicon.ico"))
      {
        con.doError (404, "");
        return;
      }
      if (file.indexOf (".") < 0)
        file = "rabbit.meta." + file;

      final Class <? extends MetaHandler> cls = con.getProxy ().load3rdPartyClass (file, MetaHandler.class);
      MetaHandler mh;
      mh = cls.newInstance ();
      mh.handle (header, htab, con, tlProxy, tlClient);
      con.getCounter ().inc ("Meta pages handled");
      // Now take care of every error...
    }
    catch (final NoSuchMethodError e)
    {
      error = "Given metahandler doesnt have a public no-arg constructor:" + file + ", " + e;
    }
    catch (final ClassCastException e)
    {
      error = "Given metapage is not a MetaHandler:" + file + ", " + e;
    }
    catch (final ClassNotFoundException e)
    {
      error = "Couldnt find class:" + file + ", " + e;
    }
    catch (final InstantiationException e)
    {
      error = "Couldnt instantiate metahandler:" + file + ", " + e;
    }
    catch (final IllegalAccessException e)
    {
      error = "Que? metahandler access violation?:" + file + ", " + e;
    }
    catch (final IllegalArgumentException e)
    {
      error = "Strange name of metapage?:" + file + ", " + e;
    }
    if (error != null)
    {
      Logger.getLogger (getClass ().getName ()).warning (error);
      con.doError (400, error);
    }
  }

  /**
   * Splits the CGI-paramsstring into variables and values. put these values
   * into a hashtable for easy retrival
   *
   * @param params
   *        the CGI-querystring.
   * @return a map with type->value maps for the CGI-querystring
   */
  public SMap splitArgs (final String params)
  {
    final SMap htab = new SMap ();
    final StringTokenizer st = new StringTokenizer (params, "=&", true);
    String key = null;
    while (st.hasMoreTokens ())
    {
      String next = st.nextToken ();
      if (next.equals ("="))
      {
        // nah..
      }
      else
        if (next.equals ("&"))
        {
          if (key != null)
          {
            htab.put (key, "");
            key = null;
          }
        }
        else
          if (key == null)
          {
            key = next;
          }
          else
          {
            try
            {
              next = URLDecoder.decode (next, "UTF-8");
            }
            catch (final UnsupportedEncodingException e)
            {
              final Logger log = Logger.getLogger (getClass ().getName ());
              log.log (Level.WARNING, "Failed to get utf-8", e);
            }
            htab.put (key, next);
            key = null;
          }
    }
    return htab;
  }
}
