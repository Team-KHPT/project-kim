import os
import json
import dotenv
import requests

dotenv_file = dotenv.find_dotenv()
dotenv.load_dotenv(dotenv_file)


def get_job_info_from_api(job_codes=None, job_type=None, education_lvl=None, location=None, count="20", keywords=None, sort=None):
    url = "https://oapi.saramin.co.kr/job-search"
    header = {
        "Accept": "application/json"
    }
    params = {
        "job_cd": job_codes,
        "job_type": job_type,
        "edu_lv": education_lvl,
        "loc_mcd": location,
        "count": count,
        "keywords": keywords,
        "sort": sort,
        "access-key": os.environ['SARAMIN_KEY']
    }
    if not job_codes:
        del params['job_cd']
    if not job_type:
        del params['job_type']
    if not education_lvl:
        del params['edu_lv']
    if not location:
        del params['loc_mcd']
    if not keywords:
        del params['keywords']
    if not sort:
        del params['sort']

    response = requests.get(url, headers=header, params=params)
    return json.loads(response.text)
