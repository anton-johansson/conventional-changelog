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
package com.antonjohansson.conventionalcommits.core.utils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

/**
 * Provides utility for managing the {@code pom.xml} file (project object model).
 */
public final class PomUtils
{
    // Prevent instantiation
    private PomUtils()
    {
    }

    /**
     * Extracts the actual model from a given project directory.
     *
     * @param projectDirectory the project directory to get the model for
     * @return the {@link Model} if one could be found
     */
    public static Optional<Model> getPOM(File projectDirectory)
    {
        if (projectDirectory == null || !projectDirectory.isDirectory())
        {
            return Optional.empty();
        }

        File pomFile = new File(projectDirectory, "pom.xml");
        if (!pomFile.exists())
        {
            return Optional.empty();
        }

        try (Reader reader = new FileReader(pomFile))
        {
            Model model = new MavenXpp3Reader().read(reader);
            return Optional.of(model);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
