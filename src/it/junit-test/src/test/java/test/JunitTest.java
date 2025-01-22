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
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.charpredicate.EbnfGrammarCharPredicates;

import java.util.Map;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    private final static String GRAMMAR = "TEST = \"A\" | \"B\" | \"C\";";

    @Test
    public void testParseGrammarAndTest() {
        final char test = 'A';

        this.checkEquals(
                "parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                true,
                this.loadGrammarAndGetCharPredicate().test(test)
        );
    }

    @Test
    public void testParseGrammarAndTest2() {
        final char test = 'Z';
        this.checkEquals(
                "parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                false,
                this.loadGrammarAndGetCharPredicate().test(test)
        );
    }

    private CharPredicate loadGrammarAndGetCharPredicate() {
        final Map<EbnfIdentifierName, CharPredicate> predicates = EbnfGrammarCharPredicates.fromGrammar(
                EbnfParserToken.parse(GRAMMAR),
                Maps.empty()
        );
        final CharPredicate predicate = predicates.get(EbnfIdentifierName.with("TEST"));
        this.checkNotEquals(
                "CharPredicate \"TEST\" not found in grammar " + CharSequences.quoteAndEscape(GRAMMAR),
                null,
                predicate
        );
        return predicate;
    }

    private void checkEquals(final String message,
                             final Object expected,
                             final Object actual) {
        Assert.assertEquals(
                expected,
                actual
        );
    }

    private void checkNotEquals(final String message,
                                final Object expected,
                                final Object actual) {
        Assert.assertNotEquals(
                expected,
                actual
        );
    }
}
