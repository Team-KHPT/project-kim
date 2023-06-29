MAX_LENGTH = 30


def cut_to_length(string, length):
    if len(string) > length:
        while string[length] != "," and length > 0:
            length -= 1
        new_string = string[:length]
        return new_string
    return ""


def simplify_job(job):
    return {
        'company': job['company']['detail']['name'],
        'title': job['position']['title'],
        'job': cut_to_length(str(job['position']['job-code']['name']), MAX_LENGTH),
        'experience-level': job['position']['experience-level']['name'],
        'salary': job['salary']['name'],
        'keyword': job['keyword']
    }
