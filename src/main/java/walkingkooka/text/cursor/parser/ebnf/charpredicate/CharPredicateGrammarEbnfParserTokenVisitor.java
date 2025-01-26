/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.text.cursor.parser.ebnf.charpredicate;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicateBuilder;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.AlternativeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.ConcatenationEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserTokenVisitor;
import walkingkooka.text.cursor.parser.ebnf.ExceptionEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.GrammarEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.IdentifierEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.OptionalEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RangeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RepeatedEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RuleEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.TerminalEbnfParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A visitor that accepts a grammar and transforms all tokens in {@link CharPredicate predicates} placing each rule into a map.
 */
final class CharPredicateGrammarEbnfParserTokenVisitor extends EbnfParserTokenVisitor {

    static Map<EbnfIdentifierName, CharPredicate> fromGrammar(final GrammarEbnfParserToken grammar,
                                                              final Map<EbnfIdentifierName, CharPredicate> predefined) {
        Objects.requireNonNull(grammar, "grammar");
        Objects.requireNonNull(predefined, "predefined");

        grammar.checkIdentifiers(predefined.keySet());

        final Map<EbnfIdentifierName, CharPredicate> copy = Maps.ordered();
        copy.putAll(predefined);

        new CharPredicateGrammarEbnfParserTokenVisitor(copy).accept(grammar);

        return Maps.immutable(copy);
    }

    private CharPredicateGrammarEbnfParserTokenVisitor(final Map<EbnfIdentifierName, CharPredicate> identifierToCharPredicate) {
        this.identifierToCharPredicate = identifierToCharPredicate;
    }

    // GRAMMAR ........................................................................................................

    @Override
    protected Visiting startVisit(final GrammarEbnfParserToken token) {
        // need this mapping to fetch tokens for a rule by identifier at any stage or walking...
        token.value()
                .stream()
                .filter(t -> t instanceof EbnfParserToken)
                .map(CharPredicateGrammarEbnfParserTokenVisitor::toEbnfParserToken)
                .filter(EbnfParserToken::isRule)
                .map(CharPredicateGrammarEbnfParserTokenVisitor::toRuleEbnfParserToken)
                .forEach(this::ruleIdentifier);
        return Visiting.CONTINUE;
    }

    private static EbnfParserToken toEbnfParserToken(final ParserToken token) {
        return token.cast(EbnfParserToken.class);
    }

    private static RuleEbnfParserToken toRuleEbnfParserToken(final ParserToken token) {
        return token.cast(RuleEbnfParserToken.class);
    }

    private void ruleIdentifier(final RuleEbnfParserToken rule) {
        final EbnfIdentifierName identifier = rule.identifier().value();
        this.identifierToRule.put(identifier, rule);
        this.identifierToCharPredicate.put(identifier, CharPredicateGrammarEbnfParserTokenVisitorProxy.with(identifier));
    }

    // RULE ........................................................................................................

    @Override
    protected Visiting startVisit(final RuleEbnfParserToken rule) {
        this.enter();
        this.accept(rule.assignment()); // RHS

        // update the proxy holding all references to this rule...
        final CharPredicateGrammarEbnfParserTokenVisitorProxy proxy = Cast.to(this.identifierToCharPredicate.get(rule.identifier().value()));
        proxy.predicate = this.children.get(0);

        return Visiting.SKIP; // skip because we dont want to visit LHS of rule.
    }

    private final Map<EbnfIdentifierName, RuleEbnfParserToken> identifierToRule = Maps.ordered();

    // ALT .......................................................................................................

    @Override
    protected Visiting startVisit(final AlternativeEbnfParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final AlternativeEbnfParserToken token) {
        final CharPredicateBuilder b = CharPredicates.builder();
        for (CharPredicate p : this.children) {
            b.or(p);
        }
        this.exit();
        this.add(
                b.build().setToString(token.toString()),
                token);
    }

    // CONCAT .......................................................................................................

    @Override
    protected Visiting startVisit(final ConcatenationEbnfParserToken token) {
        return this.fail("Concatenation", token);
    }

    // EXCEPTION .......................................................................................................

    @Override
    protected Visiting startVisit(final ExceptionEbnfParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final ExceptionEbnfParserToken token) {
        final CharPredicate predicate = this.children.get(0)
                .andNot(this.children.get(1))
                .setToString(token.toString());

        this.exit();
        this.add(predicate, token);
    }

    // OPTIONAL .......................................................................................................

    @Override
    protected Visiting startVisit(final OptionalEbnfParserToken token) {
        return this.fail("Optional", token);
    }

    // RANGE ...................................................................................................................

    @Override
    protected Visiting startVisit(final RangeEbnfParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final RangeEbnfParserToken token) {
        final char begin = this.characterForIdentifierOrTerminal(token.begin());
        final char end = this.characterForIdentifierOrTerminal(token.end());

        final CharPredicate predicate = CharPredicates.range(begin, end)
                .setToString(token.toString());
        this.exit();
        this.add(
                predicate,
                token);
    }

    private char characterForIdentifierOrTerminal(final EbnfParserToken token) {
        return token.isTerminal() ?
                this.characterFromTerminal(token.cast(TerminalEbnfParserToken.class)) :
                token.isIdentifier() ?
                        this.characterFromIdentifierReference(token.cast(IdentifierEbnfParserToken.class)) :
                        failInvalidRangeBound("Invalid range bound, expected terminal or identifier indirectly pointing to a terminal but got " + token);
    }

    private char characterFromIdentifierReference(final IdentifierEbnfParserToken identifier) {
        final RuleEbnfParserToken rule = this.identifierToRule.get(identifier.value());
        return this.characterForIdentifierOrTerminal(rule.assignment());
    }

    private char characterFromTerminal(final TerminalEbnfParserToken terminal) {
        final String value = terminal.value();
        final CharSequence unescaped = CharSequences.unescape(value);
        if (unescaped.length() != 1) {
            failInvalidRangeBound("The range terminal does not contain a single character=" + terminal);
        }
        return unescaped.charAt(0);
    }

    private static char failInvalidRangeBound(final String message) {
        throw new IllegalArgumentException(message);
    }

    // REPEATED .......................................................................................................

    @Override
    protected Visiting startVisit(final RepeatedEbnfParserToken token) {
        return this.fail("Repeated", token);
    }

    // IDENTIFIER .......................................................................................................

    @Override
    protected void visit(final IdentifierEbnfParserToken token) {
        this.add(
                this.identifierToCharPredicate.get(token.value()),
                token);
    }

    // TERMINAL .......................................................................................................

    @Override
    protected void visit(final TerminalEbnfParserToken token) {
        this.add(
                CharPredicates.any(token.value()).setToString(token.toString()),
                token);
    }

    // GENERAL PURPOSE .................................................................................................

    final Map<EbnfIdentifierName, CharPredicate> identifierToCharPredicate;

    private void enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
    }

    private void exit() {
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
    }

    private Stack<List<CharPredicate>> previousChildren = Stacks.arrayList();

    private List<CharPredicate> children;

    private void add(final CharPredicate predicate, final EbnfParserToken token) {
        if (null == predicate) {
            throw new NullPointerException("Null predicate returned for " + token);
        }
        this.children.add(predicate);
    }

    private Visiting fail(final String label, final EbnfParserToken token) {
        throw new UnsupportedOperationException(label + " tokens not supported in CharPredicate grammar=" + token);
    }

    public String toString() {
        return this.children.toString();
    }
}
