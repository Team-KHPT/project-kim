package com.khpt.projectkim.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimplifyJsonService {
    private static final int MAX_LENGTH = 30;

    private static String cutToLength(String string, int length) {
        if (string.length() > length) {
            while (string.charAt(length) != ',' && length > 0) {
                length--;
            }
            return string.substring(0, length);
        }
        return "";
    }

    private static Map<String, Object> simplifyJob(Job job) {
        Map<String, Object> simplifiedJob = new HashMap<>();

        simplifiedJob.put("company", job.company.detail.name);
        simplifiedJob.put("title", job.position.title);
        simplifiedJob.put("job", cutToLength(job.position.jobCode.name, MAX_LENGTH));
        simplifiedJob.put("experience_level", job.position.experienceLevel.name);
        simplifiedJob.put("experience_level_code", job.position.experienceLevel.name);
        simplifiedJob.put("salary", job.salary.name);
        simplifiedJob.put("keyword", job.keyword);

        return simplifiedJob;
    }

    public static Map<String, List<Map<String, Object>>> simplifyJobs(Root root, String experience_lvl, int maxSize) {
        List<String> experience_lvl_list = List.of(experience_lvl.split(","));

        List<Map<String, Object>> simplifiedJobs = new ArrayList<>();
        for (Job job : root.jobs.job) {
            Map<String, Object> objectMap = simplifyJob(job);
            if (experience_lvl_list.contains(objectMap.get("experience_level_code").toString())) {
                simplifiedJobs.add(simplifyJob(job));
            }
            if (simplifiedJobs.size() >= maxSize) {
                break;
            }
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("jobs", simplifiedJobs);

        return result;
    }

    private static Map<String, Object> simplifyJob2(Job job) {
        Map<String, Object> simplifiedJob = new HashMap<>();

        simplifiedJob.put("company", job.company.detail.name);
        simplifiedJob.put("title", job.position.title);
        simplifiedJob.put("url", job.url);
        simplifiedJob.put("location", job.position.location.name);
        simplifiedJob.put("salary", job.salary.name);
        simplifiedJob.put("type", job.position.jobType.name);
        simplifiedJob.put("education", job.position.requiredEducationLevel.name);
        simplifiedJob.put("experience_level", job.position.experienceLevel.name);
        simplifiedJob.put("experience_level_code", job.position.experienceLevel.code);
        simplifiedJob.put("keyword", job.keyword);

        return simplifiedJob;
    }

    public static Map<String, List<Map<String, Object>>> simplifyJobs2(Root root, String experience_lvl, int maxSize) {
        List<String> experience_lvl_list = List.of(experience_lvl.split(","));

        List<Map<String, Object>> simplifiedJobs = new ArrayList<>();
        for (Job job : root.jobs.job) {
            Map<String, Object> objectMap = simplifyJob2(job);
            if (experience_lvl_list.contains(objectMap.get("experience_level_code").toString())) {
                simplifiedJobs.add(objectMap);
            }
            if (simplifiedJobs.size() >= maxSize) {
                break;
            }
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("jobs", simplifiedJobs);

        return result;
    }

    @Getter
    @Setter
    public static class Detail {
        public String href;
        public String name;
    }

    @Getter
    @Setter
    public static class Company {
        public Detail detail;
    }

    @Getter
    @Setter
    public static class Location {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    public static class JobType {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    public static class JobCode {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    public static class ExperienceLevel {
        public int code;
        public int min;
        public int max;
        public String name;
    }

    @Getter
    @Setter
    public static class Salary {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Job {
        public String url;
        public int active;
        public Company company;
        public Position position;
        public String keyword;
        public Salary salary;
        public String id;
        // more fields as per your json structure
    }

    @Getter
    @Setter
    public static class Jobs {
        public int count;
        public int start;
        public String total;
        public List<Job> job;
    }

    @Getter
    @Setter
    public static class Root {
        public Jobs jobs;
    }

    public static class Industry {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    public static class JobMidCode {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    public static class RequiredEducationLevel {
        public String code;
        public String name;
    }

    @Getter
    @Setter
    public static class Position {
        public String title;

        public Industry industry;

        public Location location;

        @JsonProperty("job-type")
        public JobType jobType;

        @JsonProperty("job-mid-code")
        public JobMidCode jobMidCode;

        @JsonProperty("job-code")
        public JobCode jobCode;

        @JsonProperty("experience-level")
        public ExperienceLevel experienceLevel;

        @JsonProperty("required-education-level")
        public RequiredEducationLevel requiredEducationLevel;
    }
}
