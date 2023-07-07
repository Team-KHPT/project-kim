package com.khpt.projectkim.csv;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
                if (headers[i].equals("job_mid_cd")) {
                    jobMidCdIndex = i;
                } else if (headers[i].equals("code")) {
                    codeIndex = i;
                } else if (headers[i].equals("keyword")) {
                    keywordIndex = i;
                }
            }

            // Print the extracted rows
            System.out.println(extractedRowsString);

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
