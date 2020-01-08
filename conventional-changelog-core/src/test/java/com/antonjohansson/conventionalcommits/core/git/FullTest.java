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

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.io.FileUtils.readFileToString;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.antonjohansson.conventionalcommits.core.common.AbstractTest;
import com.antonjohansson.conventionalcommits.core.git.model.ChangeSet;
import com.antonjohansson.conventionalcommits.core.writer.ChangeLogWriter;

/**
 * Integration tests of the entire flow.
 */
public class FullTest extends AbstractTest
{
    @Test
    public void testParseAndWrite() throws Exception
    {
        File projectDirectory = prepareProject("full");
        List<ChangeSet> changeSets = new ChangeSetCollector(projectDirectory).collect();

        try (ChangeLogWriter writer = new ChangeLogWriter(changeSets))
        {
            StringWriter w = new StringWriter();
            writer
                    .projectDirectory(projectDirectory)
                    .writer(w)
                    .write();

            String actual = w.toString();
            String expected = expectedChangelog("full");

            assertEquals(expected, actual);
        }
    }

    private String expectedChangelog(String name) throws Exception
    {
        File changelogFile = new File("src/test/expected-changelogs/" + name + ".md").getAbsoluteFile();
        return readFileToString(changelogFile, "UTF-8");
    }

    private File prepareProject(String name) throws Exception
    {
        File source = new File("src/test/it-projects/" + name).getAbsoluteFile();
        File destination = new File("target/it/projects/" + name).getAbsoluteFile();
        deleteDirectory(destination);
        copyDirectory(source, destination);

        File gitTemplate = new File(destination, "git").getAbsoluteFile();
        File gitReal = new File(destination, ".git").getAbsoluteFile();
        moveDirectory(gitTemplate, gitReal);

        return destination;
    }
}
