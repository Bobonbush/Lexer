package lexer

final case class Token(tokenType: TokenType, text: String, span: Span)
