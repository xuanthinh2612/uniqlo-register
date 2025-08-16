package org.com;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    private static final int ORDER_NOT_READY = 0;
    private static final int LOGOUT_SYS = 2;
    private static final int ORDER_READY = 1;

//    static WebDriver driver = getWebDriver();
    static WebDriver driver = attachToChrome();
    static FileService fileService = new FileService();
    static SeleniumService seleniumService = new SeleniumService(driver);

    public static void main(String[] args) {
        // Đọc dữ liệu từ file
        Map<String, String> personalData = fileService.getPersonalDataSet();

        try {
            while (true) {
                String email = fileService.getFirstEmail();

                if (email == null) {
                    System.out.println("Khong co Email nao trong file!!!");
                    break; // Exit if no emails are found
                }
                seleniumService.register(email, personalData);
                System.out.println("Hoan thanh dang ky!");

                List<String> removedEmails = new ArrayList<>();
                removedEmails.add(email);
                fileService.moveProcessedEmails(removedEmails);

                waitForStartOrder(driver, personalData);
                waitForLogout(driver, seleniumService);
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.exit(0);
            throw new RuntimeException(e);
        }

        System.exit(0);
    }

    public static void waitForStartOrder(WebDriver driver, Map<String, String> personalDataSet) {
        while (true) {
            try {
                if (driver.getWindowHandles().isEmpty()) {
                    System.out.println("Trinh Duyet da bi dong! Quit.");
                    driver.quit(); // đảm bảo tắt driver nếu chưa
                    System.exit(0);
                    break;
                }

                // Kiểm tra xem có thể tiến hành đặt hàng được chưa

                int actionFlag = Integer.parseInt(fileService.getActionFlag());

                if (actionFlag == ORDER_READY) {
                    System.out.println("Bat dau dat hang...");
                    fileService.resetActionFlag();
                    String familyName = personalDataSet.get("familyName");
                    String givenName = personalDataSet.get("givenName");
                    String phoneticFamilyName = personalDataSet.get("phoneticFamilyName");
                    String phoneticGivenName = personalDataSet.get("phoneticGivenName");
                    String street1 = personalDataSet.get("street1");
                    String street2 = personalDataSet.get("street2");
                    String phone1 = personalDataSet.get("phone1");
                    String phone2 = personalDataSet.get("phone2");

                    seleniumService.order(familyName, givenName, phoneticFamilyName, phoneticGivenName, street1, street2, phone1, phone2);
                    break; // Thoát vòng lặp sau khi đặt hàng thành công
                }

                Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại

            } catch (Exception e) {
                System.out.println("Trinh duyet da tat! Thoat chuong trinh.");
                System.exit(0);
                break;
            }
        }
    }

    public static void waitForLogout(WebDriver driver, SeleniumService seleniumService) {
        while (true) {
            try {
                if (driver.getWindowHandles().isEmpty()) {
                    System.out.println("Trinh Duyet da bi dong! Quit.");
                    driver.quit(); // đảm bảo tắt driver nếu chưa
                    System.exit(0);
                    break;
                }
                // Kiểm tra xem có thể tiến hành đặt hàng được chưa

                int actionFlag = Integer.parseInt(fileService.getActionFlag());

                if (actionFlag == LOGOUT_SYS) {
                    fileService.resetActionFlag();
                    System.out.println("logout...");
                    seleniumService.logoutAccount(driver);
                    break;
                }
                Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại

            } catch (Exception e) {
                System.out.println("Trinh duyet khong con hoat dong. Thoat");
                System.exit(0);
                break;
            }
        }
    }

    public static WebDriver attachToChrome() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222"); // Cổng debug

        return new ChromeDriver(options);
    }

}
