package org.com;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v133.network.Network;
import org.openqa.selenium.devtools.v133.network.model.Headers;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SeleniumService {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final Random random = new Random();


    public SeleniumService(WebDriver driver) {
        this.driver = driver;

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

    public void register(String email, Map<String, String> personalDataSet) {
        try {
            // 1. Mở trang đăng ký
            driver.get("https://www.uniqlo.com/jp/ja/account/registry");
            Thread.sleep(2000 + random.nextInt(500));

            // 2. Chờ trang load hoàn toàn: đợi input email xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-email")));
            Thread.sleep(1000 + random.nextInt(500));

            // 3. Điền email và xác nhận email
            WebElement emailInput = driver.findElement(By.id("id-email"));
            moveMouseLikeHuman(emailInput);
            emailInput.click();
            Thread.sleep(200 + random.nextInt(100));
            emailInput.sendKeys(email);

            WebElement emailConfirm = driver.findElement(By.id("id-emailConfirm"));
            moveMouseLikeHuman(emailConfirm);
            emailConfirm.click();
            Thread.sleep(200 + random.nextInt(100));
            emailConfirm.sendKeys(email);


            // 4. Điền mật khẩu
            WebElement password = driver.findElement(By.id("id-password"));
            moveMouseLikeHuman(password);
            password.click();
            Thread.sleep(200 + random.nextInt(100));
            password.sendKeys("Loan@1234");

            // 5. Điền mã bưu chính
            WebElement postalCodeElement = driver.findElement(By.id("id-postalCode"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", postalCodeElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(postalCodeElement);
            postalCodeElement.click();
            Thread.sleep(200 + random.nextInt(100));
            postalCodeElement.sendKeys(personalDataSet.get("PostCode"));

            // 6. Điền ngày sinh
            WebElement birthday = driver.findElement(By.id("id-birthday"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", birthday);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(birthday);
            birthday.click();
            Thread.sleep(200 + random.nextInt(100));
            birthday.sendKeys(personalDataSet.get("birthday"));

            // 7. Click vào label (class fr-ec-cursor-pointer fr-ec-label--standard)
            humanFreeScroll();
            WebElement label = driver.findElement(By.cssSelector("label.fr-ec-cursor-pointer.fr-ec-label--standard"));
            moveMouseLikeHuman(label);
            label.click();

            Thread.sleep(500 + random.nextInt(200));

            // 8. Click vào checkbox agreement
            WebElement agreementLabel = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("label[for='agreement']")
                    )
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", agreementLabel);
            Thread.sleep(600 + random.nextInt(500));
            agreementLabel.click();

            Thread.sleep(500 + random.nextInt(200));

            // 9. Click nút submit form (lớn)
            WebElement submitBtn = driver.findElement(By.cssSelector(
                    "button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal.fr-ec-mt-spacing-06"));
            moveMouseLikeHuman(submitBtn);
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", submitBtn);
            submitBtn.click();

            // 10. Chờ 3s để chuyển trang
            Thread.sleep(1500 + random.nextInt(500));
            // cuộn xuống giả lập người dùng cuộn chuột
            humanFreeScroll();
            humanFreeScroll();

            // 11. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal")));

            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(confirmBtn);
            confirmBtn.click();

            // Hoàn thành
            System.out.println("Dang ky thanh cong! Thanh toan don hang.");

        } catch (Exception e) {
            System.out.println("Loi khi dang ky: " + e.getMessage());
        }
    }

    public void order(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                      String street1, String street2, String phone1, String phone2) {
        try {
            driver.get("https://www.uniqlo.com/jp/ja/cart");

            // 2. Click open kupon select modal
            List<WebElement> openModalBtns = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("button.fr-ec-link-cell.fr-ec-link-cell__variant-large.fr-ec-cursor-pointer")));
            Thread.sleep(1000 + random.nextInt(1000));

            if (!openModalBtns.isEmpty()) {
                WebElement openModalBtn = openModalBtns.get(0); // Lấy button đầu tiên
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", openModalBtn);
                Thread.sleep(600 + random.nextInt(500));
                wait.until(ExpectedConditions.elementToBeClickable(openModalBtn)); // Đảm bảo button sẵn sàng click
                moveMouseLikeHuman(openModalBtn);
                openModalBtn.click();
            } else {
                System.out.println("Khong tim thay button chon cupon.");
                throw new RuntimeException("Khong tim thay button chon cupon.");
            }

            Thread.sleep(1000 + random.nextInt(1000));

            // 3. Click chọn kupon label for chứa text bắt đầu bằng 3019379320593-X
            // Tìm label có attribute for chứa chuỗi này (phần sau có thể khác)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for^='3019379320593-X']")));
            WebElement cupon = driver.findElement(By.cssSelector("label[for^='3019379320593-X']"));
            moveMouseLikeHuman(cupon);
            cupon.click();

            // 4. Click OK xác nhận cupon
            List<WebElement> buttons = driver.findElements(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")
            );

            if (!buttons.isEmpty()) {
                Thread.sleep(500 + random.nextInt(500));
                buttons.get(1).click(); // Click button xác nhận khuyến mãi
            }

            Thread.sleep(1500 + random.nextInt(500));

            // 5. Cuộn xuống và click nút đặt hàng (primary button lớn)
            WebElement btn2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn2);
            Thread.sleep(1000 + random.nextInt(500));
            moveMouseLikeHuman(btn2);
            btn2.click();

            Thread.sleep(1500 + random.nextInt(1000));

            // 6. click nút gửi về địa chỉ cá nhận
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-selector__button.fr-ec-cursor-pointer")));

            List<WebElement> deliveryButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-selector__button.fr-ec-cursor-pointer")
            );
            if (!deliveryButtons.isEmpty()) {
                Thread.sleep(1000 + random.nextInt(500));
                deliveryButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(1500 + random.nextInt(500));

            // 9. Cuộn xuống, nhập thông tin vào các input theo id
            WebElement familyNameElement = driver.findElement(By.id("id-familyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", familyNameElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(familyNameElement);
            familyNameElement.click();
            familyNameElement.sendKeys(familyName);
            Thread.sleep(200 + random.nextInt(200));

            WebElement givenNameElement = driver.findElement(By.id("id-givenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", givenNameElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(givenNameElement);
            givenNameElement.click();givenNameElement.sendKeys(givenName);
            Thread.sleep(200 + random.nextInt(200));

            WebElement phoneticFamilyNameElement = driver.findElement(By.id("id-phoneticFamilyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticFamilyNameElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(phoneticFamilyNameElement);
            phoneticFamilyNameElement.click();
            phoneticFamilyNameElement.sendKeys(phoneticFamilyName);
            Thread.sleep(200 + random.nextInt(200));

            WebElement phoneticGivenNameElement = driver.findElement(By.id("id-phoneticGivenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticGivenNameElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(phoneticGivenNameElement);
            phoneticGivenNameElement.click();
            phoneticGivenNameElement.sendKeys(phoneticGivenName);
            Thread.sleep(200 + random.nextInt(200));

            // 10. Click nhập địa chỉ
            List<WebElement> addressInsertButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-link-text.fr-ec-link-text--standalone-secondary.fr-ec-cursor-pointer.fr-ec-button-reset")
            );
            if (!addressInsertButtons.isEmpty()) {
                moveMouseLikeHuman(phoneticGivenNameElement);

                Thread.sleep(1000 + random.nextInt(1000));

                addressInsertButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(2000 + random.nextInt(1000));

            // 11. Nhập địa chỉ
            WebElement street1Element = driver.findElement(By.id("id-street1"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street1Element);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(street1Element);
            street1Element.click();
            street1Element.sendKeys(street1);
            Thread.sleep(200 + random.nextInt(200));

            WebElement street2Element = driver.findElement(By.id("id-street2"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street2Element);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(street2Element);
            street2Element.click();
            street2Element.sendKeys(street2);
            Thread.sleep(200 + random.nextInt(200));

            WebElement phoneElement = driver.findElement(By.id("id-phone"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(phoneElement);
            phoneElement.click();
            phoneElement.sendKeys(phone1);
            Thread.sleep(200 + random.nextInt(200));

            WebElement phone2Element = driver.findElement(By.id("id-mobilePhone"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phone2Element);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(phone2Element);
            phone2Element.click();
            phone2Element.sendKeys(phone2);
            Thread.sleep(200 + random.nextInt(200));

            // 12. Click label for unattendedDeliveryOption-FRONTDOOR-1
            WebElement unattendedLabel = driver.findElement(By.cssSelector("label[for='unattendedDeliveryOption-FRONTDOOR-1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", unattendedLabel);
            Thread.sleep(200 + random.nextInt(100));
            moveMouseLikeHuman(unattendedLabel);
            unattendedLabel.click();
            Thread.sleep(1000 + random.nextInt(500));

            // 13. Click nút Xác nhận thông tin
            WebElement nextBtn1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-cursor-pointer" +
                            ".fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn1);
            Thread.sleep(2000 + random.nextInt(500));
            moveMouseLikeHuman(nextBtn1);

            nextBtn1.click(); // chuyển trang

            // 15. Chờ 3s, chọn button PAYPAY
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            Thread.sleep(2000 + random.nextInt(1000));
            moveMouseLikeHuman(paypayBtn);
            paypayBtn.click();

            // 16. Chờ 3s, click button confirm paypay

            WebElement confirmPayBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal.shared-global-ec-uikit-mt-spacing-02")));
            Thread.sleep(1000 + random.nextInt(1000));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmPayBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(confirmPayBtn);
            confirmPayBtn.click();

            Thread.sleep(2000 + random.nextInt(1000));

            // Chuyển trang xác nhận thông tin
            // 17. Chờ 3s load trang xác nhận thông tin, cuộn xuống click nút Đặt hàng
            WebElement finalBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            Thread.sleep(2000 + random.nextInt(500));
            moveMouseLikeHuman(finalBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", finalBtn);
            Thread.sleep(1000 + random.nextInt(500));
            finalBtn.click();
            Thread.sleep(2000 + random.nextInt(1000));

            // 18. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)

            WebElement finalConfirmBtn =
                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal")));

            Thread.sleep(2000 + random.nextInt(1000));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalConfirmBtn);

            System.out.println("Dat hang thanh cong! Hay thanh toan don hang sau do dong trinh duyet.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void logoutAccount(WebDriver driver) {
        driver.get("https://www.uniqlo.com/jp/ja/account/registry");
        try {
            // Tìm và click nút đăng xuất
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width.fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            // Chờ trang đăng ký tải xong
            Thread.sleep(2000 + new Random().nextInt(1000));
            moveMouseLikeHuman(logoutBtn);

            logoutBtn.click();

            // Chờ 1 giây để chắc chắn đã đăng xuất
            Thread.sleep(2000 + new Random().nextInt(500));
        } catch (Exception e) {
            System.out.println("Loi khi dang xuat: " + e.getMessage());
        }
    }

    public void moveMouseLikeHuman(WebElement element) throws Exception {
        Random random = new Random();
        Robot robot = new Robot();

        // 1. Lấy vị trí element trong cửa sổ trình duyệt
        org.openqa.selenium.Point elementLoc = element.getLocation();
        org.openqa.selenium.Dimension elementSize = element.getSize();
        org.openqa.selenium.Point browserLoc = ((ChromeDriver) driver).manage().window().getPosition();
        org.openqa.selenium.Dimension browserSize = ((ChromeDriver) driver).manage().window().getSize();

        // 2. Tính tọa độ chính xác trên màn hình (giữa element)
        int targetX;
        int targetY;

        // 3. Lấy tọa độ chuột hiện tại
        Point start = MouseInfo.getPointerInfo().getLocation();

        // 4. Giới hạn target trong vùng màn hình Chrome
        int minX = browserLoc.getX() + 10;
        int minY = browserLoc.getY() + 10;
        int maxX = browserLoc.getX() + browserSize.getWidth() - 10;
        int maxY = browserLoc.getY() + browserSize.getHeight() - 10;

        Point target = getElementScreenPosition(element);
        targetX = target.x + elementSize.getWidth() / 2;
        targetY = target.y + elementSize.getHeight() / 2;
        // 5. Di chuyển chuột từ từ đến element

        int steps = 10 + random.nextInt(10); // Số bước di chuyển ngẫu nhiên từ 10 đến 20
        for (int i = 1; i <= steps; i++) {
            int moveX = start.x + (targetX - start.x) * i / steps;
            int moveY = start.y + (targetY - start.y) * i / steps;

            // Giới hạn mỗi bước
            moveX = Math.min(Math.max(moveX, minX), maxX);
            moveY = Math.min(Math.max(moveY, minY), maxY);

            robot.mouseMove(moveX, moveY);
            Thread.sleep(5 + random.nextInt(5));
        }

        // 6. Dừng lại tầm 0.5 – 1s
        Thread.sleep(300 + random.nextInt(200));
    }

    public Point getElementScreenPosition(WebElement oldElement) {
        WebElement element = refreshElement(oldElement);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String, Number> position = (Map<String, Number>) js.executeScript(
                "const rect = arguments[0].getBoundingClientRect();" +
                        "return {" +
                        "  x: rect.left + window.screenX + window.outerWidth - window.innerWidth," +
                        "  y: rect.top + window.screenY + window.outerHeight - window.innerHeight" +
                        "};", element
        );
        return new Point(position.get("x").intValue(), position.get("y").intValue());
    }

    public WebElement refreshElement(WebElement oldElement) {

        String id = oldElement.getAttribute("id");

        if (!id.isBlank()) {
            return driver.findElement(By.id(oldElement.getAttribute("id")));
        } else {
            return driver.findElement(By.cssSelector("." + oldElement.getAttribute("class").replace(" ", ".")));
        }
    }

    public void smoothHumanScroll(int scrollAmount, int duration) {
        String script = """
                    const total = arguments[0];
                    const duration = arguments[1];
                    const steps = 30;
                    const stepSize = total / steps;
                    const delay = duration / steps;
                
                    let current = 0;
                    function smoothStep() {
                        if (current < steps) {
                            const ease = Math.sin((current / steps) * (Math.PI / 2));
                            window.scrollBy(0, stepSize * ease);
                            current++;
                            setTimeout(smoothStep, delay);
                        }
                    }
                    smoothStep();
                """;
        ((JavascriptExecutor) driver).executeScript(script, scrollAmount, duration);

        // ✅ Đợi trình duyệt cuộn xong
        try {
            Thread.sleep(duration + 500); // thêm buffer 500ms để chắc chắn đã cuộn xong
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void humanFreeScroll() throws InterruptedException {
        int scrollAmount = 300 + new Random().nextInt(300);  // từ 300 đến 600px
        int duration = 500 + new Random().nextInt(100);      // từ 800ms đến 1500ms
        smoothHumanScroll(scrollAmount, duration);
    }

}
