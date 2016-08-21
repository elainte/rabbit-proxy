package com.helger.rabbit.io;

import java.util.logging.Logger;

import com.helger.commons.url.SMap;
import com.helger.rabbit.dns.IDNSHandler;
import com.helger.rnio.INioHandler;

/**
 * A constructor of ProxyChain:s.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ProxyChainFactory
{
  /**
   * Create a ProxyChain given the properties.
   *
   * @param props
   *        the properties to use when constructing the proxy chain
   * @param nio
   *        the NioHandler to use for network and background tasks
   * @param dnsHandler
   *        the DNSHandler to use for normal DNS lookups
   * @param logger
   *        the Logger to log errors to
   * @return the new ProxyChain
   */
  IProxyChain getProxyChain (SMap props, INioHandler nio, IDNSHandler dnsHandler, Logger logger);
}
