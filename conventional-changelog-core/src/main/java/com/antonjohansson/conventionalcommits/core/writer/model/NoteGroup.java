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
 * Defines a group of notes that have the same type.
 */
public class NoteGroup
{
    private final String title;
    private final List<Note> notes;

    public NoteGroup(String title, List<Note> notes)
    {
        this.title = requireNonNull(title);
        this.notes = unmodifiableList(requireNonNull(notes));
    }

    public String getTitle()
    {
        return title;
    }

    public List<Note> getNotes()
    {
        return notes;
    }
}
