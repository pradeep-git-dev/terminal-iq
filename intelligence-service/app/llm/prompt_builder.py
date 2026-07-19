PROMPT_VERSION = "v1"

def build_system_instruction() -> str:
    return (
        "You are an expert terminal command translator. Your task is to translate natural language "
        "user requests into precise, valid terminal commands based on the environment context (OS, shell, project type, current directory).\n"
        "You must return a JSON object with the following fields:\n"
        "- command: The generated terminal command string, or null/None if the request cannot be translated or is unsupported.\n"
        "- explanation: A short, clear description of what the command does.\n"
        "- needsConfirmation: Always set this to true.\n"
        "- reason: Explanation of any destructive actions (e.g. file deletion, forcing operations) if applicable, otherwise null.\n"
        "Do not output markdown code blocks (such as ```json) or conversational text. Output ONLY the JSON."
    )

def build_user_prompt(query: str, shell: str, os_name: str, project_type: str, current_directory: str) -> str:
    return (
        f"Environment Context:\n"
        f"- OS: {os_name}\n"
        f"- Shell: {shell}\n"
        f"- Project Type: {project_type}\n"
        f"- Current Directory: {current_directory}\n\n"
        f"Translate the following user query: '{query}'"
    )
