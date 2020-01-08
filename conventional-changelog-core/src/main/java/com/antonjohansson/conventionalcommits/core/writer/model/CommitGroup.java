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

/**
 * Defines a group of commits that have the same type.
 */
public class CommitGroup
{
    private final String title;
    private final List<Commit> commits;

    public CommitGroup(String title, List<Commit> commits)
    {
        this.title = requireNonNull(title);
        this.commits = unmodifiableList(requireNonNull(commits));
    }

    public String getTitle()
    {
        return title;
    }

    public List<Commit> getCommits()
    {
        return commits;
    }
}
