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
package com.antonjohansson.conventionalcommits.core.git.parser;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.antonjohansson.conventionalcommits.core.git.model.Change;
import com.antonjohansson.conventionalcommits.core.git.model.Note;
import com.antonjohansson.conventionalcommits.core.git.model.Reference;
import com.antonjohansson.conventionalcommits.core.git.model.Revert;

/**
 * Parses a single commit message and translates it into a {@link Change}.
 */
public class CommitParser
{
    private static final String SCISSOR = "# ------------------------ >8 ------------------------";
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(\\w*)(?:\\((.*)\\))?: (.*)$");
    private static final Pattern REVERT_PATTERN = Pattern.compile("^revert\\s\\\"([\\s\\S]+?)\\\"\\s*This reverts commit (\\w*)\\.", CASE_INSENSITIVE);

    private final String hash;
    private final String raw;
    private Pattern notePattern;
    private Pattern referencePattern;
    private Pattern referencePartsPattern;

    /**
     * Instantiates a new instance of {@link CommitParser}.
     *
     * @param hash the commit hash
     * @param message the commit message
     */
    public CommitParser(String hash, String message)
    {
        this.hash = requireNonNull(hash);
        this.raw = requireNonNull(message);
        noteKeywords("BREAKING CHANGE");
        referenceActions("close", "closes", "closed", "fix", "fixes", "fixed", "resolve", "resolves", "resolved");
        issuePrefixes("#");
    }

    /**
     * Sets keywords that indicate notes. By default only {@code BREAKING CHANGE} is considered.
     *
     * @param noteKeywords the keywords to consider as notes
     * @return the parser itself, used for chaining
     */
    public CommitParser noteKeywords(String... noteKeywords)
    {
        requireNonNull(noteKeywords);
        return noteKeywords(asList(noteKeywords));
    }

    /**
     * Sets keywords that indicate notes. By default only "BREAKING CHANGE" is considered.
     *
     * @param noteKeywords the keywords to consider as notes
     * @return the parser itself, used for chaining
     */
    public CommitParser noteKeywords(List<String> noteKeywords)
    {
        String pattern = requireNonNull(noteKeywords).stream().collect(joining("|"));
        notePattern = Pattern.compile("^[\\s|*]*(" + pattern + ")[:\\s]+(.*)", CASE_INSENSITIVE);
        return this;
    }

    /**
     * Sets actions that indicate references to issues. The default actions is as follows:
     * <ul>
     * <li>close</li>
     * <li>closes</li>
     * <li>closed</li>
     * <li>fix</li>
     * <li>fixes</li>
     * <li>fixed</li>
     * <li>resolve</li>
     * <li>resolves</li>
     * <li>resolved</li>
     * </ul>
     *
     * @param referenceActions the actions to set
     * @return the parser itself, used for chaining
     */
    public CommitParser referenceActions(String... referenceActions)
    {
        requireNonNull(referenceActions);
        return referenceActions(asList(referenceActions));
    }

    /**
     * Sets actions that indicate references to issues. The default actions is as follows:
     * <ul>
     * <li>close</li>
     * <li>closes</li>
     * <li>closed</li>
     * <li>fix</li>
     * <li>fixes</li>
     * <li>fixed</li>
     * <li>resolve</li>
     * <li>resolves</li>
     * <li>resolved</li>
     * </ul>
     *
     * @param referenceActions the actions to set
     * @return the parser itself, used for chaining
     */
    public CommitParser referenceActions(List<String> referenceActions)
    {
        String pattern = requireNonNull(referenceActions).stream().collect(joining("|"));
        referencePattern = Pattern.compile("(" + pattern + ")(?:\\s+(.*?))(?=(?:" + pattern + ")|$)", CASE_INSENSITIVE);
        return this;
    }

    /**
     * The issue prefixes to look for in combination with reference actions. By default, only {@code #} is considered.
     *
     * @param issuePrefixes the issue prefixes to look for
     * @return the parser itself, used for chaining
     */
    public CommitParser issuePrefixes(String... issuePrefixes)
    {
        requireNonNull(issuePrefixes);
        return issuePrefixes(asList(issuePrefixes));
    }

