# ⚡ Terminal-IQ

> **Terminal-IQ** is a smart, context-aware interactive terminal shell that brings Retrieval-Augmented Generation (RAG), API integrations, and storage optimizations straight to your command line.

---

## 📌 Overview

**Terminal-IQ** seamlessly bridges natural language intent with command-line execution. Instead of struggling to remember complex flags or syntax, Terminal-IQ allows developers to issue natural language prompts, automatically detects local project context (Git, Node.js, Python, Java, Docker), applies safety checks before execution, and executes commands within an intuitive interactive shell.

Key focus areas & improvements include:
- **RAG & Context Intelligence**: Environment context and shell awareness.
- **API Integrations**: Seamless connection to Python FastAPI & Gemini API endpoints.
- **Storage Optimization**: Fast SQLite-backed history tracking and custom command storage.

---

## ✨ Key Features

- 🤖 **AI Command Translation**: Translates natural language queries (e.g., `ai check modified files and commit them`) into precise shell commands powered by Google Gemini API.
- 🔍 **Smart Context Engine**: Automatically inspects workspace directories to detect environment contexts (Git repositories, Node.js packages, Python virtualenvs, Java Maven projects, Docker setups).
- 🛡️ **Built-in Safety Engine**: Evaluates generated or entered commands against safety rules to prevent accidental destructive actions (e.g., recursive directory deletion, force pushing).
- ⚡ **Custom Command Registry & Engine**: Store, manage, and execute custom multi-step commands and scripts stored in SQLite.
- 📜 **Persistent History & Storage**: Tracks command execution history and custom command metadata in an optimized local SQLite database (`terminal-iq.db`).
- 💻 **Interactive Shell**: Rich CLI interface built with **JLine 3**, providing history navigation, dynamic prompts, built-in command routing, and interactive safety confirmation.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Terminal-IQ CLI                      │
│                  (Java 21 / JLine 3)                    │
└──────┬──────────────────────┬────────────────────┬──────┘
       │                      │                    │
       ▼                      ▼                    ▼
┌──────────────┐     ┌──────────────────┐  ┌──────────────┐
│ Context &    │     │ SQLite Database  │  │ Safety       │
│ Detectors    │     │ (terminal-iq.db) │  │ Engine       │
└──────────────┘     └──────────────────┘  └──────────────┘
                              ▲
                              │ HTTP / REST
                              ▼
                 ┌──────────────────────────┐
                 │   Intelligence Service   │
                 │  (FastAPI / Python 3.10) │
                 └────────────┬─────────────┘
                              │
                              ▼
                   ┌──────────────────────┐
                   │  Google Gemini API   │
                   └──────────────────────┘
```

---

## 📁 Repository Structure

```text
terminal-iq/
├── terminal-core/          # Java 21 interactive CLI shell & core execution logic
│   ├── src/main/java/      # CLI, context detectors, safety engine, DB manager
│   ├── src/test/java/      # Unit tests (JUnit 5)
│   └── pom.xml             # Maven build & packaging configuration
├── intelligence-service/   # Python FastAPI service for LLM integration
│   ├── app/                # REST endpoints & Gemini REST provider
│   ├── requirements.txt    # Python dependencies
│   └── README.md           # Microservice documentation
├── terminal-iq.db          # SQLite database (history & commands)
├── LICENSE                 # Project license
└── README.md               # Main project documentation
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)** 21 or higher
- **Apache Maven** 3.8+
- **Python** 3.10+
- **Google Gemini API Key** ([Get an API Key](https://aistudio.google.com/))

---

### Step 1: Start the Intelligence Service

1. Navigate to the `intelligence-service` directory:
   ```bash
   cd intelligence-service
   ```

2. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Configure your API key in a `.env` file:
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

4. Run the FastAPI service:
   ```bash
   uvicorn app.main:app --reload --port 8000
   ```
   *The service will be available at `http://localhost:8000` (Health check: `http://localhost:8000/health`).*

---

### Step 2: Build & Run Terminal-Core

1. Navigate to `terminal-core`:
   ```bash
   cd terminal-core
   ```

2. Build the executable fat JAR:
   ```bash
   mvn clean package
   ```

3. Run the Terminal-IQ shell:
   ```bash
   java -jar target/terminal-core-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

---

## 💡 Usage

Once inside the interactive shell, you can run standard terminal commands, AI prompts, or custom commands:

- **AI Translation**: Prefix queries with `ai` or ask in plain text:
  ```bash
  terminal-iq> ai show git log for the last 3 commits
  terminal-iq> ai find all pdf files in current directory
  ```

- **Safety Checks**: Destructive commands (e.g. `rm -rf`, `git push --force`) trigger an interactive confirmation prompt:
  ```bash
  terminal-iq> rm -rf /some/directory
  ⚠️ WARNING: High risk command detected! Proceed? [y/N]:
  ```

- **Built-in & Custom Commands**:
  - `help` - Show available commands
  - `exit` - Exit the shell

---

## 🧪 Testing

Run automated unit tests for `terminal-core`:

```bash
cd terminal-core
mvn test
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
