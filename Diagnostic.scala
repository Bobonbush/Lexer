package lexer

final case class Diagnostic(message: String, span: Span):
	def format: String =
		s"$message at line ${span.start.line} column ${span.start.column}"

	override def toString: String =
		format
