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
package com.antonjohansson.conventionalcommits.core.common;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.antonjohansson.conventionalcommits.core.utils.TimeUtils;

/**
 * Abstract skeleton for unit tests.
 */
public abstract class AbstractTest extends Assertions
{
    @BeforeEach
    public void setUp()
    {
        ZoneOffset offset = ZoneOffset.UTC;
        Instant instant = LocalDateTime.of(2020, 3, 12, 22, 24).toInstant(offset);
        TimeUtils.setClock(Clock.fixed(instant, offset));
    }

    @AfterEach
    public void tearDown()
    {
        TimeUtils.resetClock();
    }
}
