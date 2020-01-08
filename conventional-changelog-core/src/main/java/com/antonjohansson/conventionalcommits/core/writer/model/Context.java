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

import java.time.LocalDate;
import java.util.List;

/**
 * Defines the templating context model.
 */
public class Context
{
    private final String repositoryURL;
    private final String version;
    private final String tagName;
    private final String previousTagName;
    private final LocalDate createdAt;
    private final List<CommitGroup> commitGroups;
    private final List<NoteGroup> noteGroups;

    public Context(String repositoryURL, String version, String tagName, String previousTagName, LocalDate createdAt, List<CommitGroup> commitGroups, List<NoteGroup> noteGroups)
    {
        this.repositoryURL = repositoryURL;
        this.tagName = requireNonNull(tagName);
        this.previousTagName = requireNonNull(previousTagName);
        this.createdAt = requireNonNull(createdAt);
        this.version = requireNonNull(version);
        this.commitGroups = unmodifiableList(requireNonNull(commitGroups));
        this.noteGroups = unmodifiableList(requireNonNull(noteGroups));
    }

    public String getRepositoryURL()
    {
        return repositoryURL;
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

    public List<CommitGroup> getCommitGroups()
    {
        return commitGroups;
    }

    public List<NoteGroup> getNoteGroups()
    {
        return noteGroups;
    }
}
