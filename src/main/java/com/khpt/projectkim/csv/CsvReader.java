package com.khpt.projectkim.csv;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CsvReader {
    public static String getDetailedJobCode(String job_mid_code) {
        String csvFile = "src/main/java/com/khpt/projectkim/csv/job_cd.csv";
        String line;
        String csvSplitBy = ",";
        StringBuilder extractedRowsString = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
            // Read the header row
            String header = br.readLine();
            String[] headers = header.split(csvSplitBy);

            // Find the column indices of 'job_mid_cd', 'code', and 'keyword'
            int jobMidCdIndex = -1;
            int codeIndex = -1;
            int keywordIndex = -1;

            for (int i = 0; i < headers.length; i++) {
                switch (headers[i]) {
                    case "job_mid_cd":
                        jobMidCdIndex = i;
                        break;
                    case "code":
                        codeIndex = i;
                        break;
                    case "keyword":
                        keywordIndex = i;
                        break;
                }
            }

            // Print the extracted rows
            log.debug("extracted row {}", extractedRowsString);


            extractedRowsString.append("code,keyword\n");

            int cnt = 0;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(csvSplitBy);
                if (row.length > jobMidCdIndex && row[jobMidCdIndex].equals(job_mid_code)) {
                    // Extract 'code' and 'keyword' columns
                    String code = row[codeIndex];
                    String keyword = row[keywordIndex];

                    // Append the extracted row to the result string
                    extractedRowsString.append(code).append(",").append(keyword).append("\n");
                    cnt++;
                }
                if (cnt == 50) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the extracted rows
        return extractedRowsString.toString();
    }
}
