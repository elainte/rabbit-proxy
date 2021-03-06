/**
 * Copyright (c) 2010 Robert Olofsson.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the authors nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.helger.rnio.impl;

import com.helger.rnio.IStatisticsHolder;
import com.helger.rnio.ITaskIdentifier;

/**
 * A class that executes one task and gathers information about the time spent
 * and the success status of the task.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class StatisticsCollector implements Runnable
{
  private final IStatisticsHolder stats;
  private final Runnable realTask;
  private final ITaskIdentifier ti;

  /**
   * Create a new StatisticsCollector that will update the given
   * StatisticsHolder about the specific job.
   *
   * @param stats
   *        the StatisticsHolder to update
   * @param realTask
   *        the task to run
   * @param ti
   *        the identifier of the task
   */
  public StatisticsCollector (final IStatisticsHolder stats, final Runnable realTask, final ITaskIdentifier ti)
  {
    this.stats = stats;
    this.realTask = realTask;
    this.ti = ti;
  }

  /**
   * Run the task.
   */
  public void run ()
  {
    stats.changeTaskStatusToRunning (ti);
    final long started = System.currentTimeMillis ();
    boolean wasOk = false;
    try
    {
      realTask.run ();
      wasOk = true;
    }
    finally
    {
      final long ended = System.currentTimeMillis ();
      final long diff = ended - started;
      stats.changeTaskStatusToFinished (ti, wasOk, diff);
    }
  }
}
