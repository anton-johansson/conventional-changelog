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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Defines a single change (commit).
 */
public final class Change
{
    private final String hash;
    private final String type;
    private final String scope;
    private final String title;
    private final String body;
    private final String footer;
    private final List<Note> notes;
    private final List<Reference> references;
    private final Revert revert;

    public Change(String hash, String type, String scope, String title, String body, String footer, List<Note> notes, List<Reference> references, Revert revert)
    {
        this.hash = requireNonNull(hash);
        this.type = type; // requireNonNull(kind);
        this.scope = scope;
        this.title = title; // requireNonNull(title);
        this.body = trimToNull(body);
        this.footer = trimToNull(footer);
        this.notes = unmodifiableList(defaultIfNull(notes, emptyList()));
        this.references = unmodifiableList(defaultIfNull(references, emptyList()));
        this.revert = revert;
    }

    public String getHash()
    {
        return hash;
    }

    public String getType()
    {
        return type;
    }

    public Optional<String> getScope()
    {
        return Optional.ofNullable(scope);
    }

    public String getTitle()
    {
        return title;
    }

    public Optional<String> getBody()
    {
        return Optional.ofNullable(body);
    }

    public Optional<String> getFooter()
    {
        return Optional.ofNullable(footer);
    }

    public List<Note> getNotes()
    {
        return notes;
    }

    public List<Reference> getReferences()
    {
        return references;
    }

    public Optional<Revert> getRevert()
    {
        return Optional.ofNullable(revert);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hash);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Change)
        {
            Change that = (Change) obj;
            return Objects.equals(this.hash, that.hash)
                && Objects.equals(this.type, that.type)
                && Objects.equals(this.scope, that.scope)
                && Objects.equals(this.title, that.title)
                && Objects.equals(this.body, that.body)
                && Objects.equals(this.footer, that.footer)
                && Objects.equals(this.notes, that.notes)
                && Objects.equals(this.references, that.references)
                && Objects.equals(this.revert, that.revert);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
