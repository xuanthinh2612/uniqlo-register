package org.com;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private static final int MAX_EMAIL_COUNT = 1;
    private static final String EMAIL_LIST = "fileInput/emailList.txt";
    private static final String DONE_LIST  = "fileInput/doneList.txt";
    private static final String ACTION_FILE  = "fileInput/action.txt";


    public String getPostalCode() {
        String filePath = "fileInput/postalCode.txt";
        StringBuilder postalCode = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                postalCode.append(line); // Append the line to form the postal code
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());;
            return null; // Return null if an error occurs
        }

        return postalCode.toString();
    }

    public String getActionFlag() {
        StringBuilder actionCode = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(ACTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                actionCode.append(line); // Append the line to form the actionCode
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());;
            return null; // Return null if an error occurs
        }

        return actionCode.toString().trim();
    }

    public void resetActionFlag() {
        String newFlag = "0";
        try (FileWriter writer = new FileWriter(ACTION_FILE, false)) {
            // Overwrite the file content with the new flag value
            writer.write(newFlag);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> getListEmail() {
        List<String> emailList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(EMAIL_LIST))) {
            String line;
            int count = 0; // Initialize a counter to limit the number of emails read
            while ((line = reader.readLine()) != null && count < MAX_EMAIL_COUNT) { // Read only the first 10 lines
                emailList.add(line); // Append the line to form the postal code
                count++; // Increment the counter
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());;
            return null; // Return null if an error occurs
        }
        return emailList;
    }

    /**
     * Xóa các email đã xử lý khỏi emailList.txt và append chúng vào doneList.txt.
     */
    public void moveProcessedEmails(List<String> processedEmails) {
        List<String> remaining = new ArrayList<>();

        // 1. Đọc lại toàn bộ emailList, giữ lại những email không có trong processedEmails
        try (
                FileReader fr = new FileReader(EMAIL_LIST);
                BufferedReader br = new BufferedReader(fr)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !processedEmails.contains(line)) {
                    remaining.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Loi doc email list: " + e.getMessage());
            return;
        }

        // 2. Ghi đè lại emailList.txt với danh sách remaining
        try (
                FileWriter fw = new FileWriter(EMAIL_LIST, false); // false = overwrite
                BufferedWriter bw = new BufferedWriter(fw)
        ) {
            for (String email : remaining) {
                bw.write(email);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Loi ghi de email list: " + e.getMessage());
            return;
        }

        // 3. Append processedEmails vào doneList.txt
        try (
                FileWriter fwDone = new FileWriter(DONE_LIST, true); // true = append
                BufferedWriter bwDone = new BufferedWriter(fwDone)
        ) {
            for (String email : processedEmails) {
                bwDone.write(email);
                bwDone.newLine();
            }
        } catch (IOException e) {
            System.err.println("Loi ghi done list: " + e.getMessage());
        }
    }


}