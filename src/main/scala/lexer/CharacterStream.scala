package lexer

object CharacterStream:
  val EOF: Char = ' '

class CharacterStream(private val input: String):
  private var position: Int = 0
  private var line: Int = 1
  private var column: Int = 1

  def peek(offset: Int = 0): Char =
    val index = position + offset
    if index < input.length then input(index) else CharacterStream.EOF

  def advance(): Char =
    if position < input.length then
      val ch = input(position)
      if ch == '\n' then
        line += 1
        column = 1
      else
        column += 1
      position += 1
      ch
    else
      CharacterStream.EOF

  def matchChar(expected: Char): Boolean =
    if isEOF || input(position) != expected then false
    else
      advance()
      true

  def currentPosition(): Position =
    Position(position, line, column)

  def isEOF: Boolean =
    position >= input.length

  def slice(start: Int, end: Int): String =
    if start >= 0 && end <= input.length && start <= end then
      input.substring(start, end)
    else
      ""
