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
package com.antonjohansson.conventionalchangelog.maven;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.antonjohansson.conventionalcommits.core.git.ChangeSetCollector;
import com.antonjohansson.conventionalcommits.core.git.model.ChangeSet;
import com.antonjohansson.conventionalcommits.core.writer.ChangeLogWriter;

/**
 * Maven goal for generating a changelog file from conventional commits.
 */
@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(name = "numberOfVersions", property = "numberOfVersions", defaultValue = "1", required = true)
    private int numberOfVersions;

    @Parameter(name = "versionTagPrefix", property = "versionTagPrefix", defaultValue = "v", required = true)
    private String versionTagPrefix;

    @Parameter(name = "fileName", property = "fileName", defaultValue = "CHANGELOG.md", required = true)
    private String fileName;

    @Override
    public void execute() throws MojoExecutionException
    {
        List<ChangeSet> changeSets = getChangeSets();
        try (ChangeLogWriter writer = new ChangeLogWriter(changeSets))
        {
            writer
                    .projectDirectory(project.getBasedir())
                    .fileName(fileName)
                    .write();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Could not write change log", e);
        }
    }

    private List<ChangeSet> getChangeSets() throws MojoExecutionException
    {
        try
        {
            return new ChangeSetCollector(project.getBasedir())
                    .numberOfVersions(numberOfVersions)
                    .versionTagPrefix(versionTagPrefix)
                    .collect();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Could not collect change sets", e);
        }
    }
}
