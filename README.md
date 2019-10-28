[![Build Status](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate.svg?branch=master)](https://travis-ci.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate?branch=master)

Supports defining  CharPredicate(s)` using from a text grammar file defined using [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form).

The sample below defines two predicates, note more complex forms that including references, negation, grouping along with
alternatives, concatenation and repeating are supported. The test/resources directory contains more examples.

```EBNF
A_THEN_B_THEN_C = "A" , "B" , "C";

A_OR_B_OR_C = "A" | "B" | "C";
```

## Dependencies

- walkingkooka
- Only junit!

## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate.git
```
