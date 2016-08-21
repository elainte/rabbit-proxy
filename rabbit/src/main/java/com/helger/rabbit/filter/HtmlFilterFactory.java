package com.helger.rabbit.filter;

import com.helger.rabbit.http.HttpHeader;
import com.helger.rabbit.proxy.Connection;

/**
 * A factory that creates HTMLFilters.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HtmlFilterFactory
{
  /**
   * Get a new HtmlFilter for the given request, response pair.
   * 
   * @param con
   *        the Connection handling the request.
   * @param request
   *        the actual request made.
   * @param response
   *        the actual response being sent.
   * @return the new filter
   */
  public HtmlFilter newFilter (Connection con, HttpHeader request, HttpHeader response);
}
