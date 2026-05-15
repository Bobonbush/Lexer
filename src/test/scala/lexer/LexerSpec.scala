package lexer

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LexerSpec extends AnyFlatSpec with Matchers:

  private def lex(src: String): List[Token] =
    val stream = CharacterStream(src)
    val lexer  = Lexer(stream)
    Iterator.continually(lexer.nextToken())
      .takeWhile(_.tokenType != TokenType.EOF)
      .toList

  private def lexWithEOF(src: String): List[Token] =
    val stream = CharacterStream(src)
    val lexer  = Lexer(stream)
    val buf    = collection.mutable.ListBuffer[Token]()
    var tok    = lexer.nextToken()
    while tok.tokenType != TokenType.EOF do
      buf += tok
      tok  = lexer.nextToken()
    buf += tok
    buf.toList

  private def lexer(src: String): Lexer =
    Lexer(CharacterStream(src))

  // =========================================================
  // EOF
  // =========================================================

  "Lexer" should "produce an EOF token on empty input" in:
    val tok = lexer("").nextToken()
    tok.tokenType shouldBe TokenType.EOF
    tok.text      shouldBe ""

  it should "produce EOF after all tokens are consumed" in:
    val l = lexer("x")
    l.nextToken()
    l.nextToken().tokenType shouldBe TokenType.EOF

  it should "produce EOF repeatedly once exhausted" in:
    val l = lexer("")
    l.nextToken().tokenType shouldBe TokenType.EOF
    l.nextToken().tokenType shouldBe TokenType.EOF

  // =========================================================
  // Identifiers
  // =========================================================

  it should "lex a single identifier" in:
    val toks = lex("hello")
    toks should have size 1
    toks.head.tokenType shouldBe TokenType.Identifier
    toks.head.text      shouldBe "hello"

  it should "lex an identifier starting with underscore" in:
    val toks = lex("_name")
    toks.head.tokenType shouldBe TokenType.Identifier
    toks.head.text      shouldBe "_name"

  it should "lex an identifier containing digits" in:
    val toks = lex("x1")
    toks.head.tokenType shouldBe TokenType.Identifier
    toks.head.text      shouldBe "x1"

  // =========================================================
  // Keywords
  // =========================================================

  it should "recognise all keywords" in:
    val keywords = List(
      "if" -> TokenType.If,
      "else" -> TokenType.Else,
      "while" -> TokenType.While,
      "for" -> TokenType.For,
      "return" -> TokenType.Return,
      "true" -> TokenType.True,
      "false" -> TokenType.False,
      "class" -> TokenType.Class,
      "var" -> TokenType.Var,
      "val" -> TokenType.Val
    )
    for (text, expected) <- keywords do
      val toks = lex(text)
      toks should have size 1
      toks.head.tokenType shouldBe expected
      toks.head.text      shouldBe text

  it should "not treat keyword prefixes as keywords" in:
    val toks = lex("iffy")
    toks.head.tokenType shouldBe TokenType.Identifier
    toks.head.text      shouldBe "iffy"

  // =========================================================
  // Numbers
  // =========================================================

  it should "lex an integer" in:
    val toks = lex("42")
    toks.head.tokenType shouldBe TokenType.Number
    toks.head.text      shouldBe "42"

  it should "lex a floating-point number" in:
    val toks = lex("3.14")
    toks.head.tokenType shouldBe TokenType.Number
    toks.head.text      shouldBe "3.14"

  it should "not consume a trailing dot as part of a number" in:
    val toks = lex("3.")
    toks should have size 2
    toks(0).tokenType shouldBe TokenType.Number
    toks(0).text      shouldBe "3"
    toks(1).tokenType shouldBe TokenType.Dot

  // =========================================================
  // Strings
  // =========================================================

  it should "lex a simple string literal" in:
    val toks = lex(""""hello"""")
    toks.head.tokenType shouldBe TokenType.String
    toks.head.text      shouldBe """"hello""""

  it should "lex a string with an escape sequence" in:
    val toks = lex(""""a\\b"""")
    toks.head.tokenType shouldBe TokenType.String

  it should "lex an empty string literal" in:
    val toks = lex("\"\"")
    toks.head.tokenType shouldBe TokenType.String
    toks.head.text      shouldBe "\"\""

  // =========================================================
  // Single-character operators
  // =========================================================

  it should "lex each single-character operator" in:
    val cases = List(
      "+" -> TokenType.Plus,
      "-" -> TokenType.Minus,
      "*" -> TokenType.Star,
      "/" -> TokenType.Slash,
      "(" -> TokenType.LeftParen,
      ")" -> TokenType.RightParen,
      "{" -> TokenType.LeftBrace,
      "}" -> TokenType.RightBrace,
      "," -> TokenType.Comma,
      "." -> TokenType.Dot,
      ";" -> TokenType.Semicolon,
      ":" -> TokenType.Colon,
      "!" -> TokenType.Bang,
      "<" -> TokenType.Less,
      ">" -> TokenType.Greater,
      "=" -> TokenType.Equal
    )
    for (text, expected) <- cases do
      val toks = lex(text)
      toks should have size 1
      toks.head.tokenType shouldBe expected
      toks.head.text      shouldBe text

  // =========================================================
  // Multi-character operators
  // =========================================================

  it should "lex multi-character operators" in:
    val cases = List(
      "==" -> TokenType.EqualEqual,
      "!=" -> TokenType.BangEqual,
      "<=" -> TokenType.LessEqual,
      ">=" -> TokenType.GreaterEqual,
      "&&" -> TokenType.AndAnd,
      "||" -> TokenType.OrOr
    )
    for (text, expected) <- cases do
      val toks = lex(text)
      toks should have size 1
      toks.head.tokenType shouldBe expected
      toks.head.text      shouldBe text

  it should "distinguish = from ==" in:
    val toks = lex("===")
    toks should have size 2
    toks(0).tokenType shouldBe TokenType.EqualEqual
    toks(1).tokenType shouldBe TokenType.Equal

  // =========================================================
  // Whitespace
  // =========================================================

  it should "skip spaces, tabs, and newlines" in:
    val toks = lex("  \t\n  x")
    toks should have size 1
    toks.head.tokenType shouldBe TokenType.Identifier
    toks.head.text      shouldBe "x"

  // =========================================================
  // Comments
  // =========================================================

  it should "skip single-line comments" in:
    val toks = lex("x // this is a comment\ny")
    toks.map(_.text) shouldBe List("x", "y")

  it should "skip a multi-line block comment" in:
    val toks = lex("x /* block\ncomment */ y")
    toks.map(_.text) shouldBe List("x", "y")

  it should "skip nested-looking but flat block comments" in:
    val toks = lex("/* comment */ 1")
    toks.head.tokenType shouldBe TokenType.Number

  // =========================================================
  // Position / Span correctness
  // =========================================================

  it should "report correct offset for the first token" in:
    val toks = lex("abc")
    toks.head.span.start.offset shouldBe 0
    toks.head.span.end.offset   shouldBe 3

  it should "report correct offset for a token after whitespace" in:
    val toks = lex("  x")
    toks.head.span.start.offset shouldBe 2
    toks.head.span.end.offset   shouldBe 3

  it should "report correct line for a token on line 2" in:
    val toks = lex("a\nb")
    toks(1).span.start.line shouldBe 2

  it should "report correct column for a token" in:
    val toks = lex("  x")
    toks.head.span.start.column shouldBe 3

  // =========================================================
  // Multi-token sequences
  // =========================================================

  it should "lex 'x = 10 + 20' correctly" in:
    val toks = lex("x = 10 + 20")
    toks.map(_.tokenType) shouldBe List(
      TokenType.Identifier,
      TokenType.Equal,
      TokenType.Number,
      TokenType.Plus,
      TokenType.Number
    )
    toks.map(_.text) shouldBe List("x", "=", "10", "+", "20")

  it should "lex a simple if expression" in:
    val toks = lex("if (x == 0) { return y; }")
    toks.map(_.tokenType) shouldBe List(
      TokenType.If,
      TokenType.LeftParen,
      TokenType.Identifier,
      TokenType.EqualEqual,
      TokenType.Number,
      TokenType.RightParen,
      TokenType.LeftBrace,
      TokenType.Return,
      TokenType.Identifier,
      TokenType.Semicolon,
      TokenType.RightBrace
    )

  // =========================================================
  // Diagnostics
  // =========================================================

  it should "report a diagnostic for an unterminated string" in:
    val stream = CharacterStream(""""hello""")
    val l      = Lexer(stream)
    val tok    = l.nextToken()
    tok.tokenType           shouldBe TokenType.Invalid
    l.getDiagnostics        should not be empty
    l.getDiagnostics.head.message shouldBe "Unterminated string literal"

  it should "report a diagnostic for an unterminated block comment" in:
    val stream = CharacterStream("/* no end")
    val l      = Lexer(stream)
    l.nextToken()
    l.getDiagnostics should not be empty
    l.getDiagnostics.head.message shouldBe "Unterminated block comment"

  it should "report a diagnostic for an unexpected character" in:
    val stream = CharacterStream("@")
    val l      = Lexer(stream)
    val tok    = l.nextToken()
    tok.tokenType shouldBe TokenType.Invalid
    l.getDiagnostics should not be empty

  it should "continue lexing after an invalid token" in:
    val toks = lex("@ x")
    toks.last.tokenType shouldBe TokenType.Identifier
    toks.last.text      shouldBe "x"

  it should "accumulate multiple diagnostics" in:
    val stream = CharacterStream("@ @")
    val l      = Lexer(stream)
    l.nextToken()
    l.nextToken()
    l.getDiagnostics should have size 2
