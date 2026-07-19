# Terminal-IQ Intelligence Service

This Python microservice acts as the LLM interface for Terminal-IQ, translating natural-language queries into executable terminal commands using the Gemini API.

## Requirements

- Python 3.10+
- A Google/Gemini API Key (set via `GEMINI_API_KEY` or `GOOGLE_API_KEY` environment variable, or a local `.env` file)

## Local Setup

1. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Configure API Key**:
   Create a `.env` file in this directory:
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Start the Service**:
   Run from the repository root or the service directory:
   ```bash
   uvicorn app.main:app --reload --port 8000
   ```

4. **Verify Health**:
   Open http://localhost:8000/health in your browser or curl:
   ```bash
   curl http://localhost:8000/health
   ```
