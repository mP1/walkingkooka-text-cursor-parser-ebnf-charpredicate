package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.collect.map.Maps;
import walkingkooka.j2cl.locale.LocaleAware;
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

@LocaleAware
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
        assertEquals(
                "parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                true,
                loadGrammarAndGetCharPredicate()
                        .test(test)
        );
    }

    public void testParseGrammarAndTest2() {
        final char test = 'Z';

        assertEquals(
                "parsed " + CharSequences.quoteAndEscape(GRAMMAR) + " test " + CharSequences.quoteIfChars(test),
                false,
                loadGrammarAndGetCharPredicate()
                        .test(test)
        );
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

//        assertNotEquals(
//                "CharPredicate \"TEST\" not found in grammar " + CharSequences.quoteAndEscape(GRAMMAR),
//                null,
//                predicate
//        );
        return predicate;
    }
}
