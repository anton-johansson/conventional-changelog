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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.List;

import com.antonjohansson.conventionalcommits.core.git.model.Reference;

/**
 * Defines a single commit.
 */
public class Commit
{
    private static final int SHORT_HASH_LENGTH = 8;

    private final String hash;
    private final String title;
    private final String scope;
    private final List<Reference> references;

    public Commit(String hash, String title, String scope, List<Reference> references)
    {
        this.hash = requireNonNull(hash);
        this.title = requireNonNull(title);
        this.scope = scope;
        this.references = unmodifiableList(requireNonNull(references));
    }

    public String getHash()
    {
        return hash;
    }

    public String getShortHash()
    {
        return hash.substring(0, SHORT_HASH_LENGTH);
    }

    public String getTitle()
    {
        return title;
    }

    public String getScope()
    {
        return scope;
    }

    public List<Reference> getReferences()
    {
        return references;
    }
}
