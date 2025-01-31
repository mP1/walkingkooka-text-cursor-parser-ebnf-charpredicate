package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.charpredicate.EbnfGrammarCharPredicates;

import java.util.Map;

@walkingkooka.j2cl.locale.LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
                1,
                1
        );
    }

    private final static String GRAMMAR = "TEST = \"A\" | \"B\" | \"C\";";

    public void testParseGrammarAndTest() {
        final char test = 'A';

        this.checkEquals(
                "parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                true,
                this.loadGrammarAndGetCharPredicate().test(test)
        );
    }

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
        assertEquals(
                expected,
                actual
        );
    }

    private void checkNotEquals(final String message,
                                final Object expected,
                                final Object actual) {
//        assertNotEquals(
//                expected,
//                actual
//        );
    }
}
