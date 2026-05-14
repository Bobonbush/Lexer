package lexer

import scala.collection.mutable.ListBuffer

class Lexer(private val stream: CharacterStream):

  private val diagnostics = ListBuffer[Diagnostic]()

  private val keywords: Map[String, TokenType] = Map(
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

  def getDiagnostics: List[Diagnostic] =
    diagnostics.toList

  def nextToken(): Token =
    skipWhitespaceAndComments()

    val start = stream.currentPosition()

    if stream.isEOF then
      return Token(
        TokenType.EOF,
        "",
        Span(start, start)
      )

    val ch = stream.peek()

    // -----------------------------
    // Identifiers / Keywords
    // -----------------------------
    if isIdentifierStart(ch) then
      return scanIdentifier()

    // -----------------------------
    // Numbers
    // -----------------------------
    if ch.isDigit then
      return scanNumber()

    // -----------------------------
    // Strings
    // -----------------------------
    if ch == '"' then
      return scanString()

    // -----------------------------
    // Operators / Symbols
    // -----------------------------
    stream.advance()

    val tokenType = ch match
      case '+' => TokenType.Plus
      case '-' => TokenType.Minus
      case '*' => TokenType.Star
      case '/' => TokenType.Slash

      case '(' => TokenType.LeftParen
      case ')' => TokenType.RightParen
      case '{' => TokenType.LeftBrace
      case '}' => TokenType.RightBrace

      case ',' => TokenType.Comma
      case '.' => TokenType.Dot
      case ';' => TokenType.Semicolon
      case ':' => TokenType.Colon

      case '=' =>
        if stream.peek() == '=' then
          stream.advance()
          TokenType.EqualEqual
        else
          TokenType.Equal

      case '!' =>
        if stream.peek() == '=' then
          stream.advance()
          TokenType.BangEqual
        else
          TokenType.Bang

      case '<' =>
        if stream.peek() == '=' then
          stream.advance()
          TokenType.LessEqual
        else
          TokenType.Less

      case '>' =>
        if stream.peek() == '=' then
          stream.advance()
          TokenType.GreaterEqual
        else
          TokenType.Greater

      case '&' =>
        if stream.peek() == '&' then
          stream.advance()
          TokenType.AndAnd
        else
          invalidToken(start, "Unexpected '&'")

      case '|' =>
        if stream.peek() == '|' then
          stream.advance()
          TokenType.OrOr
        else
          invalidToken(start, "Unexpected '|'")

      case _ =>
        invalidToken(start, s"Unexpected character '$ch'")

    val end = stream.currentPosition()

    Token(
      tokenType,
      stream.slice(start.offset, end.offset),
      Span(start, end)
    )

  // =========================================================
  // Identifier / Keyword
  // =========================================================

  private def scanIdentifier(): Token =
    val start = stream.currentPosition()

    while isIdentifierPart(stream.peek()) do
      stream.advance()

    val end = stream.currentPosition()

    val text =
      stream.slice(start.offset, end.offset)

    val tokenType =
      keywords.getOrElse(text, TokenType.Identifier)

    Token(
      tokenType,
      text,
      Span(start, end)
    )

  // =========================================================
  // Numbers
  // =========================================================

  private def scanNumber(): Token =
    val start = stream.currentPosition()

    while stream.peek().isDigit do
      stream.advance()

    // decimal part
    if stream.peek() == '.' &&
       stream.peek(1).isDigit then

      stream.advance()

      while stream.peek().isDigit do
        stream.advance()

    val end = stream.currentPosition()

    Token(
      TokenType.Number,
      stream.slice(start.offset, end.offset),
      Span(start, end)
    )

  // =========================================================
  // Strings
  // =========================================================

  private def scanString(): Token =
    val start = stream.currentPosition()

    // consume opening "
    stream.advance()

    while !stream.isEOF && stream.peek() != '"' do

      if stream.peek() == '\\' then
        stream.advance()

        if !stream.isEOF then
          stream.advance()
      else
        stream.advance()

    if stream.isEOF then
      diagnostics += Diagnostic(
        "Unterminated string literal",
        Span(start, stream.currentPosition())
      )

      return Token(
        TokenType.Invalid,
        "",
        Span(start, stream.currentPosition())
      )

    // consume closing "
    stream.advance()

    val end = stream.currentPosition()

    Token(
      TokenType.String,
      stream.slice(start.offset, end.offset),
      Span(start, end)
    )

  // =========================================================
  // Whitespace / Comments
  // =========================================================

  private def skipWhitespaceAndComments(): Unit =
    var looping = true

    while looping && !stream.isEOF do

      stream.peek() match

        case ' ' | '\r' | '\t' | '\n' =>
          stream.advance()

        // single-line comment
        case '/' if stream.peek(1) == '/' =>
          while !stream.isEOF &&
                stream.peek() != '\n' do
            stream.advance()

        // multi-line comment
        case '/' if stream.peek(1) == '*' =>
          stream.advance()
          stream.advance()

          while !stream.isEOF &&
                !(stream.peek() == '*' &&
                  stream.peek(1) == '/') do

            stream.advance()

          if !stream.isEOF then
            stream.advance()
            stream.advance()

        case _ =>
          looping = false

  // =========================================================
  // Helpers
  // =========================================================

  private def isIdentifierStart(ch: Char): Boolean =
    ch.isLetter || ch == '_'

  private def isIdentifierPart(ch: Char): Boolean =
    ch.isLetterOrDigit || ch == '_'

  private def invalidToken(
      start: Position,
      message: String
  ): TokenType =

    diagnostics += Diagnostic(
      message,
      Span(start, stream.currentPosition())
    )

    TokenType.Invalid