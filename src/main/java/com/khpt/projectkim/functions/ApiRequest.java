package com.khpt.projectkim.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;

public class ApiRequest {

    public enum Sort {
        PD, RC, AC;
    }

    @Getter
    public static class JobData {
        @JsonPropertyDescription("The keyword to search for job posting information, e.g. 백엔드")
        public String keyword;

        @JsonPropertyDescription("pd:sort by date of publication, rc:sort by hits, ac:sort by number of applicants")
        @JsonProperty(defaultValue = "pd")
        public Sort sort;

        @JsonPropertyDescription("A list of job_code in job_code_table separated by comma. example 1) 1,2,3,4 example 2) 43,7,12,56,12,78,45,23,2")
        public String codes;

//        @JsonProperty(value = "job_cd", required = true)
//        public String job_cd;
//
//        @JsonProperty(value = "job_type", required = true)
//        public String job_type;
//
//        @JsonProperty(value = "edu_lv", required = true)
//        public String edu_lv;
//
//        @JsonProperty(value = "loc_cd", required = true)
//        public String loc_cd;
//
//        @JsonProperty(value = "access-key", required = true)
//        public String access_key;
    }
}
