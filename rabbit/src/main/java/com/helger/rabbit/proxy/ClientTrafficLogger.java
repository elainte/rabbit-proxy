package com.helger.rabbit.proxy;

import com.helger.commons.url.SMap;
import com.helger.rabbit.http.HttpHeader;
import com.helger.rabbit.util.ITrafficLogger;

/**
 * A logger that gets notified about client traffic usage.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ClientTrafficLogger
{
  /**
   * Log the traffic usage for the given user and request.
   *
   * @param user
   *        the authenticated user, may be null if user is not authenticated
   * @param request
   *        the actual http request header
   * @param client
   *        the traffic between the client and the proxy
   * @param network
   *        the traffic between the proxy and the network
   * @param cache
   *        the traffic between the proxy and the cache
   * @param proxy
   *        the traffic generated by the proxy
   */
  void logTraffic (String user,
                   HttpHeader request,
                   ITrafficLogger client,
                   ITrafficLogger network,
                   ITrafficLogger cache,
                   ITrafficLogger proxy);

  /**
   * Setup this logger
   *
   * @param properties
   *        the SProperties to get the settings from.
   * @param proxy
   *        the HttpProxy that is using this logger
   */
  void setup (SMap properties, HttpProxy proxy);
}
