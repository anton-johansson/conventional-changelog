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
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Objects;
import java.util.Optional;

/**
 * Defines a reference to an issue from a commit.
 */
public class Reference
{
    private final String action;
    private final String owner;
    private final String repository;
    private final String prefix;
    private final String issue;
    private final String raw;

    public Reference(String action, String owner, String repository, String prefix, String issue, String raw)
    {
        this.action = requireNonNull(action);
        this.owner = trimToEmpty(owner);
        this.repository = trimToEmpty(repository);
        this.prefix = requireNonNull(prefix);
        this.issue = requireNonNull(issue);
        this.raw = requireNonNull(raw);
    }

    public String getAction()
    {
        return action;
    }

    public Optional<String> getOwner()
    {
        return Optional.ofNullable(owner);
    }

    public Optional<String> getRepository()
    {
        return Optional.ofNullable(repository);
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getIssue()
    {
        return issue;
    }

    public String getRaw()
    {
        return raw;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(action, owner, repository, prefix, issue);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Reference)
        {
            Reference that = (Reference) obj;
            return Objects.equals(this.action, that.action)
                && Objects.equals(this.owner, that.owner)
                && Objects.equals(this.repository, that.repository)
                && Objects.equals(this.prefix, that.prefix)
                && Objects.equals(this.issue, that.issue)
                && Objects.equals(this.raw, that.raw);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
