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
package com.antonjohansson.conventionalcommits.core.writer;

import static com.antonjohansson.conventionalcommits.core.writer.TemplateUtils.compileFromResource;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;

import com.antonjohansson.conventionalcommits.core.git.model.Change;
import com.antonjohansson.conventionalcommits.core.git.model.ChangeSet;
import com.antonjohansson.conventionalcommits.core.utils.PomUtils;
import com.antonjohansson.conventionalcommits.core.writer.model.Commit;
import com.antonjohansson.conventionalcommits.core.writer.model.CommitGroup;
import com.antonjohansson.conventionalcommits.core.writer.model.Context;
import com.antonjohansson.conventionalcommits.core.writer.model.Note;
import com.antonjohansson.conventionalcommits.core.writer.model.NoteGroup;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Formats and writes {@link ChangeSet change set}.
 */
public class ChangeLogWriter implements AutoCloseable
{
    private final List<ChangeSet> changeSets;
    private final TemplateSource mainTemplate = compileFromResource("main.hbs");
    private final TemplateSource headerTemplate = compileFromResource("header.hbs");
    private final TemplateSource commitTemplate = compileFromResource("commit.hbs");
    private final TemplateSource footerTemplate = compileFromResource("footer.hbs");
    private File projectDirectory;
    private String repositoryURL;
    private File outputFile;
    private Writer writer;

    /**
     * Instantiates a new instance of {@link ChangeLogWriter}.
     *
     * @param changeSets the change sets to write
     */
    public ChangeLogWriter(List<ChangeSet> changeSets)
    {
        this.changeSets = unmodifiableList(defaultIfNull(changeSets, emptyList()));
    }

    /**
     * Sets the project directory. Used to find meta data from the POM and to locate the output file.
     *
     * @param projectDirectory the project directory to use
     * @return the writer itself, used for chaining
     */
    public ChangeLogWriter projectDirectory(File projectDirectory)
    {
        this.projectDirectory = requireNonNull(projectDirectory);
        return this;
    }

    /**
     * Sets the file name of the output file.
     *
     * @param fileName the name of the output file
     * @return the writer itself, used for chaining
     */
    public ChangeLogWriter fileName(String fileName)
    {
        requireNonNull(fileName);
        if (projectDirectory != null)
        {
            outputFile = new File(projectDirectory, fileName);
        }
        else
        {
            outputFile = new File(fileName);
            if (!outputFile.isAbsolute())
            {
                throw new IllegalStateException("If no project directory is specifeid, the fileName must be absolute");
            }
        }
        return this;
    }

    /**
     * Sets the output writer.
     *
     * @param writer the writer to use
     * @return the writer itself, used for chaining
     */
    public ChangeLogWriter writer(Writer writer)
    {
        if (outputFile != null)
        {
            throw new RuntimeException("Cannot use 'writer' and 'fileName'");
        }
        this.writer = requireNonNull(writer);
        return this;
    }

    private void extractProjectMetaData()
    {
        repositoryURL = PomUtils.getPOM(projectDirectory)
                .map(Model::getScm)
                .map(Scm::getUrl)
                .orElse(null);
    }

    /**
     * Writes the change log.
     */
    public void write() throws IOException
    {
        extractProjectMetaData();
        initialieWriter();
        for (ChangeSet set : changeSets)
        {
            write(set);
        }
    }

    private void initialieWriter()
    {
        if (writer != null)
        {
            return;
        }

        if (outputFile != null)
        {
            try
            {
                writer = new FileWriter(outputFile);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not create output file writer", e);
            }
        }
        else
        {
            writer = new PrintWriter(System.out);
        }
    }

    private void write(ChangeSet set) throws IOException
    {
        Context context = toContext(set);

        CustomTemplateLoader loader = new CustomTemplateLoader();
        loader.registerPartial("header", headerTemplate);
        loader.registerPartial("commit", commitTemplate);
        loader.registerPartial("footer", footerTemplate);

        Template template = new Handlebars(loader)
                .prettyPrint(true)
                .compile(mainTemplate);

        template.apply(context, writer);
    }

    private Context toContext(ChangeSet set)
    {
        String version = set.getVersion();
        String tagName = set.getTagName();
        String previousTagName = set.getPreviousTagName();
        LocalDate createdAt = set.getCreatedAt();
        List<CommitGroup> commitGroups = set.getChanges()
                .stream()
                .filter(change -> change.getType() != null)
                .collect(groupingBy(Change::getType))
                .entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(translateGroupTitle(entry.getKey()), entry.getValue()))
                .filter(entry -> entry.getKey() != null)
                .map(entry -> new CommitGroup(entry.getKey(), entry.getValue()
                        .stream()
                        .map(change -> toCommit(change))
                        .collect(toList())))
                .collect(toList());
        List<NoteGroup> noteGroups = set.getChanges()
                .stream()
                .flatMap(change -> change.getNotes()
                        .stream()
                        .map(note -> new Note(note.getTitle(), note.getText(), toCommit(change))))
                .collect(groupingBy(Note::getTitle))
                .entrySet()
                .stream()
                .map(entry -> new NoteGroup(entry.getKey(), entry.getValue()))
                .collect(toList());

        return new Context(repositoryURL, version, tagName, previousTagName, createdAt, commitGroups, noteGroups);
    }

    private Commit toCommit(Change change)
    {
        return new Commit(change.getHash(), change.getTitle(), change.getScope().orElse(null), change.getReferences());
    }

    private String translateGroupTitle(String type)
    {
        // TODO: do we need support for more types, such as docs, style, refactor, test, build, or ci? maybe configurable?

        switch (type)
        {
            case "feat":
                return "Features";
            case "fix":
                return "Bug fixes";
            case "perf":
                return "Performance improvements";
            case "revert":
                return "Reverts";
            default:
                return null;
        }
    }

    @Override
    public void close() throws Exception
    {
        closeWriter();
    }

    private void closeWriter() throws IOException
    {
        if (writer != null)
        {
            writer.flush();
            writer.close();
        }
    }
}
