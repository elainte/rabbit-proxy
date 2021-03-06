package com.helger.rabbit.filter;

import com.helger.rabbit.html.HtmlBlock;
import com.helger.rabbit.http.HttpHeader;
import com.helger.rabbit.proxy.Connection;

/**
 * This class describes the functions neccessary to filter a block of html.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class AbstractHtmlFilter implements IHtmlFilterFactory
{
  /** The Connection handling the request. */
  protected Connection con;
  /** The actual request made. */
  protected HttpHeader request;
  /** The actual response. */
  protected HttpHeader response;

  /**
   * Create the factory.
   */
  public AbstractHtmlFilter ()
  {
    // empty
  }

  /**
   * Create a new HtmlFilter for the given request, response pair.
   * 
   * @param con
   *        the Connection handling the request
   * @param request
   *        the actual request made.
   * @param response
   *        the actual response being sent.
   */
  public AbstractHtmlFilter (final Connection con, final HttpHeader request, final HttpHeader response)
  {
    this.con = con;
    this.request = request;
    this.response = response;
  }

  /**
   * Filter a block of html.
   * 
   * @param block
   *        the part of the html page to filter.
   */
  public abstract void filterHtml (HtmlBlock block);

  /**
   * If the filter determines that the page is not cacheable it can negate this,
   * and FilterHandler will not cache it. This method returns true, override in
   * sub classes when needed.
   * 
   * @return true
   */
  public boolean isCacheable ()
  {
    return true;
  }
}
