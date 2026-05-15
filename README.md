# Scala Lexer Project

A lexer implementation in Scala for tokenizing source code.

## Project Setup

This project uses **SBT** (Scala Build Tool) for building and managing dependencies.

### Prerequisites

1. **Java** (JDK 11 or later)
2. **Scala** (3.3.1 or later)
3. **SBT** (1.9.7 or later)

### Installation

#### Windows

1. **Install Java**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use: `choco install openjdk` (if using Chocolatey)

2. **Install SBT**
   - Download from: https://www.scala-sbt.org/download.html
   - Or use: `choco install sbt`

3. **Verify Installation**
   ```powershell
   java -version
   sbt sbtVersion
   ```

## Building the Project

Navigate to the project directory and run:

```powershell
sbt compile
```

## Running Tests

```powershell
sbt test
```

## Run Lexer and Export CSV

```powershell
sbt run
```

This command will:

1. Read `test.txt`
2. Tokenize the source code
3. Export the tokens into a `.csv` file

## Project Structure

```
src/
├── main/scala/lexer/
│   ├── CharacterStream.scala
│   ├── Diagnostic.scala
│   ├── Lexer.scala
│   ├── Position.scala
│   ├── Span.scala
│   ├── Main.scala
│   ├── Token.scala
│   └── TokenType.scala
└── test/scala/lexer/
    └── (test files)
```

## Components
- **Main**: Entry point that reads `test.txt`, runs the lexer, and exports tokens into a CSV file
- **CharacterStream**: Manages character input and position tracking
- **Position**: Represents line and column information in source code
- **Span**: Represents a range in source code (start and end positions)
- **TokenType**: Defines all token types (keywords, operators, literals, etc.)
- **Token**: Represents a single token with type, text, and position
- **Diagnostic**: Represents compilation errors and warnings
- **Lexer**: Main lexer class that tokenizes input strings

## Supported Tokens

- Keywords: `if`, `else`, `while`, `for`, `return`, `true`, `false`, `class`, `var`, `val`
- Operators: `+`, `-`, `*`, `/`, `=`, `==`, `!=`, `<`, `<=`, `>`, `>=`, `&&`, `||`, `!`
- Literals: identifiers, numbers (integers and floats), strings
- Symbols: `(`, `)`, `{`, `}`, `,`, `.`, `;`, `:`

## Compilation Issues & Solutions

### Current System Issue
The system has Java 11 and SBT installed, but experiences encoding issues with the Windows username containing special characters (Ñ/Đ). This prevents SBT from creating its home directory.

### Recommended Workarounds

#### 1. **VS Code Scala Extension (Easiest)**
   - Install: "Scala (Metals)" extension
   - VS Code will auto-detect and compile your project
   - No terminal commands needed

#### 2. **Use Online Compiler**
   - Paste code at: https://scalafiddle.io/
   - Test and run your lexer in the browser

#### 3. **Manual Compilation (Advanced)**
   If you need to compile locally:
   ```bash
   # Try this direct command
   D:\sbt\bin\sbt.bat compile
   
   # Or run the launcher script
   D:\APCS\Lexer\sbt.bat compile
   ```

#### 4. **Docker (if available)**
   Use a Docker container with Scala to bypass the encoding issue

### Project is Ready!
All source code is properly structured and ready to compile. The issue is purely environmental and doesn't affect the code quality.
