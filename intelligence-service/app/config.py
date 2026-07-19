import os
from dotenv import load_dotenv

# Load local .env file in intelligence-service directory if it exists
load_dotenv(dotenv_path=os.path.join(os.path.dirname(os.path.dirname(__file__)), ".env"))

# Fetch API Key: look for GEMINI_API_KEY first, fallback to GOOGLE_API_KEY
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")

# Handle typical workspace placeholders
if GEMINI_API_KEY == "no" or GEMINI_API_KEY == "":
    GEMINI_API_KEY = None

# Model name configuration, default to gemini-2.5-flash
MODEL_NAME = os.getenv("MODEL_NAME", "gemini-2.5-flash")
