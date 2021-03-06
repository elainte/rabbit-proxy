package com.helger.rabbit.zip;

import static com.helger.rabbit.zip.GZipFlags.FCOMMENT;
import static com.helger.rabbit.zip.GZipFlags.FEXTRA;
import static com.helger.rabbit.zip.GZipFlags.FHCRC;
import static com.helger.rabbit.zip.GZipFlags.FNAME;

/**
 * GZipState for reading the gzip flags
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FlagReader implements GZipUnpackState
{
  private final GZipUnpackListener listener;
  private int pos = 0;
  private byte flag = -1;
  private final byte [] flags = new byte [6];

  public FlagReader (final GZipUnpackListener listener)
  {
    this.listener = listener;
  }

  public void handleCurrentData (final GZipUnpacker unpacker)
  {
    throw new IllegalStateException ("need more input");
  }

  public boolean needsInput ()
  {
    return true;
  }

  public void handleBuffer (final GZipUnpacker unpacker, final byte [] buf, int off, int len)
  {
    if (len <= 0)
      return;

    if (flag == -1)
    {
      flag = buf[off++];
      len--;
    }
    while (len > 0 && pos < flags.length)
    {
      flags[pos++] = buf[off++];
      len--;
    }
    if (pos < flags.length)
      return;

    /*
     * at the moment we do not care about mtime or xfl or os, but if we did we
     * would find the data as
     */
    /*
     * int mtime = (flags[3] << 24) | (flags[2] << 16) | (flags[1] << 8) |
     * flags[0]; byte xfl = flags[4]; byte os = flags[5];
     */

    if ((flag & FEXTRA) == FEXTRA)
      useNewState (unpacker, new FExtraReader (listener, flag), buf, off, len);

    if ((flag & FNAME) == FNAME)
      useNewState (unpacker, new NameReader (listener, flag), buf, off, len);

    if ((flag & FCOMMENT) == FCOMMENT)
      useNewState (unpacker, new CommentReader (listener, flag), buf, off, len);

    if ((flag & FHCRC) == FHCRC)
      useNewState (unpacker, new HCRCReader (listener, flag), buf, off, len);

    useNewState (unpacker, new UnCompressor (listener, true), buf, off, len);
  }

  private void useNewState (final GZipUnpacker unpacker,
                            final GZipUnpackState state,
                            final byte [] buf,
                            final int off,
                            final int len)
  {
    unpacker.setState (state);
    state.handleBuffer (unpacker, buf, off, len);
  }
}
