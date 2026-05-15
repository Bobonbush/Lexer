package lexer

enum TokenType:
  case Identifier
  case Number
  case String
  case Invalid
  case EOF

  case If
  case Else
  case While
  case For
  case Return
  case True
  case False
  case Class
  case Var
  case Val

  case Plus
  case Minus
  case Star
  case Slash

  case LeftParen
  case RightParen
  case LeftBrace
  case RightBrace

  case Comma
  case Dot
  case Semicolon
  case Colon

  case Equal
  case EqualEqual
  case Bang
  case BangEqual
  case Less
  case LessEqual
  case Greater
  case GreaterEqual

  case AndAnd
  case OrOr

object TokenType:
  val keywordTable: Map[String, TokenType] = Map(
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

  def fromKeyword(text: String): Option[TokenType] =
    keywordTable.get(text)
