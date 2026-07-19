import json
import httpx
from app import config
from app.llm import prompt_builder

async def generate_command(query: str, shell: str, os_name: str, project_type: str, current_directory: str) -> dict:
    if not config.GEMINI_API_KEY:
        raise ValueError(
            "Gemini API Key is not configured. "
            "Please create a .env file in the 'intelligence-service' directory "
            "and set GEMINI_API_KEY=your_api_key_here."
        )

    system_instruction = prompt_builder.build_system_instruction()
    user_prompt = prompt_builder.build_user_prompt(query, shell, os_name, project_type, current_directory)

    # Call the Google Gemini REST API generateContent endpoint
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{config.MODEL_NAME}:generateContent?key={config.GEMINI_API_KEY}"
    
    headers = {
        "Content-Type": "application/json"
    }

    payload = {
        "contents": [
            {
                "role": "user",
                "parts": [
                    {"text": user_prompt}
                ]
            }
        ],
        "systemInstruction": {
            "parts": [
                {"text": system_instruction}
            ]
        },
        "generationConfig": {
            "responseMimeType": "application/json",
            "responseSchema": {
                "type": "OBJECT",
                "properties": {
                    "command": {
                        "type": "STRING",
                        "description": "The translated terminal command, or null/empty if translation is not possible."
                    },
                    "explanation": {
                        "type": "STRING",
                        "description": "A concise explanation of the command."
                    },
                    "needsConfirmation": {
                        "type": "BOOLEAN",
                        "description": "Should always be true."
                    },
                    "reason": {
                        "type": "STRING",
                        "description": "Reason for safety concerns, if any."
                    }
                },
                "required": ["command", "explanation", "needsConfirmation"]
            }
        }
    }

    async with httpx.AsyncClient(timeout=15.0) as client:
        response = await client.post(url, json=payload, headers=headers)
        
    if response.status_code != 200:
        raise RuntimeError(f"Gemini API returned status {response.status_code}: {response.text}")
        
    response_data = response.json()
    try:
        candidate = response_data["candidates"][0]
        text_content = candidate["content"]["parts"][0]["text"]
        result = json.loads(text_content.strip())
        # Inject prompt version
        result["promptVersion"] = prompt_builder.PROMPT_VERSION
        return result
    except (KeyError, IndexError, ValueError, json.JSONDecodeError) as e:
        raise ValueError(f"Failed to parse JSON response from Gemini API. Error: {e}. Raw response: {response_data}")
