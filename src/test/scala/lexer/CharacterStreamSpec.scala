package lexer

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CharacterStreamSpec extends AnyFlatSpec with Matchers:

  // =========================================================
  // advance()
  // =========================================================

  "advance()" should "return the current character before moving forward" in:
    val s = CharacterStream("abc")
    s.advance() shouldBe 'a'
    s.advance() shouldBe 'b'
    s.advance() shouldBe 'c'

  it should "advance the stream position" in:
    val s = CharacterStream("xy")
    s.advance()
    s.peek() shouldBe 'y'

  it should "return the EOF sentinel when called past end" in:
    val s = CharacterStream("a")
    s.advance()
    s.advance() shouldBe CharacterStream.EOF

  // =========================================================
  // isEOF
  // =========================================================

  "isEOF" should "be false at the start of a non-empty string" in:
    val s = CharacterStream("hi")
    s.isEOF shouldBe false

  it should "be true immediately on an empty string" in:
    val s = CharacterStream("")
    s.isEOF shouldBe true

  it should "be true after consuming all characters" in:
    val s = CharacterStream("a")
    s.advance()
    s.isEOF shouldBe true

  // =========================================================
  // peek() -- default offset 0
  // =========================================================

  "peek()" should "return the current character without consuming it" in:
    val s = CharacterStream("abc")
    s.peek() shouldBe 'a'
    s.peek() shouldBe 'a'

  it should "return the EOF sentinel on an empty string" in:
    val s = CharacterStream("")
    s.peek() shouldBe CharacterStream.EOF

  it should "return the EOF sentinel after stream is exhausted" in:
    val s = CharacterStream("x")
    s.advance()
    s.peek() shouldBe CharacterStream.EOF

  // =========================================================
  // peek(offset) -- arbitrary lookahead
  // =========================================================

  "peek(offset)" should "look ahead by the given number of characters" in:
    val s = CharacterStream("abcd")
    s.peek(0) shouldBe 'a'
    s.peek(1) shouldBe 'b'
    s.peek(2) shouldBe 'c'
    s.peek(3) shouldBe 'd'

  it should "return the EOF sentinel when offset exceeds the string" in:
    val s = CharacterStream("ab")
    s.peek(2)   shouldBe CharacterStream.EOF
    s.peek(10)  shouldBe CharacterStream.EOF
    s.peek(100) shouldBe CharacterStream.EOF

  it should "not advance the stream when used" in:
    val s = CharacterStream("abc")
    s.peek(2)
    s.peek(0) shouldBe 'a'

  // =========================================================
  // matchChar()
  // =========================================================

  "matchChar()" should "return true and advance when the expected char matches" in:
    val s = CharacterStream("ab")
    s.matchChar('a') shouldBe true
    s.peek() shouldBe 'b'

  it should "return false and not advance when the char does not match" in:
    val s = CharacterStream("ab")
    s.matchChar('z') shouldBe false
    s.peek() shouldBe 'a'

  it should "return false and be safe when called at EOF" in:
    val s = CharacterStream("")
    s.matchChar('x') shouldBe false
    s.isEOF shouldBe true

  it should "work correctly with successive calls" in:
    val s = CharacterStream("==")
    s.matchChar('=') shouldBe true
    s.matchChar('=') shouldBe true
    s.isEOF shouldBe true

  // =========================================================
  // Line tracking
  // =========================================================

  "line tracking" should "start at line 1" in:
    val s = CharacterStream("a")
    s.currentPosition().line shouldBe 1

  it should "increment line after a newline character" in:
    val s = CharacterStream("a\nb")
    s.advance() // 'a'
    s.advance() // '\n'
    s.currentPosition().line shouldBe 2

  it should "count multiple newlines correctly" in:
    val s = CharacterStream("a\n\nb")
    s.advance() // 'a'
    s.advance() // first '\n'
    s.advance() // second '\n'
    s.currentPosition().line shouldBe 3

  it should "reset column to 1 after a newline" in:
    val s = CharacterStream("ab\ncd")
    s.advance() // 'a'
    s.advance() // 'b'
    s.advance() // '\n'
    s.currentPosition().column shouldBe 1

  // =========================================================
  // Column tracking
  // =========================================================

  "column tracking" should "start at column 1" in:
    val s = CharacterStream("a")
    s.currentPosition().column shouldBe 1

  it should "increment column as characters are consumed" in:
    val s = CharacterStream("abc")
    s.advance() // 'a' -- now at 'b'
    s.currentPosition().column shouldBe 2
    s.advance() // 'b' -- now at 'c'
    s.currentPosition().column shouldBe 3

  it should "track columns on the second line correctly" in:
    val s = CharacterStream("a\nbc")
    s.advance() // 'a'
    s.advance() // '\n'
    s.advance() // 'b' -- col 1 consumed, now at 'c'
    s.currentPosition().column shouldBe 2

  // =========================================================
  // Offset tracking
  // =========================================================

  "offset tracking" should "start at offset 0" in:
    val s = CharacterStream("abc")
    s.currentPosition().offset shouldBe 0

  it should "increment offset by 1 per advance()" in:
    val s = CharacterStream("abc")
    s.advance()
    s.currentPosition().offset shouldBe 1
    s.advance()
    s.currentPosition().offset shouldBe 2

  it should "increment offset correctly across newlines" in:
    val s = CharacterStream("a\nb")
    s.advance() // offset 0 -> 1
    s.advance() // offset 1 -> 2  (newline)
    s.currentPosition().offset shouldBe 2

  // =========================================================
  // Empty string edge cases
  // =========================================================

  "empty string" should "report isEOF immediately" in:
    CharacterStream("").isEOF shouldBe true

  it should "return the EOF sentinel for peek()" in:
    CharacterStream("").peek() shouldBe CharacterStream.EOF

  it should "return the EOF sentinel for advance()" in:
    CharacterStream("").advance() shouldBe CharacterStream.EOF

  it should "return false for matchChar()" in:
    CharacterStream("").matchChar('a') shouldBe false

  it should "start at line 1 column 1 offset 0" in:
    val pos = CharacterStream("").currentPosition()
    pos.line   shouldBe 1
    pos.column shouldBe 1
    pos.offset shouldBe 0

  // =========================================================
  // Multiple newlines
  // =========================================================

  "multiple newlines" should "produce correct line and column for 'a\\n\\nb'" in:
    val s = CharacterStream("a\n\nb")
    s.advance() // 'a'
    s.advance() // first '\n'
    s.advance() // second '\n'
    val pos = s.currentPosition()
    pos.line   shouldBe 3
    pos.column shouldBe 1
    pos.offset shouldBe 3
