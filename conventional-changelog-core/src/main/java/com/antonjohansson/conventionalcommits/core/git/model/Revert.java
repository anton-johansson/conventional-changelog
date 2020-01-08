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
package com.antonjohansson.conventionalcommits.core.git.model;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Objects;

/**
 * Defines a revert.
 */
public class Revert
{
    private final String header;
    private final String hash;

    public Revert(String header, String hash)
    {
        this.header = requireNonNull(header);
        this.hash = requireNonNull(hash);
    }

    public String getHeader()
    {
        return header;
    }

    public String getHash()
    {
        return hash;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hash);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Revert)
        {
            Revert that = (Revert) obj;
            return Objects.equals(this.header, that.header)
                && Objects.equals(this.hash, that.hash);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
