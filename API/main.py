import os

from fastapi import FastAPI
import openai
import requests
import json
import dotenv

dotenv_file = dotenv.find_dotenv()
dotenv.load_dotenv(dotenv_file)

app = FastAPI()


def simplify_job(job):
    new_format = {
        'company': job['company']['detail']['name'],
        'position': {
            'title': job['position']['title'],
            'job': job['position']['job-code']['name'],
            'experience-level': job['position']['experience-level']['name'],
        },
        'salary': job['salary']['name'],
        'keyword': job['keyword']
    }

    max_len = 30
    job_content = str(new_format['position']['job'])
    if len(job_content) > max_len:
        idx = max_len
        while job_content[idx] != ",":
            idx -= 1
        job_content = job_content[:idx]
    new_format['position']['job'] = job_content

    return new_format


def simplify_job2(job):
    return {
        'active': job['active'],
        'company': job['company']['detail']['name'],
        'position': {
            'title': job['position']['title'],
            'location': job['position']['location']['name'],
            'job-type': job['position']['job-type']['name'],
            'job-category': job['position']['job-mid-code']['name'],
            'job': job['position']['job-code']['name'],
            'experience-level': job['position']['experience-level']['name'],
            'required-education-level': job['position']['required-education-level']['name'],
        },
        'salary': job['salary']['name'],
        'keyword': job['keyword']
    }


def simplify_jobs(data):
    return {'jobs': [simplify_job(job) for job in data['jobs']['job']]}


def get_current_weather(location, unit="fahrenheit"):
    """Get the current weather in a given location"""
    weather_info = {
        "location": location,
        "temperature": "72",
        "unit": unit,
        "forecast": ["sunny", "windy"],
    }
    return json.dumps(weather_info)


def get_job_info_from_api(job_codes="", job_type="", education_lvl="", location=""):
    url = "https://oapi.saramin.co.kr/job-search"
    params = {
        "job_cd": job_codes,
        "job_type": job_type,
        "edu_lv": education_lvl,
        "loc_mcd": location,
        "count": "40",
        "access-key": os.environ['SARAMIN_KEY']
    }

    response = requests.get(url, params=params)
    jobs = json.loads(response.text)

    # Simplify the data
    simplified_data = simplify_jobs(jobs)

    print(simplified_data)
    print(str(simplified_data).replace("'", ""))

    # Convert back to JSON
    simplified_json = json.dumps(simplified_data, indent=4)
    print(simplified_json)
    return simplified_data


def run_conversation():
    # Step 1: send the conversation and available functions to GPT
    messages = [{"role": "user", "content": "What's the weather like in Boston?"}]
    functions = [
        {
            "name": "get_current_weather",
            "description": "Get the current weather in a given location",
            "parameters": {
                "type": "object",
                "properties": {
                    "location": {
                        "type": "string",
                        "description": "The city and state, e.g. San Francisco, CA",
                    },
                    "unit": {"type": "string", "enum": ["celsius", "fahrenheit"]},
                },
                "required": ["location"],
            },
        }
    ]
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo-0613",
        messages=messages,
        functions=functions,
        function_call="auto",  # auto is default, but we'll be explicit
    )
    response_message = response["choices"][0]["message"]

    # Step 2: check if GPT wanted to call a function
    if response_message.get("function_call"):
        # Step 3: call the function
        # Note: the JSON response may not always be valid; be sure to handle errors
        available_functions = {
            "get_current_weather": get_current_weather,
        }  # only one function in this example, but you can have multiple
        function_name = response_message["function_call"]["name"]
        fuction_to_call = available_functions[function_name]
        function_args = json.loads(response_message["function_call"]["arguments"])
        function_response = fuction_to_call(
            location=function_args.get("location"),
            unit=function_args.get("unit"),
        )

        # Step 4: send the info on the function call and function response to GPT
        messages.append(response_message)  # extend conversation with assistant's reply
        messages.append(
            {
                "role": "function",
                "name": function_name,
                "content": function_response,
            }
        )  # extend conversation with function response
        second_response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo-0613",
            messages=messages,
        )  # get a new response from GPT where it can see the function response
        return second_response


@app.get("/")
async def root():

    return get_job_info_from_api()


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}
