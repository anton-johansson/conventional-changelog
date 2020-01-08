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
package com.antonjohansson.conventionalcommits.core.git;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import com.antonjohansson.conventionalcommits.core.git.model.Change;
import com.antonjohansson.conventionalcommits.core.git.model.ChangeSet;
import com.antonjohansson.conventionalcommits.core.git.parser.CommitParser;
import com.antonjohansson.conventionalcommits.core.utils.PomUtils;
import com.antonjohansson.conventionalcommits.core.utils.TimeUtils;

/**
 * Collects {@link ChangeSet change sets} from a Git repository by parsing conventional commits.
 */
public class ChangeSetCollector
{
    private final Git git;
    private final File projectDirectory;
    private int numberOfVersions;
    private String nextVersion;
    private String versionTagPrefix = "v";
    private Pattern versionTagPattern;

    /**
     * Instantiates a new instance of {@link ChangeSetCollector}.
     *
     * @param projectDirectory the project directory to collect change sets from
     */
    public ChangeSetCollector(File projectDirectory) throws IOException
    {
        this.git = Git.open(requireNonNull(projectDirectory));
        this.projectDirectory = projectDirectory;
    }

    public ChangeSetCollector(Git git)
    {
        this.git = requireNonNull(git);
        this.projectDirectory = git.getRepository().getDirectory();
    }

    /**
     * <p>
     * Sets the version number currently being worked on. This is useful if you are generating change logs for a next version and the tag isn't
     * created yet and the POM is not prepared for the next version.
     * <p>
     * If not specified, this value will be extracted from the POM if possible.
     * </p>
     *
     * @param nextVersion the next version
     * @return the collector itself, used for chaining
     */
    public ChangeSetCollector nextVersion(String nextVersion)
    {
        this.nextVersion = requireNonNull(nextVersion);
        return this;
    }

    /**
     * Sets the number of versions to collect. A value of {@code 0} indicates that the entire change set will be generated. Defaults to {@code 1}.
     *
     * @param numberOfVersions the number of versions to collect
     * @return the collector itself, used for chaining
     */
    public ChangeSetCollector numberOfVersions(int numberOfVersions)
    {
        this.numberOfVersions = numberOfVersions;
        return this;
    }

    /**
     * Sets the prefix of version tags. The default prefix is {@code v}.
     * 
     * @param prefix the prefix
     * @return the collector itself, used for chaining
     */
    public ChangeSetCollector versionTagPrefix(String prefix)
    {
        versionTagPrefix = prefix;
        return this;
    }

    /**
     * Collects the set of changes based on the set configuration.
     *
     * @return an unmodifiable set of changes, in reverse chronological order
     */
    public List<ChangeSet> collect() throws Exception
    {
        Iterator<RevCommit> iterator = git.log().call().iterator();
        versionTagPattern = Pattern.compile("^" + versionTagPrefix + "(\\d.*)$");

        String nextVersion = this.nextVersion;
        if (isBlank(nextVersion))
        {
            nextVersion = PomUtils.getPOM(projectDirectory)
                    .map(Model::getVersion)
                    .orElse("");
        }

        if (isBlank(nextVersion))
        {
            throw new RuntimeException("Next version is not set and it could not be determined from the POM");
        }

        VersionTag versionTag = new VersionTag();
        versionTag.version = nextVersion;
        versionTag.tagName = versionTagPrefix + versionTag.version;
        LocalDate createdAt = TimeUtils.now();

        List<ChangeSet> changeSets = new ArrayList<>();
        List<Change> changes = new ArrayList<>();

        int index = 0;
        while (index++ < numberOfVersions || numberOfVersions == 0)
        {
            if (!iterator.hasNext())
            {
                break;
            }

            while (iterator.hasNext())
            {
                RevCommit commit = iterator.next();
                String hash = commit.getName();
                String message = commit.getFullMessage();
                LocalDate commitCreatedAt = commit.getAuthorIdent().getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                Set<Ref> refs = git.getRepository().getRefDatabase().getTipsWithSha1(commit);
                Optional<VersionTag> previousVersionTagMaybe = refs.stream()
                        .map(this::getVersionTag)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findAny();

                if (previousVersionTagMaybe.isPresent())
                {
                    VersionTag previousVersionTag = previousVersionTagMaybe.get();
                    Collections.reverse(changes);
                    ChangeSet changeSet = new ChangeSet(versionTag.version, versionTag.tagName, previousVersionTag.tagName, createdAt, changes);
                    changeSets.add(changeSet);
                    versionTag = previousVersionTag;
                    createdAt = commitCreatedAt;
                    changes = new ArrayList<>();
                }

                Change change = new CommitParser(hash, message).parse();
                changes.add(change);

                if (previousVersionTagMaybe.isPresent())
                {
                    break;
                }
            }
        }

        return unmodifiableList(changeSets);
    }

    private Optional<VersionTag> getVersionTag(Ref ref)
    {
        if (!ref.getName().startsWith(R_TAGS))
        {
            return Optional.empty();
        }

        String tagName = ref.getName().substring(R_TAGS.length());
        Matcher matcher = versionTagPattern.matcher(tagName);
        if (!matcher.matches())
        {
            return Optional.empty();
        }

        VersionTag version = new VersionTag();
        version.tagName = tagName;
        version.version = matcher.group(1);
        return Optional.of(version);
    }

    /**
     * Defines a version tag.
     */
    private static class VersionTag
    {
        String tagName;
        String version;
    }
}
