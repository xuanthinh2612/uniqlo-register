package org.com;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import java.nio.file.*;
import java.util.regex.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FileService {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final int MAX_EMAIL_COUNT = 1;
    private static final String EMAIL_LIST = "fileInput/emailList.txt";
    private static final String DONE_LIST = "fileInput/doneList.txt";
    private static final String ACTION_FILE = "fileInput/action.txt";
    private static final String JSON_FILE = "fileInput/portUserProfileStatus.txt";
    private static final int MAX_RETRIES = 5;
    private static final String EMAIL_AND_CODE_FILE = "fileInput/emailsAndCode.txt"; // file chứa đoạn text bạn nói


    public Map<String, String> getPersonalDataSet() {
        // Đọc dữ liệu từ file postalCode.txt
        // Thứ tự của các trường trong file sẽ được ánh xạ vào một Map
        List<String> personalInfoKey = new ArrayList<>(Arrays.asList("PostCode",
                "familyName", "givenName", "phoneticFamilyName", "phoneticGivenName",
                "street1", "street2", "phone1", "phone2", "birthday"));

        Map<String, String> personalData = new HashMap<>();

        String filePath = "fileInput/postalCode.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                personalData.put(personalInfoKey.get(lineNumber), line); // Store each line in the map
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null; // Return null if an error occurs
        }

        return personalData;
    }

    public String getActionFlag() {
        StringBuilder actionCode = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(ACTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                actionCode.append(line); // Append the line to form the actionCode
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null; // Return null if an error occurs
        }

        return actionCode.toString().trim();
    }

    public List<String> getProductDetails() {
        String inputFile = "fileInput/productsList.txt";
        String doneFile = "fileInput/productDoneList.txt";
        List<String> productDetails = new ArrayList<>();

        int count = 0;
        while (count < MAX_RETRIES) {
            try {
                Thread.sleep(1000);
                // Đọc toàn bộ file productsList.txt
                List<String> lines = Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8);
                if (!lines.isEmpty()) {
                    // Lấy dòng đầu tiên
                    String firstLine = lines.get(0);
                    productDetails = Arrays.asList(firstLine.split(","));
                    // Xóa dòng đầu tiên khỏi danh sách
                    lines.remove(0);
                    // Ghi đè lại file gốc (productsList.txt) với phần còn lại
                    Files.write(Paths.get(inputFile), lines);
                    // Append dòng đầu tiên vào file productsDoneList.txt
                    Files.write(Paths.get(doneFile),
                            Collections.singletonList(firstLine),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
                break; // Thoát vòng lặp nếu thành công
            } catch (Exception e) {
                System.out.println("Loi khi doc file productsList.txt: " + e.getMessage() + "\n Thu lai lan: " + count);
            }
            count++;
        }
        return productDetails;
    }

    public String getFirstEmail() {
        String line = null;
        int count = 0;
        while (count < MAX_RETRIES) {
            try {
                Thread.sleep(1000);
                List<String> lines = Files.readAllLines(Paths.get(EMAIL_LIST), StandardCharsets.UTF_8);
                line = lines.get(0).trim();
                // remove used email from the list
                List<String> removedEmails = new ArrayList<>();
                removedEmails.add(line);
                moveProcessedEmails(removedEmails);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            count++;
        }
        return line;
    }

    /**
     * Xóa các email đã xử lý khỏi emailList.txt và append chúng vào doneList.txt.
     */
    public void moveProcessedEmails(List<String> processedEmails) throws Exception {
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
            throw new Exception(e.getMessage());
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
            throw new Exception(e.getMessage());
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
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Tìm những port chưa dùng và user profile chưa dùng để khởi động Chrome
     */
    public HashMap<String, String> getAvailablePortAndUserProfile() {
        int count = 0;
        while (count < MAX_RETRIES) {
            try {
                Thread.sleep(1000);
                ObjectNode node = getFirstPortProfileAvailable();
                if (node != null) {
                    HashMap<String, String> portAndProfile = new HashMap<>();
                    String port = node.get("port").asText();
                    String userProfile = node.get("userProfileDir").asText();
                    portAndProfile.put("port", port);
                    portAndProfile.put("userProfileDir", userProfile);
                    return portAndProfile;
                } else {
                    System.out.println("Khong con port va profile nao available. Thu lai lan " + (count + 1) + " sau 5 giay.");
                    Thread.sleep(5000); // Chờ 5 giây trước khi thử lại
                }
            } catch (Exception e) {
                System.out.println("Exception khi lay port va profile available: " + e.getMessage() + "\n Thu lai lan: " + count);
            }
            count++;
        }
        return null;
    }

    /**
     * Tìm port chưa dùng đầu tiên để khởi động Chrome
     * Lấy profile available đầu tiên và set thành in_use
     */
    public ObjectNode getFirstPortProfileAvailable() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(JSON_FILE));
        List<String> updated = new ArrayList<>();
        ObjectNode chosen = null;

        for (String line : lines) {
            ObjectNode node = (ObjectNode) mapper.readTree(line);
            String status = node.get("status").asText();

            if (chosen == null && "available".equals(status)) {
                node.put("status", "in_use");
                chosen = node;
            }

            updated.add(mapper.writeValueAsString(node));
        }


        Files.write(Paths.get(JSON_FILE), updated);
        return chosen;
    }

    /**
     * release sẽ reset port status thành available
     * Release profile theo port -> chuyển in_use thành available
     */
    public void releasePortAndProfile(String port) {
        int count = 0;
        while (count < MAX_RETRIES) {
            try {
                Thread.sleep(1000);
                List<String> lines = Files.readAllLines(Paths.get(JSON_FILE));
                List<String> updated = new ArrayList<>();

                for (String line : lines) {
                    ObjectNode node = (ObjectNode) mapper.readTree(line);
                    String currentPort = node.get("port").asText();

                    if (currentPort.equals(port)) {
                        node.put("status", "available");
                    }
                    updated.add(mapper.writeValueAsString(node));
                }
                Files.write(Paths.get(JSON_FILE), updated);
                System.out.println("Đã release profile port " + port);
                break;
            } catch (IOException | InterruptedException e) {
                System.out.println("Loi khi release port " + port + "va profile: " + e.getMessage() + "\n Thu lai lan: " + count);
            }
            count++;
        }


    }

    // Đọc file, trích xuất email:code, rồi tìm code theo email
    public static String getCodeFromFile(String emailToFind) {
        try {
            // Đọc toàn bộ nội dung file
            String text = Files.readString(Path.of(EMAIL_AND_CODE_FILE));

            // Regex tương tự bản Python
            Pattern pattern = Pattern.compile("to\\s+([\\w+@.]+)\\s+.*?認証コード：\\s*(\\d+)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);

            // Map lưu email:code
            Map<String, String> resultMap = new HashMap<>();

            // Duyệt các match
            while (matcher.find()) {
                String email = matcher.group(1).trim() + "@gmail.com";
                String code = matcher.group(2).trim();
                resultMap.put(email, code);
            }

            // Trả về code tương ứng email truyền vào
            return resultMap.getOrDefault(emailToFind, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}