from fastapi import FastAPI
from app.api import generate

app = FastAPI(
    title="Terminal-IQ Intelligence Service",
    description="Microservice providing natural-language translation to terminal commands via Gemini API",
    version="1.0.0"
)

# Include the generate router with the /api prefix
app.include_router(generate.router, prefix="/api")

@app.get("/health")
async def health():
    return {"status": "healthy"}
