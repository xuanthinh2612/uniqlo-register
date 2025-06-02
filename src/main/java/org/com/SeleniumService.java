package org.com;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v133.network.Network;
import org.openqa.selenium.devtools.v133.network.model.Headers;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SeleniumService {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final int ORDER_NOT_READY = 0;
    private final int ORDER_READY = 1;
    FileService fileService = new FileService();

    private final Random random = new Random();


    public SeleniumService() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // Tắt các dấu hiệu cho thấy đang dùng automation
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

//        // Thiết lập user-agent giống như người dùng thật
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
//                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
//
//        options.addArguments("sec-ch-ua-platform=Windows");
//        options.addArguments("sec-ch-ua-mobile=?0");
//        options.addArguments("referer=https://www.uniqlo.com/");
//        options.addArguments("sec-ch-ua=Chromium;v=137, Not;A Brand;v=24, Google Chrome;v=138");

        // Thiết lập đường dẫn đến chromedriver
        driver = new ChromeDriver(options);

        DevTools devTools = ((HasDevTools) driver).getDevTools();
        devTools.createSession();
        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Language", "ja,en-US;q=0.9,en;q=0.8");
        headers.put("sec-ch-ua", "\"Google Chrome\";v=\"138\", \"Chromium\";v=\"138\", \";Not A Brand\";v=\"99\"");
        headers.put("sec-ch-ua-platform", "\"Windows\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("referer", "https://www.uniqlo.com/");
        headers.put("origin", "https://www.uniqlo.com");
        headers.put("upgrade-insecure-requests", "1");
        headers.put("sec-fetch-site", "same-origin");
        headers.put("sec-fetch-mode", "navigate");
        headers.put("sec-fetch-user", "?1");
        headers.put("sec-fetch-dest", "document");


        devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));

        // Gỡ dấu hiệu automation (ẩn navigator.webdriver = true)
        ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        // Khởi tạo WebDriverWait với timeout 10s
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void register(String email, String postalCode) {
        try {
            // 1. Mở trang đăng ký
            driver.get("https://www.uniqlo.com/jp/ja/account/registry");

            // 2. Chờ trang load hoàn toàn: đợi input email xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-email")));

            Thread.sleep(500 + random.nextInt(1000));

            // 3. Điền email và xác nhận email
            driver.findElement(By.id("id-email")).sendKeys(email);
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-emailConfirm")).sendKeys(email);
            Thread.sleep(500 + random.nextInt(1000));

            // 4. Điền mật khẩu
            driver.findElement(By.id("id-password")).sendKeys("Loan@1234");
            Thread.sleep(500 + random.nextInt(1000));

            // 5. Điền mã bưu chính
            driver.findElement(By.id("id-postalCode")).sendKeys(postalCode);
            Thread.sleep(500 + random.nextInt(1000));

            // 6. Điền ngày sinh
            driver.findElement(By.id("id-birthday")).sendKeys("19971201");
            Thread.sleep(500 + random.nextInt(1000));

            // 7. Click vào label (class fr-ec-cursor-pointer fr-ec-label--standard)
            WebElement label = driver.findElement(By.cssSelector("label.fr-ec-cursor-pointer.fr-ec-label--standard"));
            label.click();

            Thread.sleep(500 + random.nextInt(1000));

            // 8. Click vào checkbox agreement
            WebElement agreementLabel = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("label[for='agreement']")
                    )
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", agreementLabel);
            Thread.sleep(500 + random.nextInt(1000));

            agreementLabel.click();

            Thread.sleep(500 + random.nextInt(1000));

            // 9. Click nút submit form (lớn)
            WebElement submitBtn = driver.findElement(By.cssSelector(
                    "button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal.fr-ec-mt-spacing-06"));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", submitBtn);
            Thread.sleep(500 + random.nextInt(1000));

            submitBtn.click();

            // 10. Chờ 3s để chuyển trang
            Thread.sleep(3000 + random.nextInt(1000));

            // 11. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", confirmBtn);
            Thread.sleep(500 + random.nextInt(1000));

            confirmBtn.click();

            // Hoàn thành
            System.out.println("Dang ky thanh cong! Thanh toan don hang.");

        } catch (Exception ignored) {
        } finally {
            while (true) {
                try {
                    if (driver.getWindowHandles().isEmpty()) {
                        System.out.println("Trinh Duyet da bi dong! Quit.");
                        driver.quit(); // đảm bảo tắt driver nếu chưa
                        break;
                    }

                    // Kiểm tra xem có thể tiến hành đặt hàng được chưa

                    int actionFlag = Integer.parseInt(fileService.getActionFlag());

                    if (actionFlag == ORDER_READY) {
                        System.out.println("Bat dau dat hang...");
                        fileService.resetActionFlag();
                        order("Ｔｕｏｉ", "－Ｈａｎａ", "ハナ",
                                "ハナ", "１－２０－１", "SONG19‐202", "07084051420");
                        break; // Thoát vòng lặp sau khi đặt hàng thành công
                    }

                } catch (Exception e) {
                    System.out.println("Trinh duyet da tat! Thoat chuong trinh.");
                    break;
                }
                try {
                    Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void order(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                      String street1, String street2, String phone) {
        try {
            driver.get("https://www.uniqlo.com/jp/ja/cart");

            Thread.sleep(1000 + random.nextInt(1000));

            // 2. Click chọn kupon
            List<WebElement> openModalBtns = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("button.fr-ec-link-cell.fr-ec-link-cell__variant-large.fr-ec-cursor-pointer")));
            Thread.sleep(500 + random.nextInt(1000));

            if (!openModalBtns.isEmpty()) {
                WebElement openModalBtn = openModalBtns.get(0); // Lấy button đầu tiên
                wait.until(ExpectedConditions.elementToBeClickable(openModalBtn)).click(); // Đảm bảo button sẵn sàng click
            } else {
                System.out.println("Khong tim thay button chon cupon.");
            }


            Thread.sleep(1000 + random.nextInt(1000));

            // 3. Click chọn kupon label for chứa text bắt đầu bằng 3017125618123-X
            // Tìm label có attribute for chứa chuỗi này (phần sau có thể khác)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for^='3017125618123-X']")));
            WebElement label = driver.findElement(By.cssSelector("label[for^='3017125618123-X']"));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", label);
            Thread.sleep(500 + random.nextInt(1000));
            label.click();

            Thread.sleep(1000 + random.nextInt(1000));

            // 4. Click nút (class: fr-ec-button fr-ec-button--large fr-ec-button--variant-primary ...)
            List<WebElement> buttons = driver.findElements(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")
            );
            Thread.sleep(500 + random.nextInt(1000));

            if (!buttons.isEmpty()) {
                buttons.get(1).click(); // Click button đầu tiên
            }

            Thread.sleep(3000);

            // 5. Cuộn xuống và click nút tương tự
            WebElement btn2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn2);
            Thread.sleep(1000 + random.nextInt(1000));
            btn2.click();

            Thread.sleep(1000 + random.nextInt(1000));

            // 6. Chờ nút half-width có thể click và click
            List<WebElement> deliveryButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-selector__button.fr-ec-cursor-pointer")
            );
            if (!deliveryButtons.isEmpty()) {
                deliveryButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(3000 + random.nextInt(1000));

            // 9. Cuộn xuống, nhập thông tin vào các input theo id
            ((JavascriptExecutor) driver).executeScript("document.getElementById('id-familyName').scrollIntoView(true);");
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-familyName")).sendKeys(familyName);
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-givenName")).sendKeys(givenName);
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-phoneticFamilyName")).sendKeys(phoneticFamilyName);
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-phoneticGivenName")).sendKeys(phoneticGivenName);
            Thread.sleep(500 + random.nextInt(1000));

            // 10. Click nhập địa chỉ
            List<WebElement> addressInsertButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-link-text.fr-ec-link-text--standalone-secondary.fr-ec-cursor-pointer.fr-ec-button-reset")
            );
            if (!addressInsertButtons.isEmpty()) {
                Thread.sleep(500 + random.nextInt(1000));

                addressInsertButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(500 + random.nextInt(1000));

            // 11. Nhập địa chỉ
            driver.findElement(By.id("id-street1")).sendKeys(street1);
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-street2")).sendKeys(street2);
            Thread.sleep(500 + random.nextInt(1000));

            driver.findElement(By.id("id-phone")).sendKeys(phone);
            Thread.sleep(500 + random.nextInt(1000));

            // 12. Click label for unattendedDeliveryOption-FRONTDOOR-1
            WebElement unattendedLabel = driver.findElement(By.cssSelector("label[for='unattendedDeliveryOption-FRONTDOOR-1']"));
            Thread.sleep(500 + random.nextInt(1000));
            unattendedLabel.click();
            Thread.sleep(500 + random.nextInt(1000));

            // 13. Click nút tiếp theo (primary button lớn)
            WebElement nextBtn1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-cursor-pointer" +
                            ".fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            Thread.sleep(500 + random.nextInt(1000));
            nextBtn1.click();

            // 15. Chờ 3s, chọn button PAYPAY
            Thread.sleep(3000);
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            paypayBtn.click();

            // 16. Chờ 3s, click button variant-primary half-width
            Thread.sleep(3000);
            WebElement confirmPayBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal.shared-global-ec-uikit-mt-spacing-02")));
            Thread.sleep(500 + random.nextInt(1000));
            confirmPayBtn.click();

            // 17. Chờ 3s load trang, cuộn xuống click nút cuối cùng (primary half-width)
            Thread.sleep(3000);
            WebElement finalBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            Thread.sleep(500 + random.nextInt(1000));
            finalBtn.click();
            Thread.sleep(500 + random.nextInt(1000));

            // 18. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)
            WebElement finalConfirmBtn =
                    driver.findElement(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal"));

            Thread.sleep(500 + random.nextInt(1000));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalConfirmBtn);
            System.out.println("Dat hang thanh cong! Hay thanh toan don hang sau do dong trinh duyet.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            while (true) {
                try {
                    if (driver.getWindowHandles().isEmpty()) {
                        System.out.println("Trinh Duyet da bi dong! Quit.");
                        driver.quit(); // đảm bảo tắt driver nếu chưa
                        break;
                    }

                    Thread.sleep(5000); // chờ 5 giây rồi kiểm tra lại

                } catch (Exception e) {
                    System.out.println("Trinh duyet khong con hoat dong. Thoat");
                    break;
                }
            }
        }
    }

}
