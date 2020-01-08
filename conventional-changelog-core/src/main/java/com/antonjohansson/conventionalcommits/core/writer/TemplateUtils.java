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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Provides utilities for {@link Handlebars} templates.
 */
class TemplateUtils
{
    static TemplateSource compileFromResource(String resourceName)
    {
        String fileName = "/templates/" + resourceName;
        try (InputStream stream = TemplateUtils.class.getResourceAsStream(fileName))
        {
            String content = IOUtils.toString(stream, "UTF-8");
            return new StringTemplateSource(fileName, content);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load template: " + fileName, e);
        }
    }
}