    /**
     * The issue prefixes to look for in combination with reference actions. By default, only {@code #} is considered.
     *
     * @param issuePrefixes the issue prefixes to look for
     * @return the parser itself, used for chaining
     */
    public CommitParser issuePrefixes(List<String> issuePrefixes)
    {
        String pattern = requireNonNull(issuePrefixes).stream().collect(joining("|"));
        referencePartsPattern = Pattern.compile("(?:.*?)??\\s*([\\w-\\.\\/]*?)??(" + pattern + ")([\\w-]*\\d+)");
        return this;
    }

    /**
     * Parses the message and translates it into a {@link Change}.
     *
     * @return the parsed {@link Change}
     */
    public Change parse()
    {
        List<String> lines = getMessageLines(raw);

        String header = lines.remove(0);
        String type = null;
        String scope = null;
        String title = null;
        Matcher headerMatcher = HEADER_PATTERN.matcher(header);
        if (headerMatcher.find())
        {
            type = headerMatcher.group(1);
            scope = headerMatcher.group(2);
            title = headerMatcher.group(3);
        }

        boolean isBody = true;
        String body = "";
        String footer = "";
        List<Note> notes = new ArrayList<>();
        List<Reference> references = new ArrayList<>();

        for (String line : lines)
        {
            Matcher noteMatcher = notePattern.matcher(line);
            if (noteMatcher.find())
            {
                String noteTitle = noteMatcher.group(1);
                String noteText = noteMatcher.group(2);
                Note note = new Note(noteTitle, noteText);
                notes.add(note);
                footer = footer.concat(line).concat("\n");
                isBody = false;
                continue;
            }

            List<Reference> lineReferences = getReferences(line);
            if (!lineReferences.isEmpty())
            {
                references.addAll(lineReferences);
                footer = footer.concat(line).concat("\n");
                isBody = false;
                continue;
            }

            if (isBody)
            {
                body = body.concat(line).concat("\n");
            }
            else
            {
                footer = footer.concat(line).concat("\n");
            }
        }

        Revert revert = null;
        Matcher revertMatcher = REVERT_PATTERN.matcher(raw);
        if (revertMatcher.find())
        {
            String revertHeader = revertMatcher.group(1);
            String revertHash = revertMatcher.group(2);
            revert = new Revert(revertHeader, revertHash);
        }

        return new Change(hash, type, scope, title, body, footer, notes, references, revert);
    }

    private List<String> getMessageLines(String message)
    {
        List<String> lines = new ArrayList<>(asList(message.split("\\r?\\n")));
        int index = lines.indexOf(SCISSOR);
        if (index >= 0)
        {
            lines = new ArrayList<>(lines.subList(0, index));
        }
        while (isBlank(lines.get(lines.size() - 1)))
        {
            lines.remove(lines.size() - 1);
        }
        return lines;
    }

    private List<Reference> getReferences(String line)
    {
        List<Reference> references = new ArrayList<>();

        Matcher referenceMatcher = referencePattern.matcher(line);
        while (referenceMatcher.find())
        {
            String action = referenceMatcher.group(1).toLowerCase();
            String sentence = referenceMatcher.group(2);

            Matcher partsMatcher = referencePartsPattern.matcher(sentence);
            while (partsMatcher.find())
            {
                String repository = partsMatcher.group(1);
                String prefix = partsMatcher.group(2);
                String issue = partsMatcher.group(3);
                String owner = null;
                int index = repository == null ? -1 : repository.indexOf('/');
                if (index >= 0)
                {
                    owner = repository.substring(0, index);
                    repository = repository.substring(index + 1);
                }
                StringBuilder raw = new StringBuilder()
                        .append(prefix)
                        .append(issue);
                if (!isBlank(repository))
                {
                    raw.insert(0, repository);
                }
                if (!isBlank(owner))
                {
                    raw.insert(0, "/").insert(0, owner);
                }

                Reference reference = new Reference(action, owner, repository, prefix, issue, raw.toString());
                references.add(reference);
            }
        }
        return references;
    }
}
