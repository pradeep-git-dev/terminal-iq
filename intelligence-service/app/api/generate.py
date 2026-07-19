from fastapi import APIRouter, HTTPException, status
from app.models.request import AIRequestModel
from app.models.response import AIResponseModel
from app.llm import provider

router = APIRouter()

@router.post("/generate", response_model=AIResponseModel)
async def generate(request: AIRequestModel):
    try:
        # Generate the command using the configured LLM provider
        result = await provider.generate_command(
            query=request.query,
            shell=request.shell,
            os_name=request.os,
            project_type=request.projectType,
            current_directory=request.currentDirectory
        )
        
        return AIResponseModel(
            command=result.get("command"),
            explanation=result.get("explanation"),
            needsConfirmation=result.get("needsConfirmation", True),
            reason=result.get("reason"),
            promptVersion=result.get("promptVersion")
        )
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )
