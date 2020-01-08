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
import static java.util.Collections.emptyList;

import org.junit.jupiter.api.Test;

import com.antonjohansson.conventionalcommits.core.common.AbstractTest;
import com.antonjohansson.conventionalcommits.core.git.model.Change;
import com.antonjohansson.conventionalcommits.core.git.model.Note;
import com.antonjohansson.conventionalcommits.core.git.model.Reference;
import com.antonjohansson.conventionalcommits.core.git.model.Revert;

/**
 * Unit tests of {@link CommitParser}.
 */
public class CommitParserTest extends AbstractTest
{
    @Test
    public void testBigMessage()
    {
        String message = "feat(ngMessages): provide support for dynamic message resolution\r\n" +
            "\r\n" +
            "Prior to this fix it was impossible to apply a binding to a the ngMessage directive to represent the name of the error.\r\n" +
            "\r\n" +
            "BREAKING CHaNGE: The `ngMessagesInclude` attribute is now its own directive and that must be placed as a **child** element within the element with the ngMessages directive.\r\n" +
            "\r\n" +
            "Closes #10036, #13\r\n" +
            "Closes #9338\r\n" +
            "Resolves kubernetes/kubernetes#9338\r\n" +
            "Resolves kubernetes#9338\r\n" +
            "\r\n" +
            "# ------------------------ >8 ------------------------\r\n" +
            "\r\n" +
            "this data should not\r\n" +
            "be processed because\r\n" +
            "it is below\r\n" +
            "the\r\n" +
            "scissor\r\n";

        Change actual = new CommitParser("3a37f7e1b522909d1afff2e24945ead5e637a25c", message).parse();
        Change expected = new Change(
                "3a37f7e1b522909d1afff2e24945ead5e637a25c",
                "feat",
                "ngMessages",
                "provide support for dynamic message resolution",
                "Prior to this fix it was impossible to apply a binding to a the ngMessage directive to represent the name of the error.",
                "BREAKING CHaNGE: The `ngMessagesInclude` attribute is now its own directive and that must be placed as a **child** element within the element with the ngMessages directive.\n" +
                    "\n" +
                    "Closes #10036, #13\n" +
                    "Closes #9338\n" +
                    "Resolves kubernetes/kubernetes#9338\n" +
                    "Resolves kubernetes#9338",
                asList(
                        note("BREAKING CHaNGE",
                                "The `ngMessagesInclude` attribute is now its own directive and that must be placed as a **child** element within the element with the ngMessages directive.")),
                asList(
                        ref("closes", "", "", "#", "10036", "#10036"),
                        ref("closes", "", "", "#", "13", "#13"),
                        ref("closes", "", "", "#", "9338", "#9338"),
                        ref("resolves", "kubernetes", "kubernetes", "#", "9338", "kubernetes/kubernetes#9338"),
                        ref("resolves", "", "kubernetes", "#", "9338", "kubernetes#9338")),
                null);

        assertEquals(expected, actual);
    }

    @Test
    public void testRevert()
    {
        String message = "Revert \"chore: Prepare for next development iteration\"\r\n" +
            "\r\n" +
            "This reverts commit 51b8dd4063baacf4c4ebe5679da4c9559be339b6.\r\n";

        Change actual = new CommitParser("832d40da87f3b72c356a99674af176d7188dfa86", message).parse();
        Change expected = new Change(
                "832d40da87f3b72c356a99674af176d7188dfa86",
                null,
                null,
                null,
                "This reverts commit 51b8dd4063baacf4c4ebe5679da4c9559be339b6.",
                "",
                emptyList(),
                emptyList(),
                new Revert("chore: Prepare for next development iteration", "51b8dd4063baacf4c4ebe5679da4c9559be339b6"));

        assertEquals(expected, actual);
    }

    private Note note(String title, String text)
    {
        return new Note(title, text);
    }

    private Reference ref(String action, String owner, String repository, String prefix, String issue, String raw)
    {
        return new Reference(action, owner, repository, prefix, issue, raw);
    }
}
