package lexer

import java.io.{File, PrintWriter}
import scala.collection.mutable.ListBuffer

@main def exportTokensToCsv(): Unit =
  // =========================================================
  // Helper Function (Defined inside the main scope)
  // =========================================================
  def escapeForCsv(value: String): String =
    val doubleQuotesEscaped = value.replace("\"", "\"\"")
    if doubleQuotesEscaped.contains(",") || 
       doubleQuotesEscaped.contains("\n") || 
       doubleQuotesEscaped.contains("\r") || 
       doubleQuotesEscaped.contains("\"") || 
       doubleQuotesEscaped.contains(";") then
      s"\"$doubleQuotesEscaped\""
    else
      doubleQuotesEscaped

  // =========================================================
  // Core Logic
  // =========================================================
  val inputFileName = "test.scala"
  val outputFileName = "tokens.csv"
  
  val sourceFile = new File(inputFileName)
  
  // Read 'test.scala' if it exists; otherwise use a fallback sample
  val sourceCode = if sourceFile.exists() then
    println(s"[INFO] Found '$inputFileName'. Reading source code...")
    val source = scala.io.Source.fromFile(sourceFile)
    try source.mkString finally source.close()
  else
    println(s"[WARN] '$inputFileName' not found at project root. Using fallback test string.")
    """
    // Default fallback sample code
    var score = 10.5;
    if (score >= 10 && true) {
      val message = "Hello, \"World\"!";
      return false;
    }
    """

  // Initialize your team's stream and lexer engine
  val stream = new CharacterStream(sourceCode)
  val lexer = new Lexer(stream)

  // Extract all tokens until EOF is reached
  val tokenList = ListBuffer[Token]()
  var token = lexer.nextToken()
  tokenList += token

  while token.tokenType != TokenType.EOF do
    token = lexer.nextToken()
    tokenList += token

  // Generate CSV Data
  val csvHeader = "Token Type,Lexeme,Line,Column,Start Offset,End Offset\n"
  
  val csvRows = tokenList.map { t =>
    val tokenType = t.tokenType.toString
    
    // Pattern match to extract data by slot positioning
    val (rawText, spanData) = t match
      case Token(_, textStr, s) => (textStr, s)

    val escapedLexeme = escapeForCsv(rawText)
    
    // Pull positioning details out of the extracted span data
    val line   = spanData.start.line
    val col    = spanData.start.column
    val startO = spanData.start.offset
    val endO   = spanData.end.offset

    s"$tokenType,$escapedLexeme,$line,$col,$startO,$endO"
  }.mkString("\n")

  val completeCsvData = csvHeader + csvRows

  // Save data to file
  val outputFile = new File(outputFileName)
  val writer = new PrintWriter(outputFile)
  try
    writer.write(completeCsvData)
    println(s"[SUCCESS] Successfully lexed source code and saved report to: ${outputFile.getAbsolutePath}")
  finally
    writer.close()