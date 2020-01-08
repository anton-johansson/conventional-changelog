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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.time.LocalDate;
import java.util.List;

/**
 * Defines a set of changes (A.K.A. a release or version).
 */
public final class ChangeSet
{
    private final String version;
    private final String tagName;
    private final String previousTagName;
    private final LocalDate createdAt;
    private final List<Change> changes;

    public ChangeSet(String version, String tagName, String previousTagName, LocalDate createdAt, List<Change> changes)
    {
        this.version = requireNonNull(version);
        this.tagName = requireNonNull(tagName);
        this.previousTagName = requireNonNull(previousTagName);
        this.createdAt = requireNonNull(createdAt);
        this.changes = unmodifiableList(requireNonNull(changes));
    }

    public String getVersion()
    {
        return version;
    }

    public String getTagName()
    {
        return tagName;
    }

    public String getPreviousTagName()
    {
        return previousTagName;
    }

    public LocalDate getCreatedAt()
    {
        return createdAt;
    }

    public List<Change> getChanges()
    {
        return changes;
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
