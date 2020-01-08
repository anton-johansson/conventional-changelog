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
package com.antonjohansson.conventionalcommits.core.writer.model;

import static com.github.jknack.handlebars.internal.lang3.builder.ToStringBuilder.reflectionToString;
import static com.github.jknack.handlebars.internal.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static java.util.Objects.requireNonNull;

/**
 * Defines a single commit note.
 */
public class Note
{
    private final String text;
    private final Commit commit;
    private final String title;

    public Note(String title, String text, Commit commit)
    {
        this.title = requireNonNull(title);
        this.text = requireNonNull(text);
        this.commit = requireNonNull(commit);
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }

    public Commit getCommit()
    {
        return commit;
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
