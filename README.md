[![Build Status](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate/badge.svg)](https://coveralls.io/github/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-text-cursor-parser-ebnf-charpredicate)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)



Supports defining `CharPredicate(s)` using from a text grammar file defined using [EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form).

The sample below defines two predicates, note more complex forms that including references, negation, grouping along with
alternatives, concatenation and repeating are supported. The test/resources directory contains more examples.

```EBNF
A_THEN_B_THEN_C = "A" , "B" , "C";

A_OR_B_OR_C = "A" | "B" | "C";
```


