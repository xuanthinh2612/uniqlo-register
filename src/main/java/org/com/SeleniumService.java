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

import java.awt.*;
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

            Thread.sleep(1000 + random.nextInt(1000));

            // 2. Chờ trang load hoàn toàn: đợi input email xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-email")));

            Thread.sleep(500 + random.nextInt(1000));

            // 3. Điền email và xác nhận email
            WebElement emailInput = driver.findElement(By.id("id-email"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", emailInput);
            moveMouseLikeHuman(emailInput);
            Thread.sleep(200 + random.nextInt(1000));
            emailInput.click();
            Thread.sleep(200 + random.nextInt(1000));
            emailInput.sendKeys(email);

            WebElement emailConfirm = driver.findElement(By.id("id-emailConfirm"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", emailConfirm);
            moveMouseLikeHuman(emailConfirm);
            Thread.sleep(200 + random.nextInt(1000));
            emailConfirm.click();
            Thread.sleep(200 + random.nextInt(1000));
            emailConfirm.sendKeys(email);


            // 4. Điền mật khẩu
            WebElement password = driver.findElement(By.id("id-password"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", password);
            moveMouseLikeHuman(password);
            Thread.sleep(200 + random.nextInt(1000));
            password.click();
            Thread.sleep(200 + random.nextInt(1000));
            password.sendKeys("Loan@1234");

            // 5. Điền mã bưu chính
            WebElement postalCodeElement = driver.findElement(By.id("id-postalCode"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", postalCodeElement);
            moveMouseLikeHuman(postalCodeElement);
            Thread.sleep(200 + random.nextInt(1000));
            postalCodeElement.click();
            Thread.sleep(200 + random.nextInt(1000));
            postalCodeElement.sendKeys(postalCode);

            // 6. Điền ngày sinh
            WebElement birthday = driver.findElement(By.id("id-birthday"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", birthday);
            moveMouseLikeHuman(birthday);
            Thread.sleep(200 + random.nextInt(1000));
            birthday.click();
            Thread.sleep(200 + random.nextInt(1000));
            birthday.sendKeys("19970731");

            // 7. Click vào label (class fr-ec-cursor-pointer fr-ec-label--standard)

            // mô phỏng cuộn
            String script =
                    "let event = new WheelEvent('wheel', { deltaY: 100, bubbles: true });" +
                            "window.dispatchEvent(event);";
            ((JavascriptExecutor) driver).executeScript(script);

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
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", agreementLabel);
            moveMouseLikeHuman(agreementLabel);
            Thread.sleep(500 + random.nextInt(1000));
            agreementLabel.click();

            Thread.sleep(500 + random.nextInt(1000));

            // 9. Click nút submit form (lớn)
            WebElement submitBtn = driver.findElement(By.cssSelector(
                    "button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal.fr-ec-mt-spacing-06"));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", submitBtn);
            moveMouseLikeHuman(submitBtn);
            Thread.sleep(500 + random.nextInt(1000));

            submitBtn.click();

            // 10. Chờ 3s để chuyển trang
            Thread.sleep(2000 + random.nextInt(1000));

            // 11. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmBtn);
            moveMouseLikeHuman(confirmBtn);
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
                        order("Ｈａｎａ", "－Ｈａｎａ", "ハナ",
                                "ハナ", "１－５－３", "ハイツ白鷺３０２", "07090753090");
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

            // 2. Click chọn kupon
            List<WebElement> openModalBtns = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("button.fr-ec-link-cell.fr-ec-link-cell__variant-large.fr-ec-cursor-pointer")));
            Thread.sleep(2000 + random.nextInt(1000));

            if (!openModalBtns.isEmpty()) {
                WebElement openModalBtn = openModalBtns.get(0); // Lấy button đầu tiên
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", openModalBtn);


                wait.until(ExpectedConditions.elementToBeClickable(openModalBtn)); // Đảm bảo button sẵn sàng click
                moveMouseLikeHuman(openModalBtn);
                Thread.sleep(500 + random.nextInt(1000));
                openModalBtn.click();
            } else {
                System.out.println("Khong tim thay button chon cupon.");
            }


            Thread.sleep(1000 + random.nextInt(1000));

            // 3. Click chọn kupon label for chứa text bắt đầu bằng 3019379320593-X
            // Tìm label có attribute for chứa chuỗi này (phần sau có thể khác)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for^='3019379320593-X']")));
            WebElement label = driver.findElement(By.cssSelector("label[for^='3019379320593-X']"));
            moveMouseLikeHuman(label);
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", label);
            Thread.sleep(500 + random.nextInt(1000));
            label.click();

            // 4. Click OK xác nhận cupon
            List<WebElement> buttons = driver.findElements(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")
            );
            Thread.sleep(500 + random.nextInt(1000));

            if (!buttons.isEmpty()) {
                buttons.get(1).click(); // Click button đầu tiên
            }

            Thread.sleep(1000 + random.nextInt(1000));

            // 5. Cuộn xuống và click nút đặt hàng (primary button lớn)
            WebElement btn2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn2);
            moveMouseLikeHuman(btn2);
            Thread.sleep(1000 + random.nextInt(1000));
            btn2.click();

            Thread.sleep(2000 + random.nextInt(1000));

            // 6. lick nút gửi về địa chỉ cá nhận
            List<WebElement> deliveryButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-selector__button.fr-ec-cursor-pointer")
            );
            if (!deliveryButtons.isEmpty()) {
                Thread.sleep(2000 + random.nextInt(1000));
                deliveryButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(3000 + random.nextInt(1000));

            // 9. Cuộn xuống, nhập thông tin vào các input theo id
            WebElement familyNameElement = driver.findElement(By.id("id-familyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", familyNameElement);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(familyNameElement);
            familyNameElement.click();
            Thread.sleep(200 + random.nextInt(1000));
            familyNameElement.sendKeys(familyName);
            Thread.sleep(200 + random.nextInt(1000));

            WebElement givenNameElement = driver.findElement(By.id("id-givenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", givenNameElement);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(givenNameElement);
            givenNameElement.click();
            Thread.sleep(200 + random.nextInt(1000));
            givenNameElement.sendKeys(givenName);
            Thread.sleep(200 + random.nextInt(1000));

            WebElement phoneticFamilyNameElement = driver.findElement(By.id("id-phoneticFamilyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticFamilyNameElement);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(phoneticFamilyNameElement);
            phoneticFamilyNameElement.click();
            Thread.sleep(200 + random.nextInt(1000));
            phoneticFamilyNameElement.sendKeys(phoneticFamilyName);
            Thread.sleep(200 + random.nextInt(1000));

            WebElement phoneticGivenNameElement = driver.findElement(By.id("id-phoneticGivenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticGivenNameElement);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(phoneticGivenNameElement);
            phoneticGivenNameElement.click();
            Thread.sleep(200 + random.nextInt(1000));
            phoneticGivenNameElement.sendKeys(phoneticGivenName);
            Thread.sleep(200 + random.nextInt(1000));

            // 10. Click nhập địa chỉ
            List<WebElement> addressInsertButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-link-text.fr-ec-link-text--standalone-secondary.fr-ec-cursor-pointer.fr-ec-button-reset")
            );
            if (!addressInsertButtons.isEmpty()) {
                Thread.sleep(500 + random.nextInt(1000));

                addressInsertButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(2000 + random.nextInt(1000));

            // 11. Nhập địa chỉ
            WebElement street1Element = driver.findElement(By.id("id-street1"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street1Element);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(street1Element);
            street1Element.click();
            Thread.sleep(200 + random.nextInt(1000));
            street1Element.sendKeys(street1);
            Thread.sleep(200 + random.nextInt(1000));

            WebElement street2Element = driver.findElement(By.id("id-street2"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street2Element);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(street2Element);
            street2Element.click();
            Thread.sleep(200 + random.nextInt(1000));
            street2Element.sendKeys(street2);
            Thread.sleep(200 + random.nextInt(1000));

            WebElement phoneElement = driver.findElement(By.id("id-phone"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneElement);
            Thread.sleep(200 + random.nextInt(1000));
            moveMouseLikeHuman(phoneElement);
            phoneElement.click();
            Thread.sleep(200 + random.nextInt(1000));
            phoneElement.sendKeys(phone);
            Thread.sleep(200 + random.nextInt(1000));

            // 12. Click label for unattendedDeliveryOption-FRONTDOOR-1
            WebElement unattendedLabel = driver.findElement(By.cssSelector("label[for='unattendedDeliveryOption-FRONTDOOR-1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", unattendedLabel);
            Thread.sleep(500 + random.nextInt(1000));
            moveMouseLikeHuman(unattendedLabel);
            unattendedLabel.click();
            Thread.sleep(500 + random.nextInt(1000));

            // 13. Click nút Tiến hành thanh toán
            WebElement nextBtn1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-cursor-pointer" +
                            ".fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn1);

            Thread.sleep(2000 + random.nextInt(1000));
            moveMouseLikeHuman(nextBtn1);
            nextBtn1.click();

            // 15. Chờ 3s, chọn button PAYPAY
            Thread.sleep(3000 + random.nextInt(1000));
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", paypayBtn);
            Thread.sleep(1000 + random.nextInt(1000));
            moveMouseLikeHuman(paypayBtn);
            paypayBtn.click();

            // 16. Chờ 3s, click button variant-primary half-width
            Thread.sleep(3000 + random.nextInt(1000));
            WebElement confirmPayBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal.shared-global-ec-uikit-mt-spacing-02")));
            Thread.sleep(2000 + random.nextInt(1000));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmPayBtn);
            moveMouseLikeHuman(confirmPayBtn);
            confirmPayBtn.click();

            // 17. Chờ 3s load trang, cuộn xuống click nút cuối cùng (primary half-width)
            Thread.sleep(3000 + random.nextInt(1000));
            WebElement finalBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            Thread.sleep(2000 + random.nextInt(1000));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", finalBtn);
            moveMouseLikeHuman(finalBtn);
            finalBtn.click();
            Thread.sleep(1000 + random.nextInt(1000));

            // 18. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)
            WebElement finalConfirmBtn =
                    driver.findElement(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal"));

            Thread.sleep(3000 + random.nextInt(1000));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalConfirmBtn);
            moveMouseLikeHuman(finalConfirmBtn);
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
        int steps = 50;
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
        Thread.sleep(500 + random.nextInt(300));

        // 7. Di chuyển ra khỏi element (300–500px ngẫu nhiên)
        int offsetX = 100 + random.nextInt(200);
        int offsetY = 100 + random.nextInt(200);
        int awayX = targetX + offsetX * (random.nextBoolean() ? 1 : -1);
        int awayY = targetY + offsetY * (random.nextBoolean() ? 1 : -1);

        // Giới hạn ra khỏi màn hình
        awayX = Math.min(Math.max(awayX, minX), maxX);
        awayY = Math.min(Math.max(awayY, minY), maxY);

        Point midStart = new Point(targetX, targetY);
        for (int i = 1; i <= steps; i++) {
            int moveX = midStart.x + (awayX - midStart.x) * i / steps;
            int moveY = midStart.y + (awayY - midStart.y) * i / steps;

            moveX = Math.min(Math.max(moveX, minX), maxX);
            moveY = Math.min(Math.max(moveY, minY), maxY);

            robot.mouseMove(moveX, moveY);
            Thread.sleep(5 + random.nextInt(5));
        }
    }

    public Point getElementScreenPosition(WebElement element) {
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
}
