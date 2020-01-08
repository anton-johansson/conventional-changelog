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
 * Defines a single commit note.
 */
public final class Note
{
    private final String title;
    private final String text;

    public Note(String title, String text)
    {
        this.title = requireNonNull(title);
        this.text = requireNonNull(text);
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(title, text);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Note)
        {
            Note that = (Note) obj;
            return Objects.equals(this.title, that.title)
                && Objects.equals(this.text, that.text);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
