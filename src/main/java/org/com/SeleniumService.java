package org.com;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SeleniumService {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final Random random = new Random();


    public SeleniumService(WebDriver driver) {
        this.driver = driver;

        // Khởi tạo WebDriverWait với timeout 10s
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void register(String email, Map<String, String> personalDataSet) {
        try {
            // 1. Mở trang đăng ký
            driver.get("https://www.uniqlo.com/jp/ja/account/registry");

            // 2. Chờ trang load hoàn toàn: đợi input email xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-email")));
            Thread.sleep(1000 + random.nextInt(500));

            // 3. Điền email và xác nhận email
            WebElement emailInput = driver.findElement(By.id("id-email"));
            moveMouseLikeHuman(emailInput);
            emailInput.click();
            emailInput.sendKeys(email);

            WebElement emailConfirm = driver.findElement(By.id("id-emailConfirm"));
            moveMouseLikeHuman(emailConfirm);
            emailConfirm.click();
            emailConfirm.sendKeys(email);


            // 4. Điền mật khẩu
            WebElement password = driver.findElement(By.id("id-password"));
            moveMouseLikeHuman(password);
            password.click();
            password.sendKeys("Loan@1234");

            // 5. Điền mã bưu chính
            WebElement postalCodeElement = driver.findElement(By.id("id-postalCode"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", postalCodeElement);
            Thread.sleep(600 + random.nextInt(500));
            postalCodeElement.click();
            postalCodeElement.sendKeys(personalDataSet.get("PostCode"));

            // 6. Điền ngày sinh
            WebElement birthday = driver.findElement(By.id("id-birthday"));
            birthday.click();
            birthday.sendKeys(personalDataSet.get("birthday"));

            // 7. Click vào label (class fr-ec-cursor-pointer fr-ec-label--standard)
            humanFreeScroll();
            WebElement label = driver.findElement(By.cssSelector("label.fr-ec-cursor-pointer.fr-ec-label--standard"));
            moveMouseLikeHuman(label);
            label.click();

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

            // 9. Click nút submit form (lớn)
            WebElement submitBtn = driver.findElement(By.cssSelector(
                    "button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps" +
                            ".fr-ec-button--ec-renewal.fr-ec-mt-spacing-06"));
            moveMouseLikeHuman(submitBtn);
            submitBtn.click();

            // 10. Chờ 3s để chuyển trang
            Thread.sleep(1000 + random.nextInt(500));

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
            System.exit(0);
        }
    }

    public void addOneProductToCart(WebDriver driver, List<String> productsDetailList) {
        for (int i = 0; i + 1 < productsDetailList.size(); i += 5) {
            try {
                String url = productsDetailList.get(i + 4);

                String amountString = productsDetailList.get(i + 1);
                int amount = amountString != null && !amountString.isEmpty() ? Integer.parseInt(amountString) : 1;  // Mặc định là 1 nếu không có số lượng

                String size = productsDetailList.get(i + 2).toUpperCase();
                String color = productsDetailList.get(i + 3).toUpperCase();
                addOneProductToCartAction(driver, url, amount, size, color);
            } catch (Exception e) {
                System.out.println("Loi khi them san pham vao gio hang: " + e.getMessage());
                System.exit(0);
                // Nếu có lỗi, bỏ qua sản phẩm này và tiếp tục với sản phẩm tiếp theo
            }
        }
    }

    public void addOneProductToCartAction(WebDriver driver, String productUrl, int amount, String size, String color) throws Exception {
        try {

            int retryCount = 0;
            int maxRetry = 5; // tránh reload vô hạn
            // 1. Chờ trang load hoàn toàn: đợi nút thêm vào giỏ hàng xuất hiện
            By addToCartLocator = By.xpath("//button[contains(.,'カートに入れる')]");
            By registerForStockImport = By.xpath("//button[contains(.,'再入荷通知を登録する')]");

            while (retryCount < maxRetry) {
                try {
                    driver.get(productUrl);
                    // Chờ một trong hai nút
                    wait.until(ExpectedConditions.or(
                            ExpectedConditions.presenceOfElementLocated(addToCartLocator),
                            ExpectedConditions.presenceOfElementLocated(registerForStockImport)));
                    break;
                } catch (TimeoutException e) {
                    retryCount++;
                    System.out.println("Không tìm thấy nút sau 20s. Reload lần " + retryCount + "...");
                }
            }

            Thread.sleep(1000 + random.nextInt(500));

            // 2. Click chọn màu
            String colorXpath = "//button[@data-testid='ITOChip']//img[@alt='" + color + "']/ancestor::button";
            WebElement colorButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(colorXpath)));
            moveMouseLikeHuman(colorButton);
            colorButton.click();

            // 3. Chọn Size
            // Tìm button có text đúng size
            if (!size.isBlank()) {
                String sizeXpath = "//button[@data-testid='ITOChip']//div[text()='" + size + "']/ancestor::button";
                WebElement sizeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(sizeXpath)));
                // Kiểm tra xem size có bị strike không
                WebElement parentDiv = sizeButton.findElement(By.xpath("./parent::div")); // size-chip-wrapper
                boolean isSoldOut = !parentDiv.findElements(By.className("strike")).isEmpty();

                if (isSoldOut) {
                    throw new RuntimeException("Size " + size + " đã hết hàng!");
                }

                // Click chọn size
                moveMouseLikeHuman(sizeButton);
                sizeButton.click();
            }

            // 4. click thêm số lượng
            // Lấy span hiển thị số lượng
            WebElement quantitySpan = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("span.counter__value")
            ));
            int currentQuantity = Integer.parseInt(quantitySpan.getText().trim());

            // Lấy nút tăng
            WebElement increaseBtn = driver.findElement(By.xpath("//button[contains(@aria-label,'Increase')]"));

            moveMouseLikeHuman(increaseBtn);
            // Click tăng tới khi đủ số lượng
            while (currentQuantity < amount) {
                increaseBtn.click();
                // Đợi 0.3s cho số lượng update
                Thread.sleep(300 + random.nextInt(200));
                currentQuantity = Integer.parseInt(quantitySpan.getText().trim());
            }

            // 3. Click nút thêm vào giỏ hàng
            WebElement addToCartBtn = driver.findElement(By.xpath("//button[contains(.,'カートに入れる')]"));
            moveMouseLikeHuman(addToCartBtn);
            addToCartBtn.click();

            // 4. Chờ 1s để chắc chắn đã thêm vào giỏ hàng
            Thread.sleep(2000 + random.nextInt(500));

        } catch (Exception e) {
            System.out.println("Loi khi them san pham vao gio hang: " + e.getMessage());
            throw e;
        }

    }

    public void order(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                      String street1, String street2, String phone1, String phone2) {
        try {
            driver.get("https://www.uniqlo.com/jp/ja/cart");

            // 2. Click open kupon select modal
            List<WebElement> openModalBtns = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("button.fr-ec-link-cell.fr-ec-link-cell__variant-large.fr-ec-cursor-pointer")));
            Thread.sleep(500 + random.nextInt(500));

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

            Thread.sleep(1000 + random.nextInt(500));

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

            Thread.sleep(1000 + random.nextInt(500));

            // 5. Cuộn xuống và click nút đặt hàng (primary button lớn)
            WebElement btn2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn2);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(btn2);
            btn2.click();

            Thread.sleep(1000 + random.nextInt(1000));

            // 6. click nút gửi về địa chỉ cá nhận
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-selector__button.fr-ec-cursor-pointer")));

            List<WebElement> deliveryButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-selector__button.fr-ec-cursor-pointer")
            );
            if (!deliveryButtons.isEmpty()) {
                Thread.sleep(300 + random.nextInt(300));
                deliveryButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(1000 + random.nextInt(500));

            // 9. Cuộn xuống, nhập thông tin vào các input theo id
            WebElement familyNameElement = driver.findElement(By.id("id-familyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", familyNameElement);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(familyNameElement);
            familyNameElement.click();
            familyNameElement.sendKeys(familyName);

            WebElement givenNameElement = driver.findElement(By.id("id-givenName"));
            givenNameElement.click();
            givenNameElement.sendKeys(givenName);

            WebElement phoneticFamilyNameElement = driver.findElement(By.id("id-phoneticFamilyName"));
            phoneticFamilyNameElement.click();
            phoneticFamilyNameElement.sendKeys(phoneticFamilyName);

            WebElement phoneticGivenNameElement = driver.findElement(By.id("id-phoneticGivenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticGivenNameElement);
            Thread.sleep(600 + random.nextInt(500));
            phoneticGivenNameElement.click();
            phoneticGivenNameElement.sendKeys(phoneticGivenName);

            // 10. Click nhập địa chỉ
            List<WebElement> addressInsertButtons = driver.findElements(
                    By.cssSelector("button.fr-ec-link-text.fr-ec-link-text--standalone-secondary.fr-ec-cursor-pointer.fr-ec-button-reset")
            );
            if (!addressInsertButtons.isEmpty()) {
                moveMouseLikeHuman(phoneticGivenNameElement);
                addressInsertButtons.get(0).click(); // Click button đầu tiên
            }

            Thread.sleep(1000 + random.nextInt(500));

            // 11. Nhập địa chỉ
            WebElement street1Element = driver.findElement(By.id("id-street1"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street1Element);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(street1Element);
            street1Element.click();
            street1Element.sendKeys(street1);

            WebElement street2Element = driver.findElement(By.id("id-street2"));
            street2Element.click();
            street2Element.sendKeys(street2);

            WebElement phoneElement = driver.findElement(By.id("id-phone"));
            phoneElement.click();
            phoneElement.sendKeys(phone1);

            WebElement phone2Element = driver.findElement(By.id("id-mobilePhone"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phone2Element);
            Thread.sleep(600 + random.nextInt(500));
            phone2Element.click();
            phone2Element.sendKeys(phone2);

            // 12. Click label for unattendedDeliveryOption-FRONTDOOR-1
            WebElement unattendedLabel = driver.findElement(By.cssSelector("label[for='unattendedDeliveryOption-FRONTDOOR-1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", unattendedLabel);
            Thread.sleep(200 + random.nextInt(100));
            moveMouseLikeHuman(unattendedLabel);
            unattendedLabel.click();

            // 13. Click nút Xác nhận thông tin
            WebElement nextBtn1 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-cursor-pointer" +
                            ".fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn1);
            Thread.sleep(1000 + random.nextInt(500));
            moveMouseLikeHuman(nextBtn1);

            nextBtn1.click(); // chuyển trang

            // 15. Chờ 3s, chọn button PAYPAY
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            Thread.sleep(1000 + random.nextInt(500));
            moveMouseLikeHuman(paypayBtn);
            paypayBtn.click();

            // 16. Chờ 3s, click button confirm paypay

            WebElement confirmPayBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal.shared-global-ec-uikit-mt-spacing-02")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmPayBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(confirmPayBtn);
            confirmPayBtn.click();

            Thread.sleep(2000 + random.nextInt(500));

            // Chuyển trang xác nhận thông tin
            // 17. Chờ 3s load trang xác nhận thông tin, cuộn xuống click nút Đặt hàng
            WebElement finalBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", finalBtn);
            Thread.sleep(1000 + random.nextInt(500));
            moveMouseLikeHuman(finalBtn);
            finalBtn.click();

            // 18. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)

            WebElement finalConfirmBtn =
                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal")));

            Thread.sleep(1000 + random.nextInt(500));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalConfirmBtn);

            System.out.println("Dat hang thanh cong! Hay thanh toan don hang sau do dong trinh duyet.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public void orderToShop(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                      String street1, String street2, String phone1, String phone2, String storeName) {
        try {
            driver.get("https://www.uniqlo.com/jp/ja/cart");

            // 2. Click open kupon select modal
            List<WebElement> openModalBtns = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("button.fr-ec-link-cell.fr-ec-link-cell__variant-large.fr-ec-cursor-pointer")));
            Thread.sleep(500 + random.nextInt(500));

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

            Thread.sleep(1000 + random.nextInt(500));

            // 3. Click chọn kupon label for chứa text bắt đầu bằng 3017125618123-X
            // Tìm label có attribute for chứa chuỗi này (phần sau có thể khác)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for^='3017125618123-X']")));
            WebElement cupon = driver.findElement(By.cssSelector("label[for^='3017125618123-X']"));
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

            Thread.sleep(1000 + random.nextInt(500));

            // 5. Cuộn xuống và click nút đặt hàng (primary button lớn)
            WebElement btn2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", btn2);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(btn2);
            btn2.click();

            Thread.sleep(1000 + random.nextInt(1000));

            // 6. click nút "Nhận hàng ở cửa hàng uniqlo"
            By takeProductAtShop = By.xpath("//button[contains(.,'ユニクロ店舗受取り')]");
            wait.until(ExpectedConditions.elementToBeClickable(takeProductAtShop));
            Thread.sleep(300 + random.nextInt(300));

            WebElement takeProductAtShopBtn = driver.findElement(takeProductAtShop);
            moveMouseLikeHuman(takeProductAtShopBtn);
            takeProductAtShopBtn.click();

            Thread.sleep(1000 + random.nextInt(500));

            // 7. Nhập thông tin địa chỉ shop input theo id

            // copy text vào clipboard
            StringSelection selection = new StringSelection(storeName);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);

            // click vào ô input
            WebElement storeSearchInput = driver.findElement(By.id("storeSearch"));
            moveMouseLikeHuman(storeSearchInput);
            storeSearchInput.click();
            storeSearchInput.sendKeys(Keys.CONTROL, "v"); // dán trực tiếp
            // Nhấn Enter để tìm kiếm
            storeSearchInput.sendKeys(Keys.ENTER);

            // 8. Chờ 2s và click chọn shop
            // chờ kết quả hiện ra
            Thread.sleep(1000 + random.nextInt(500));
            WebElement storeLabel = driver.findElement(
                    By.xpath("//label[normalize-space(text())='" + storeName + "']")
            );
            storeLabel.click();

            // 9. click nút "Nhận hàng ở cửa hàng đã chọn"
            By takeProductAtShopConfirm = By.xpath("//button[contains(.,'選択した店舗で受取る')]");
            wait.until(ExpectedConditions.elementToBeClickable(takeProductAtShopConfirm));
            Thread.sleep(300 + random.nextInt(300));
            WebElement takeProductAtShopConfirmBtn = driver.findElement(takeProductAtShopConfirm);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", takeProductAtShopConfirmBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(takeProductAtShopConfirmBtn);
            takeProductAtShopConfirmBtn.click();
            Thread.sleep(1000 + random.nextInt(500));

            // 10. Cuộn xuống, nhập thông tin vào các input theo id
            WebElement familyNameElement = driver.findElement(By.id("id-familyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", familyNameElement);
            Thread.sleep(600 + random.nextInt(500));
            familyNameElement.click();
            familyNameElement.sendKeys(familyName);

            WebElement givenNameElement = driver.findElement(By.id("id-givenName"));
            givenNameElement.click();
            givenNameElement.sendKeys(givenName);

            WebElement phoneticFamilyNameElement = driver.findElement(By.id("id-phoneticFamilyName"));
            phoneticFamilyNameElement.click();
            phoneticFamilyNameElement.sendKeys(phoneticFamilyName);

            WebElement phoneticGivenNameElement = driver.findElement(By.id("id-phoneticGivenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticGivenNameElement);
            Thread.sleep(600 + random.nextInt(500));
            phoneticGivenNameElement.click();
            phoneticGivenNameElement.sendKeys(phoneticGivenName);

            // 11. Nhập số điện thoại
            WebElement phoneElement = driver.findElement(By.id("id-phone"));
            phoneElement.click();
            phoneElement.sendKeys(phone1);

            WebElement phone2Element = driver.findElement(By.id("id-mobilePhone"));
            phone2Element.click();
            phone2Element.sendKeys(phone2);

            // 12. Click button "Xác nhận phương thức gửi hàng"
            By confirmShipMethod = By.xpath("//button[contains(.,'配送方法を確定する')]");
            WebElement confirmShipMethodBtn = driver.findElement(confirmShipMethod);
            moveMouseLikeHuman(confirmShipMethodBtn);
            confirmShipMethodBtn.click();
            Thread.sleep(1000 + random.nextInt(500));

            // 13. chọn button PAYPAY
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", paypayBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(paypayBtn);
            paypayBtn.click();

            // 14. Click nhập địa chỉ 郵便番号から住所入力
            By addressInsert = By.xpath("//button[contains(.,'郵便番号から住所入力')]");
            wait.until(ExpectedConditions.elementToBeClickable(addressInsert));
            WebElement addressInsertButtons = driver.findElement(addressInsert);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", addressInsertButtons);
            Thread.sleep(600 + random.nextInt(500));
            addressInsertButtons.click();
            Thread.sleep(1000 + random.nextInt(500));

            // 15. Nhập địa chỉ
            WebElement street1Element = driver.findElement(By.id("id-street1"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street1Element);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(street1Element);
            street1Element.click();
            street1Element.sendKeys(street1);

            WebElement street2Element = driver.findElement(By.id("id-street2"));
            street2Element.click();
            street2Element.sendKeys(street2);

            // 16. Chờ click button お支払い方法を確定する
            By confirmPaymentMethod = By.xpath("//button[contains(.,'お支払い方法を確定する')]");
            wait.until(ExpectedConditions.elementToBeClickable(confirmPaymentMethod));
            WebElement confirmPaymentMethodBtn = driver.findElement(confirmPaymentMethod);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmPaymentMethodBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(confirmPaymentMethodBtn);
            confirmPaymentMethodBtn.click();
            Thread.sleep(1000 + random.nextInt(500));

            // 17. Click nút đặt hàng 注文する
            By processOrder = By.xpath("//button[contains(.,'注文する')]");
            wait.until(ExpectedConditions.elementToBeClickable(processOrder));
            WebElement processOrderBtn = driver.findElement(processOrder);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", processOrderBtn);
            Thread.sleep(600 + random.nextInt(500));
            moveMouseLikeHuman(processOrderBtn);
            processOrderBtn.click();
            Thread.sleep(1000 + random.nextInt(500));

            // 18. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)
            WebElement finalConfirmBtn =
                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal")));

            Thread.sleep(1000 + random.nextInt(500));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalConfirmBtn);

            System.out.println("Dat hang thanh cong! Hay thanh toan don hang sau do dong trinh duyet.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public void logoutAccount(WebDriver driver) {
        driver.get("https://www.uniqlo.com/jp/ja/account/registry");
        try {
            // Tìm và click nút đăng xuất
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary.fr-ec-button--half-width.fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-all-caps.fr-ec-button--ec-renewal")));
            // Chờ trang đăng ký tải xong
            Thread.sleep(500 + new Random().nextInt(500));
            moveMouseLikeHuman(logoutBtn);

            logoutBtn.click();

            // Chờ 1 giây để chắc chắn đã đăng xuất
            Thread.sleep(1000 + new Random().nextInt(500));
        } catch (Exception e) {
            System.out.println("Loi khi dang xuat: " + e.getMessage());
        }
    }

    private void moveMouseLikeHuman(WebElement element) throws Exception {
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

    private Point getElementScreenPosition(WebElement oldElement) {
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

    private WebElement refreshElement(WebElement oldElement) {

        String id = oldElement.getAttribute("id");

        if (!id.isBlank()) {
            return driver.findElement(By.id(oldElement.getAttribute("id")));
        } else {
            return driver.findElement(By.cssSelector("." + oldElement.getAttribute("class").replace(" ", ".")));
        }
    }

    private void smoothHumanScroll(int scrollAmount, int duration) {
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

    private void humanFreeScroll() throws InterruptedException {
        int scrollAmount = 300 + new Random().nextInt(300);  // từ 300 đến 600px
        int duration = 500 + new Random().nextInt(100);      // từ 800ms đến 1500ms
        smoothHumanScroll(scrollAmount, duration);
    }

}
