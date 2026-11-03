# Integral Solver

A symbolic indefinite integral solver written in Java from scratch. It has its own lexer, parser, expression tree and exact arithmetic, with no CAS library behind it. The goal is a solver that works by applying integration rules to unseen inputs, not by looking up memorized answers.

> **Status: early development (Phase 0).** The foundation is in place: exact rational arithmetic, the expression tree, a printer, a lexer, a parser and an interactive CLI that echoes parsed expressions. The solver itself and the integration methods are not implemented yet.

## Goals

- Solve indefinite integrals symbolically and return the exact result.
- Make the method set extensible, so techniques can be added one at a time (the long-term target is roughly 30-40 methods).
- Select a method automatically from the shape of the expression, using a priority ordering with hand-tuned weights. If that fails, try every method.
- Return "unsolvable" when no method works.

### Roadmap

| Version | Scope |
|---|---|
| Phase 0 | Project setup, `Rational`, expression tree, `Printer`, `Lexer`, `Parser`, CLI (current) |
| v1.0.0 | Solve all integrals covered in Calculus 1 and Calculus 2 course content |
| v2.0 | Benchmark against Rubi / SymPy test suites |
| Later | Step-by-step solutions, definite integrals, Laplace / Fourier / Feynman techniques |

## Design decisions

- **Language and build:** Java 25 with Gradle (Kotlin DSL) and the Gradle Wrapper, so no system Gradle is needed.
- **No external math library:** own parser and own math engine.
- **Input:** plain text, only the variable `x` and numeric constants. No parametric integrals.
- **Exact arithmetic:** all numbers are exact fractions (`BigInteger`-based `Rational`). `pi` and `e` stay symbolic.
- **Output:** result only for now. Logarithms are printed as `ln|x|`.

## Input syntax

Multiplication must be explicit and powers use `^`:

```
2*x^2+1
(x+1)^2
-x^2
1/3*x
sin(x)
```

- Implicit multiplication such as `2x` is rejected with a parse error.
- Variable: `x`. Constants: `pi`, `e`. Numbers: integers and decimals.
- Operators: `+ - * / ^` and parentheses.
- Functions: `sin`, `cos`, `tan`, `sec`, `csc`, `cot`, `ln`, `sqrt`, `asin`, `acos`, `atan` (`arcsin`, `arccos`, `arctan` are accepted as aliases).

## Getting started

### Requirements

- JDK 25 (Temurin recommended)
- Git

### Build and test

```
git clone <repository-url>
cd Integral-Solver
.\gradlew test
```

On Linux or macOS use `./gradlew` instead of `.\gradlew`.

Gradle only prints failing tests by default. For a full HTML report open `build/reports/tests/test/index.html`.

### Run the interactive CLI

```
.\gradlew run --console=plain -q
```

The CLI currently parses each line into an expression tree and prints it back, which is useful for checking the parser and the printer. Type `exit` to quit.

If Gradle does not forward terminal input properly, build a standalone distribution instead:

```
.\gradlew installDist -q
.\build\install\Integral-Solver\bin\Integral-Solver.bat
```

Example session:

```
> -2*x
tree:  Mul(Num(-2), Var)
print: (-2)*x
> 2x
error: ...
```

## Project structure

```
Integral-Solver/
├── build.gradle.kts            Gradle build (java + application plugins, JUnit 5)
├── settings.gradle.kts         Project name
├── gradlew / gradlew.bat       Gradle Wrapper
├── .gitignore
├── README.md
└── src/
    ├── main/java/integral/
    │   ├── Main.java           Interactive CLI (parse -> print)
    │   ├── math/
    │   │   └── Rational.java   Immutable exact fraction on BigInteger,
    │   │                       always stored in lowest terms
    │   ├── ast/
    │   │   ├── Node.java       Sealed interface for the expression tree and
    │   │   │                   its node types: Num, Var, Const, Add, Mul,
    │   │   │                   Pow, Neg, Func
    │   │   ├── FuncKind.java   Supported functions (sin, cos, ln, ...)
    │   │   ├── ConstKind.java  Symbolic constants (PI, E)
    │   │   └── Printer.java    Tree -> text, precedence-aware parentheses
    │   ├── parser/
    │   │   ├── Lexer.java      Text -> tokens (numbers, names, operators)
    │   │   ├── Parser.java     Tokens -> expression tree
    │   │   └── ParseException.java
    │   ├── solver/             Solver core (planned)
    │   └── methods/            Integration methods (planned)
    └── test/java/integral/     JUnit 5 tests mirroring the packages above
        ├── math/RationalTest.java
        ├── ast/NodeTest.java
        ├── ast/PrinterTest.java
        └── parser/{LexerTest,ParserTest}.java
```

### How the pieces fit together

```
input text --Lexer--> tokens --Parser--> Node tree --Solver--> Node tree --Printer--> output text
                                                      |
                                       IntegrationMethod implementations
```

1. **Lexer** splits the input into numbers, names (`sin`, `pi`, `x`) and operators. Whitespace is skipped.
2. **Parser** builds an immutable expression tree from the tokens.
3. **Solver** (planned) picks an `IntegrationMethod` based on the expression pattern and applies it. If it fails, it tries the remaining methods.
4. **Printer** turns a tree back into text. It adds parentheses only where operator precedence requires them, so printed output can be parsed back into the same tree.

`Rational` is used for all numeric values in the tree, so there is no floating-point error anywhere in the pipeline.

## Contributing

Conventions used in this repository:

- All code (class, method, variable and test names, comments) is written in English.
- Tests come first where practical (`.\gradlew test` must stay green).
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/), for example `feat(printer): add Printer with precedence-aware parentheses`.

## License

This project is licensed under the MIT License.

```
MIT License

Copyright (c) 2026 Mehmet Emin Özdemir

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```
