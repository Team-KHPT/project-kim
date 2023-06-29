import os

from fastapi import FastAPI
import openai
import json
import dotenv
from pydantic import BaseModel

import saramin

openai.api_key = os.environ['OPENAI_KEY']

dotenv_file = dotenv.find_dotenv()
dotenv.load_dotenv(dotenv_file)

app = FastAPI()


def get_job_info(keywords="", sort="pd"):
    # TODO 사전 정보는 따로 가죠오고, gpt한테는 keywords, sort 파라미터만 알려주기
    jobs = saramin.get_job_info_from_api(keywords="인공지능")
    print("jobs: ", jobs)

    simplified_data = saramin.simplify_jobs(jobs)
    print(simplified_data)
    print(str(simplified_data).replace("'", ""))
    return str(simplified_data).replace("'", "")


def run_conversation():
    messages = [{"role": "user", "content": "나는 백엔드 개발자로 취업을 하려해"}]
    functions = [
        {
            "name": "get_job_info",
            "description": "Search list of job posting information with given keywords.",
            "parameters": {
                "type": "object",
                "properties": {
                    "keywords": {
                        "type": "string",
                        "description": "The keyword to search for job posting information, e.g. 백엔드",
                    },
                    "sort": {
                        "type": "string",
                        "enum": ["pd", "rc", "ac"],
                        # "description": "pd:sort by date of publication, rc:sort by hits, ac:sort by number of applicants",
                    },
                },
                "required": ["keywords"],
            },
        }
    ]
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo-16k-0613",
        messages=messages,
        functions=functions,
        function_call="auto",
    )
    response_message = response["choices"][0]["message"]
    print(response_message)

    # Step 2: check if GPT wanted to call a function
    if response_message.get("function_call"):
        available_functions = {
            "get_job_info": get_job_info,
        }
        function_name = response_message["function_call"]["name"]
        function_to_call = available_functions[function_name]
        function_args = json.loads(response_message["function_call"]["arguments"])
        function_response = function_to_call(
            keywords=function_args.get("keywords"),
            sort=function_args.get("sort"),
        )

        messages.append(response_message)
        messages.append(
            {
                "role": "function",
                "name": function_name,
                "content": function_response,
            }
        )
        second_response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo-16k-0613",
            messages=messages,
        )
        return second_response


chatting_prompts = [
    {
        'role': 'system',
        'content': '''너는 취업 상담사야.

사용자가 원하는 직무를 물어봐.
직무를 알았다면 사용자가 원하는 근무형태를 물어봐. 상세하게 물어보지는 않아도 돼.
근무형태를 알았다면 사용자의 학력을 물어봐.
학력을 알았다면 취업을 원하는 지역을 물어봐.


모두 알게 되었다면 계속 대화를 이끌어 나가며 적극적으로 상대방에게 질문해야 해.'''}
]


def chatting_response(messages: list) -> str:
    messages[:0] = chatting_prompts
    completion = openai.ChatCompletion.create(
        model="gpt-3.5-turbo-16k-0613",
        temperature=0.4,
        top_p=1,
        frequency_penalty=0.9,
        presence_penalty=0.4,
        messages=messages
    )
    return completion['choices'][0]['message']['content']


@app.get("/gpt")
async def root():
    return run_conversation()


@app.get("/result")
async def result(job_codes: str = "", job_type: str = "", education_lvl: str = "", location: str = "", keyword: str = ""):

    return get_job_info()


class ChatData(BaseModel):
    role: str
    content: str


@app.post("/chat")
async def say_hello(chats: list[ChatData]):
    # Convert chats to dict
    chat_dicts = [chat.dict() for chat in chats]
    print(chat_dicts)
    return {"role": "assistant", "content": chatting_response(chat_dicts)}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}
