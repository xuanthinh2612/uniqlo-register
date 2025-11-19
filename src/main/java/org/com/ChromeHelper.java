package org.com;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;

public class ChromeHelper {
    static FileService fileService = new FileService();
    static String CURRENT_PORT;

    public static WebDriver initChromeWithAttachedSelenium() {
        HashMap<String, String> portAndProfile = fileService.getAvailablePortAndUserProfile();
        String port = portAndProfile.get("port");
        String userProfileDir = portAndProfile.get("userProfileDir");

        CURRENT_PORT = port;
        ChromeHelper.startChrome(port, userProfileDir);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + port); // Cổng debug
        return new ChromeDriver(options);
    }

    public static void startChrome(String port, String userProfileDir) {
        // Đường dẫn Chrome
        String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";

        // Tham số command
        String remoteDebugPort = "--remote-debugging-port=" + port;
        String userDataDir = "--user-data-dir=" + userProfileDir;

        // Tạo process
        ProcessBuilder processBuilder = new ProcessBuilder(
                chromePath,
                remoteDebugPort,
                userDataDir
        );

        try {
            processBuilder.start();
            Thread.sleep(3000); // Chờ 3 giây để Chrome khởi động
            System.out.println("Da khoi dong google chorme port: " + port + " userProfileDir: " + userProfileDir);
        } catch (Exception e) {
            System.out.println("Exception: Loi khi khoi dong chrome voi port: " + port + " userProfileDir: " + userProfileDir);
            e.printStackTrace();
        }
    }

    public static void releasePort() {
        fileService.releasePortAndProfile(CURRENT_PORT);
    }

}
