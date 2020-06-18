/*
 * Copyright © 2020 Miroslav Pokorny
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
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.charpredicate.EbnfGrammarCharPredicates;

import java.util.Map;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    private final static String GRAMMAR = "TEST = \"A\" | \"B\" | \"C\";";

    @Test
    public void testParseGrammarAndTest() {
        final char test = 'A';
        Assert.assertEquals("parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                true,
                loadGrammarAndGetCharPredicate().test(test));
    }

    @Test
    public void testParseGrammarAndTest2() {
        final char test = 'Z';
        Assert.assertEquals("parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                false,
                loadGrammarAndGetCharPredicate().test(test));
    }

    private CharPredicate loadGrammarAndGetCharPredicate() {
        final TextCursor grammarFile = TextCursors.charSequence(GRAMMAR);
        final EbnfGrammarParserToken parsed = EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(grammarFile, EbnfParserContexts.basic())
                .get()
                .cast(EbnfGrammarParserToken.class);

        final Map<EbnfIdentifierName, CharPredicate> predicates = EbnfGrammarCharPredicates.fromGrammar(parsed, Maps.empty());
        final CharPredicate predicate = predicates.get(EbnfIdentifierName.with("TEST"));
        Assert.assertNotEquals("CharPredicate \"TEST\" not found in grammar " + CharSequences.quoteAndEscape(GRAMMAR), null, predicate);
        return predicate;
    }
}
