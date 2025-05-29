package org.com;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SeleniumService {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SeleniumService() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // Tắt các dấu hiệu cho thấy đang dùng automation
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // Thiết lập user-agent giống như người dùng thật
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) "
                + "Chrome/124.0.0.0 Safari/537.36");

        // Thiết lập đường dẫn đến chromedriver
        driver = new ChromeDriver(options);

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

            // 3. Điền email và xác nhận email
            driver.findElement(By.id("id-email")).sendKeys(email);
            driver.findElement(By.id("id-emailConfirm")).sendKeys(email);

            // 4. Điền mật khẩu
            driver.findElement(By.id("id-password")).sendKeys("Loan@1234");

            // 5. Điền mã bưu chính
            driver.findElement(By.id("id-postalCode")).sendKeys(postalCode);

            // 6. Điền ngày sinh
            driver.findElement(By.id("id-birthday")).sendKeys("19971201");

            // 7. Click vào label (class fr-ec-cursor-pointer fr-ec-label--standard)
            WebElement label = driver.findElement(By.cssSelector("label.fr-ec-cursor-pointer.fr-ec-label--standard"));
            label.click();

            // 8. Click vào checkbox agreement
            WebElement agreementLabel = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("label[for='agreement']")
                    )
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", agreementLabel);
            agreementLabel.click();

            // 9. Click nút submit form (lớn)
            WebElement submitBtn = driver.findElement(By.cssSelector(
                    "button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal.fr-ec-mt-spacing-06"));
            submitBtn.click();

            // 10. Chờ 3s để chuyển trang
            Thread.sleep(3000);

            // 11. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", confirmBtn);

            confirmBtn.click();

            // Hoàn thành
            System.out.println("Dang ky thanh cong! Thanh toan don hang.");

        } catch (Exception ignored) {}
        finally {
            while (true) {
                try {
                    if (driver.getWindowHandles().isEmpty()) {
                        System.out.println("Trình duyệt đã bị đóng. Kết thúc chương trình.");
                        driver.quit(); // đảm bảo tắt driver nếu chưa
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Trình duyệt không còn hoạt động. Thoát.");
                    break;
                }
                try {
                    Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại
                } catch (Exception ignored) {}
            }
        }
    }
}
