/**
 * Copyright (c) Anton Johansson <hello@anton-johansson.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.antonjohansson.conventionalcommits.core.utils;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Provides utilities for managing time.
 */
public final class TimeUtils
{
    private static Clock clock = Clock.systemDefaultZone();

    // Prevent instantiation
    private TimeUtils()
    {
    }

    /**
     * Sets the clock to a specific one.
     *
     * @param clock the clock to use
     */
    public static void setClock(Clock clock)
    {
        TimeUtils.clock = clock;
    }

    /**
     * Resets the clock back to the system default.
     */
    public static void resetClock()
    {
        clock = Clock.systemDefaultZone();
    }

    /**
     * Gets the current date.
     *
     * @return the current date
     */
    public static LocalDate now()
    {
        return LocalDate.now(clock);
    }
}
