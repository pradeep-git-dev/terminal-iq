from typing import Optional
from pydantic import BaseModel

class AIResponseModel(BaseModel):
    command: Optional[str] = None
    explanation: str
    needsConfirmation: bool
    reason: Optional[str] = None
    promptVersion: str
