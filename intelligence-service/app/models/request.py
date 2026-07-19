from pydantic import BaseModel

class AIRequestModel(BaseModel):
    requestId: str
    query: str
    shell: str
    os: str
    projectType: str
    currentDirectory: str
