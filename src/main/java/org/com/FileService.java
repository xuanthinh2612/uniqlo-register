package org.com;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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

//    private static final int MAX_EMAIL_COUNT = 1;
    private static final String EMAIL_LIST = "fileInput/emailList.txt";
    private static final String DONE_LIST = "fileInput/doneList.txt";
    private static final String ACTION_FILE = "fileInput/action.txt";
    private static final String JSON_FILE = "fileInput/portUserProfileStatus.txt";
    private static final int MAX_RETRIES = 10;
    private static final String EMAIL_AND_CODE_FILE = "fileInput/emailsAndCode.txt"; // file chứa đoạn text bạn nói
    String inputFile = "fileInput/productsList.txt";
    String doneFile = "fileInput/productDoneList.txt";


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
        List<String> productDetails = new ArrayList<>();

        Path inputPath = Paths.get(inputFile);
        Path donePath = Paths.get(doneFile);

        int count = 0;

        while (count < MAX_RETRIES) {
            try {
                Thread.sleep(1000);
                // try-with-resources cho cả channel và lock
                try (FileChannel channel = FileChannel.open(
                        inputPath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.READ,
                        StandardOpenOption.WRITE
                );
                     FileLock ignored = channel.lock()  // lock exclusive
                ) {

                    List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);

                    if (!lines.isEmpty()) {
                        String firstLine = lines.get(0);
                        productDetails = Arrays.asList(firstLine.split(","));

                        // Xóa dòng đầu và ghi phần còn lại
                        lines.remove(0);
                        Files.write(inputPath, lines, StandardCharsets.UTF_8);

                        // Append vào done file
                        Files.write(
                                donePath,
                                Collections.singletonList(firstLine + System.lineSeparator()),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND
                        );
                    }

                } // channel và lock được auto close & release ở đây

                break; // thành công

            } catch (Exception e) {
                System.out.println("Loi khi doc file productsList.txt: " + e);
            }

            count++;
        }

        return productDetails;
    }


    public String getFirstEmail() {
        Path emailListPath = Paths.get(EMAIL_LIST);

        for (int retry = 0; retry < MAX_RETRIES; retry++) {
            try {
                Thread.sleep(1000);

                // Mở file với cả quyền READ và WRITE để sửa file sau khi đọc
                try (
                        FileChannel channel = FileChannel.open(
                                emailListPath,
                                StandardOpenOption.READ,
                                StandardOpenOption.WRITE
                        );

                        // LOCK độc quyền (exclusive lock) để đảm bảo chỉ 1 process vào đây
                        FileLock ignored = channel.lock()
                ) {
                    // Đọc nội dung file qua chính channel đang được lock
                    BufferedReader reader = new BufferedReader(
                            Channels.newReader(channel, StandardCharsets.UTF_8));

                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            lines.add(line.trim());
                        }
                    }

                    if (lines.isEmpty()) return null;

                    // Lấy email đầu
                    String firstEmail = lines.get(0);

                    // Xóa email đầu ra khỏi list
                    lines.remove(0);

                    // Ghi lại phần còn lại của file
                    channel.truncate(0);                 // xóa toàn bộ file
                    channel.position(0);                 // quay về đầu file
                    BufferedWriter writer = new BufferedWriter(
                            Channels.newWriter(channel, StandardCharsets.UTF_8));

                    for (String e : lines) {
                        writer.write(e);
                        writer.newLine();
                    }
                    writer.flush();

                    // Append email đã xử lý vào doneList
                    appendDoneList(firstEmail);

                    return firstEmail;
                }

            } catch (Exception e) {
                System.out.println("Retry vi loi or file dang lock: " + e.getMessage());
            }
        }

        return null;
    }

    private void appendDoneList(String email) throws Exception {
        Path donePath = Paths.get(DONE_LIST);

        // Append không cần lock
        try (BufferedWriter writer = Files.newBufferedWriter(
                donePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            writer.write(email);
            writer.newLine();
            writer.flush();
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
                System.out.println("da release profile port " + port);
                break;
            } catch (IOException | InterruptedException e) {
                System.out.println("Loi khi release port " + port + "va profile: " + e.getMessage() + "\n Thu lai lan: " + count);
            }
            count++;
        }

    }

    // Đọc file, trích xuất email:code, rồi tìm code theo email
    public static String getCodeFromFile(String emailToFind) throws Exception {
        // Đọc toàn bộ nội dung file
        String text = Files.readString(Path.of(EMAIL_AND_CODE_FILE)).toLowerCase();
        emailToFind = emailToFind.split("@")[0];

        // Regex tương tự bản Python
        Pattern pattern = Pattern.compile("(?:to|đến)\\s+([\\w+@.]+)\\s+.*?認証コード：\\s*(\\d+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        // Map lưu email:code
        Map<String, String> resultMap = new HashMap<>();

        // Duyệt các match
        while (matcher.find()) {
            String email = matcher.group(1).trim().toLowerCase();
            String code = matcher.group(2).trim().toLowerCase();
            resultMap.put(email, code);
        }
        // Trả về code tương ứng email truyền vào
        return resultMap.getOrDefault(emailToFind, null);
    }
}
