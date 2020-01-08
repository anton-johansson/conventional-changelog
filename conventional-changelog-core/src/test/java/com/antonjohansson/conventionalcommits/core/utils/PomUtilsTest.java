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
import java.nio.file.Paths;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import com.antonjohansson.conventionalcommits.core.common.AbstractTest;

/**
 * Unit tests of {@link PomUtils}.
 */
public class PomUtilsTest extends AbstractTest
{
    private static final File ROOT = new File(".");

    @Test
    public void testNonExistingPoms()
    {
        assertFalse(PomUtils.getPOM(null).isPresent());
        assertFalse(PomUtils.getPOM(new File(ROOT, "pom.xml")).isPresent());
        assertFalse(PomUtils.getPOM(new File(ROOT, "src")).isPresent());
    }

    @Test
    public void testBrokenPom()
    {
        assertThrows(RuntimeException.class, () -> PomUtils.getPOM(ROOT.toPath().resolve(Paths.get("src", "test", "bad-project")).toFile()));
    }

    @Test
    public void testReadingPom()
    {
        Model model = PomUtils.getPOM(ROOT).get();
        assertEquals("com.anton-johansson", model.getParent().getGroupId());
        assertEquals("conventional-changelog", model.getParent().getArtifactId());
        assertEquals("conventional-changelog-core", model.getArtifactId());
        assertEquals("Anton Johansson :: Conventional changelog :: Core", model.getName());
    }
}
