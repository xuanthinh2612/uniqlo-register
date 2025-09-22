package org.com;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    public void register(String email, Map<String, String> personalDataSet) throws Exception {
        try {
            // 1. Mở trang đăng ký
            driver.get("https://www.uniqlo.com/jp/ja/account/registry");

            // 2. Chờ trang load hoàn toàn: đợi input email xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("id-email")));
            Thread.sleep(1000 + random.nextInt(500));

            // 3. Điền email và xác nhận email
            WebElement emailInput = driver.findElement(By.id("id-email"));
            clickElementByJs(emailInput);
            sendKeyByJs(emailInput, email);

            WebElement emailConfirm = driver.findElement(By.id("id-emailConfirm"));
            clickElementByJs(emailConfirm);
            sendKeyByJs(emailConfirm, email);

            // 4. Điền mật khẩu
            WebElement password = driver.findElement(By.id("id-password"));
            clickElementByJs(password);
            sendKeyByJs(password, "Loan@1234");

            // 5. Điền mã bưu chính
            WebElement postalCodeElement = driver.findElement(By.id("id-postalCode"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", postalCodeElement);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(postalCodeElement);
            sendKeyByJs(postalCodeElement, personalDataSet.get("PostCode"));

            // 6. Điền ngày sinh
            WebElement birthday = driver.findElement(By.id("id-birthday"));
            clickElementByJs(birthday);
            sendKeyByJs(birthday, personalDataSet.get("birthday"));

            // 7. Click vào label giới tính (class fr-ec-cursor-pointer fr-ec-label--standard)
            humanFreeScroll();
            WebElement label = driver.findElement(By.cssSelector("label.fr-ec-cursor-pointer.fr-ec-label--standard"));
            clickElementByJs(label);

            // 8. Click vào checkbox agreement
            WebElement agreementLabel = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("label[for='agreement']")
                    )
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", agreementLabel);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(agreementLabel);

            // 9. Click nút submit form (lớn)
            By submitBtnBy = By.xpath("//button[contains(.,'確認画面へ')]");
            WebElement submitBtn = driver.findElement(submitBtnBy);
            clickElementByJs(submitBtn);

            // 10. Chờ 3s để chuyển trang
            Thread.sleep(1000 + random.nextInt(500));

            // 11. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
            By confirmInfoBtnBy = By.xpath("//button[contains(.,'認証コードを送る')]");
            WebElement confirmInfoBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmInfoBtnBy));

            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmInfoBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(confirmInfoBtn);

            // Hoàn thành
            System.out.println("Dang ky thanh cong! Vui long dat hang.");

        } catch (Exception e) {
            throw new Exception("Loi khi dang ky: " + e.getMessage());
        }

    }

    public void addOneProductToCart(WebDriver driver, List<String> productsDetailList) throws Exception {
        for (int i = 0; i + 1 < productsDetailList.size(); i += 5) {
            try {
                String url = productsDetailList.get(i + 4);

                String amountString = productsDetailList.get(i + 1);
                int amount = amountString != null && !amountString.isEmpty() ? Integer.parseInt(amountString) : 1;  // Mặc định là 1 nếu không có số lượng

                String size = productsDetailList.get(i + 2).toUpperCase();
                String color = productsDetailList.get(i + 3).toUpperCase();
                addOneProductToCartAction(driver, url, amount, size, color);
            } catch (Exception e) {
                throw new Exception("Loi khi them san pham vao gio hang: " + e.getMessage());
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
            clickElementByJs(colorButton);

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
                clickElementByJs(sizeButton);
            }

            // 4. click thêm số lượng
            // Lấy span hiển thị số lượng
            WebElement quantitySpan = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("span.counter__value")
            ));
            int currentQuantity = Integer.parseInt(quantitySpan.getText().trim());

            // Lấy nút tăng
            WebElement increaseBtn = driver.findElement(By.xpath("//button[contains(@aria-label,'Increase')]"));

            // Click tăng tới khi đủ số lượng
            while (currentQuantity < amount) {
                clickElementByJs(increaseBtn);
                // Đợi 0.3s cho số lượng update
                Thread.sleep(300 + random.nextInt(200));
                currentQuantity = Integer.parseInt(quantitySpan.getText().trim());
            }

            // 3. Click nút thêm vào giỏ hàng
            WebElement addToCartBtn = driver.findElement(By.xpath("//button[contains(.,'カートに入れる')]"));
            clickElementByJs(addToCartBtn);

            // 4. Chờ 1s để chắc chắn đã thêm vào giỏ hàng
            Thread.sleep(2000 + random.nextInt(500));

        } catch (Exception e) {
            System.out.println("Loi khi them san pham vao gio hang: " + e.getMessage());
            throw e;
        }

    }

    public void order(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                      String street1, String street2, String phone1, String phone2) throws Exception {
        try {
            driver.get("https://www.uniqlo.com/jp/ja/cart");

            // 1. Click open kupon select modal
            By openModalkuponBy = By.xpath("//button[contains(.,'クーポン')]");
            WebElement openModalBtn = wait.until(ExpectedConditions.elementToBeClickable(openModalkuponBy));
            Thread.sleep(500 + random.nextInt(500));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", openModalBtn);
            Thread.sleep(600 + random.nextInt(500));
            wait.until(ExpectedConditions.elementToBeClickable(openModalBtn)); // Đảm bảo button sẵn sàng click
            clickElementByJs(openModalBtn);

            Thread.sleep(1000 + random.nextInt(500));

            // 2. Click chọn kupon label for chứa text bắt đầu bằng 3019379320593-X
            // Tìm label có attribute for chứa chuỗi này (phần sau có thể khác)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for^='3019379320593-X']")));
            WebElement cupon = driver.findElement(By.cssSelector("label[for^='3019379320593-X']"));
            clickElementByJs(cupon);

            // 3. Click OK xác nhận cupon
            By kuponConfirmBy = By.xpath("//button[contains(.,'適用')]");
            WebElement kuponConfirmBtn = driver.findElement(kuponConfirmBy);
            Thread.sleep(500 + random.nextInt(500));
            clickElementByJs(kuponConfirmBtn); // Click button xác nhận khuyến mãi

            Thread.sleep(1000 + random.nextInt(500));

            // 4. Cuộn xuống và click nút đặt hàng (primary button lớn)
            By orderBtnBy = By.xpath("//button[contains(.,'購入手続きへ')]");
            WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(orderBtnBy));

            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", orderBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(orderBtn);

            Thread.sleep(1000 + random.nextInt(1000));

            // 5. click nút gửi về địa chỉ cá nhận
            By orderToStockBy = By.xpath("//button[contains(.,'指定住所受取り')]");
            wait.until(ExpectedConditions.elementToBeClickable(orderToStockBy));

            List<WebElement> deliveryButtons = driver.findElements(orderToStockBy);
            if (!deliveryButtons.isEmpty()) {
                Thread.sleep(300 + random.nextInt(300));
                clickElementByJs(deliveryButtons.get(0));// Click button đầu tiên
            }

            Thread.sleep(1000 + random.nextInt(500));

            // 6. Cuộn xuống, nhập thông tin vào các input theo id
            WebElement familyNameElement = driver.findElement(By.id("id-familyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", familyNameElement);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(familyNameElement);
            sendKeyByJs(familyNameElement, familyName);

            WebElement givenNameElement = driver.findElement(By.id("id-givenName"));
            clickElementByJs(givenNameElement);
            sendKeyByJs(givenNameElement, givenName);

            WebElement phoneticFamilyNameElement = driver.findElement(By.id("id-phoneticFamilyName"));
            clickElementByJs(phoneticFamilyNameElement);
            sendKeyByJs(phoneticFamilyNameElement, phoneticFamilyName);

            WebElement phoneticGivenNameElement = driver.findElement(By.id("id-phoneticGivenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticGivenNameElement);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(phoneticGivenNameElement);
            sendKeyByJs(phoneticGivenNameElement, phoneticGivenName);

            // 7. Click nhập địa chỉ
            By addressInsertBtnBy = By.xpath("//button[contains(.,'郵便番号から住所入力')]");
            WebElement addressInsertBtn = driver.findElement(addressInsertBtnBy);
            clickElementByJs(addressInsertBtn);

            Thread.sleep(1000 + random.nextInt(500));

            // 8. Nhập địa chỉ
            WebElement street1Element = driver.findElement(By.id("id-street1"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street1Element);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(street1Element);
            sendKeyByJs(street1Element, street1);

            WebElement street2Element = driver.findElement(By.id("id-street2"));
            clickElementByJs(street2Element);
            sendKeyByJs(street2Element, street2);

            WebElement phoneElement = driver.findElement(By.id("id-phone"));
            clickElementByJs(phoneElement);
            sendKeyByJs(phoneElement, phone1);

            WebElement phone2Element = driver.findElement(By.id("id-mobilePhone"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phone2Element);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(phone2Element);
            sendKeyByJs(phone2Element, phone2);

            // 9. Click label for unattendedDeliveryOption-FRONTDOOR-1
            WebElement unattendedLabel = driver.findElement(By.cssSelector("label[for='unattendedDeliveryOption-FRONTDOOR-1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", unattendedLabel);
            Thread.sleep(200 + random.nextInt(100));
            clickElementByJs(unattendedLabel);

            // 10. Click nút Xác nhận thông tin giao hàng
            By deliveryInfoConfirmBy = By.xpath("//button[contains(.,'確定する')]");
            WebElement deliveryInfoConfirmBtn = wait.until(ExpectedConditions.elementToBeClickable(deliveryInfoConfirmBy));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", deliveryInfoConfirmBtn);
            Thread.sleep(1000 + random.nextInt(500));

            clickElementByJs(deliveryInfoConfirmBtn); // chuyển trang

            // 11. Chờ 3s, chọn button PAYPAY
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            Thread.sleep(1000 + random.nextInt(500));
            clickElementByJs(paypayBtn);

            // 12. Chờ 3s, click button confirm paypay
            Thread.sleep(1000 + random.nextInt(500));
            By confirmPayBy = By.xpath("//button[contains(.,'お支払い方法を確定する')]");
            WebElement confirmPayBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmPayBy));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmPayBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(confirmPayBtn);

            Thread.sleep(2000 + random.nextInt(500));

            // Chuyển trang xác nhận thông tin
            // 13. Chờ 3s load trang xác nhận thông tin, cuộn xuống click nút Đặt hàng
            By processOrderBtnBy = By.xpath("//button[contains(.,'注文する')]");
            WebElement finalBtn = wait.until(ExpectedConditions.elementToBeClickable(processOrderBtnBy));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", finalBtn);
            Thread.sleep(1000 + random.nextInt(500));
            clickElementByJs(finalBtn);

            // 14. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)
            WebElement finalConfirmBtn =
                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal")));

            Thread.sleep(1000 + random.nextInt(500));

            clickElementByJs(finalConfirmBtn);

            System.out.println("Dat hang thanh cong! Hay thanh toan don hang sau do dong trinh duyet.");

        } catch (Exception e) {
            throw new Exception("Loi khi thuc hien thanh toan don hang: " + e.getMessage());
        }

    }

    public void orderToShop(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                            String street1, String street2, String phone1, String phone2, String storeName) throws Exception {
        try {
            driver.get("https://www.uniqlo.com/jp/ja/cart");

            // 1. Click open kupon select modal
            By openModalkuponBy = By.xpath("//button[contains(.,'クーポン')]");
            WebElement openModalBtn = wait.until(ExpectedConditions.elementToBeClickable(openModalkuponBy));
            Thread.sleep(500 + random.nextInt(500));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", openModalBtn);
            Thread.sleep(600 + random.nextInt(500));
            wait.until(ExpectedConditions.elementToBeClickable(openModalBtn)); // Đảm bảo button sẵn sàng click
            clickElementByJs(openModalBtn);

            Thread.sleep(1000 + random.nextInt(500));

            // 2. Click chọn kupon label for chứa text bắt đầu bằng 3017125618123-X
            // Tìm label có attribute for chứa chuỗi này (phần sau có thể khác)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("label[for^='3017125618123-X']")));
            WebElement cupon = driver.findElement(By.cssSelector("label[for^='3017125618123-X']"));
            clickElementByJs(cupon);

            // 3. Click OK xác nhận cupon
            By kuponConfirmBy = By.xpath("//button[contains(.,'適用')]");
            WebElement kuponConfirmBtn = driver.findElement(kuponConfirmBy);
            Thread.sleep(500 + random.nextInt(500));
            clickElementByJs(kuponConfirmBtn); // Click button xác nhận khuyến mãi

            Thread.sleep(1000 + random.nextInt(500));

            // 4. Cuộn xuống và click nút đặt hàng (primary button lớn)
            By orderBtnBy = By.xpath("//button[contains(.,'購入手続きへ')]");
            WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(orderBtnBy));

            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", orderBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(orderBtn);

            Thread.sleep(1000 + random.nextInt(1000));

            // 5. click nút "Nhận hàng ở cửa hàng uniqlo"
            By takeProductAtShop = By.xpath("//button[contains(.,'ユニクロ店舗受取り')]");
            wait.until(ExpectedConditions.elementToBeClickable(takeProductAtShop));
            Thread.sleep(300 + random.nextInt(300));

            WebElement takeProductAtShopBtn = driver.findElement(takeProductAtShop);
            clickElementByJs(takeProductAtShopBtn);

            Thread.sleep(1000 + random.nextInt(500));

            // 6. Nhập thông tin địa chỉ shop input theo id
            // click vào ô input
            WebElement storeSearchInput = driver.findElement(By.id("storeSearch"));
            clickElementByJs(storeSearchInput);
            // send store name to input
            sendKeyByJs(storeSearchInput, storeName);
            // Nhấn Enter để tìm kiếm
            sendKeyEnterByJs(storeSearchInput);

            // 7. Chờ 2s và click chọn shop
            // chờ kết quả hiện ra
            Thread.sleep(1000 + random.nextInt(500));
            WebElement storeLabel = driver.findElement(
                    By.xpath("//label[normalize-space(text())='" + storeName + "']")
            );
            clickElementByJs(storeLabel);

            // 8. click nút "Nhận hàng ở cửa hàng đã chọn"
            By takeProductAtShopConfirm = By.xpath("//button[contains(.,'選択した店舗で受取る')]");
            wait.until(ExpectedConditions.elementToBeClickable(takeProductAtShopConfirm));
            Thread.sleep(300 + random.nextInt(300));
            WebElement takeProductAtShopConfirmBtn = driver.findElement(takeProductAtShopConfirm);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", takeProductAtShopConfirmBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(takeProductAtShopConfirmBtn);
            Thread.sleep(1000 + random.nextInt(500));

            // 9. Cuộn xuống, nhập thông tin vào các input theo id
            WebElement familyNameElement = driver.findElement(By.id("id-familyName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", familyNameElement);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(familyNameElement);
            sendKeyByJs(familyNameElement, familyName);

            WebElement givenNameElement = driver.findElement(By.id("id-givenName"));
            clickElementByJs(givenNameElement);
            sendKeyByJs(givenNameElement, givenName);

            WebElement phoneticFamilyNameElement = driver.findElement(By.id("id-phoneticFamilyName"));
            clickElementByJs(phoneticFamilyNameElement);
            sendKeyByJs(phoneticFamilyNameElement, phoneticFamilyName);

            WebElement phoneticGivenNameElement = driver.findElement(By.id("id-phoneticGivenName"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", phoneticGivenNameElement);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(phoneticGivenNameElement);
            sendKeyByJs(phoneticGivenNameElement, phoneticGivenName);

            // 10. Nhập số điện thoại
            WebElement phoneElement = driver.findElement(By.id("id-phone"));
            clickElementByJs(phoneElement);
            sendKeyByJs(phoneElement, phone1);

            WebElement phone2Element = driver.findElement(By.id("id-mobilePhone"));
            clickElementByJs(phone2Element);
            sendKeyByJs(phone2Element, phone2);

            // 11. Click button "Xác nhận phương thức gửi hàng"
            By confirmShipMethod = By.xpath("//button[contains(.,'配送方法を確定する')]");
            WebElement confirmShipMethodBtn = driver.findElement(confirmShipMethod);
            clickElementByJs(confirmShipMethodBtn);
            Thread.sleep(1000 + random.nextInt(500));

            // 12. chọn button PAYPAY
            WebElement paypayBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("PAYPAY")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", paypayBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(paypayBtn);

            // 13. Click nhập địa chỉ 郵便番号から住所入力
            By addressInsert = By.xpath("//button[contains(.,'郵便番号から住所入力')]");
            wait.until(ExpectedConditions.elementToBeClickable(addressInsert));
            WebElement addressInsertButtons = driver.findElement(addressInsert);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", addressInsertButtons);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(addressInsertButtons);
            Thread.sleep(1000 + random.nextInt(500));

            // 14. Nhập địa chỉ
            WebElement street1Element = driver.findElement(By.id("id-street1"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", street1Element);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(street1Element);
            sendKeyByJs(street1Element, street1);

            WebElement street2Element = driver.findElement(By.id("id-street2"));
            clickElementByJs(street2Element);
            sendKeyByJs(street2Element, street2);

            // 15. Chờ click button お支払い方法を確定する
            By confirmPaymentMethod = By.xpath("//button[contains(.,'お支払い方法を確定する')]");
            wait.until(ExpectedConditions.elementToBeClickable(confirmPaymentMethod));
            WebElement confirmPaymentMethodBtn = driver.findElement(confirmPaymentMethod);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmPaymentMethodBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(confirmPaymentMethodBtn);
            Thread.sleep(1000 + random.nextInt(500));

            // 16. Click nút đặt hàng 注文する
            By processOrder = By.xpath("//button[contains(.,'注文する')]");
            wait.until(ExpectedConditions.elementToBeClickable(processOrder));
            WebElement processOrderBtn = driver.findElement(processOrder);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", processOrderBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(processOrderBtn);
            Thread.sleep(1000 + random.nextInt(500));

            // 17. Modal bật lên, click nút confirm cuối cùng (variant-primary normal transform normal)
            WebElement finalConfirmBtn =
                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.fr-ec-button.fr-ec-button--large.fr-ec-button--variant-primary" +
                            ".fr-ec-cursor-pointer.fr-ec-button-max-width-reset.fr-ec-text-transform-normal.fr-ec-button--ec-renewal")));

            Thread.sleep(1000 + random.nextInt(500));

            clickElementByJs(finalConfirmBtn);

            System.out.println("Dat hang thanh cong! Hay thanh toan don hang sau do dong trinh duyet.");

        } catch (Exception e) {
            throw new Exception("Loi khi thuc hien thanh toan don hang: " + e.getMessage());

        }

    }

    public void logoutAccount(WebDriver driver) throws Exception {
        driver.get("https://www.uniqlo.com/jp/ja/account/registry");
        try {
            // Tìm và click nút đăng xuất
            By logoutBtnBy = By.xpath("//button[contains(.,'ログアウト')]");
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(logoutBtnBy));
            // Chờ trang đăng ký tải xong
            Thread.sleep(500 + new Random().nextInt(500));

            clickElementByJs(logoutBtn);

            // Chờ 1 giây để chắc chắn đã đăng xuất
            Thread.sleep(1000 + new Random().nextInt(500));
        } catch (Exception e) {
            throw new Exception("Loi khi dang xuat: " + e.getMessage());

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

    public void sendKeyByJs(WebElement element, String value) {
        String script = "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(arguments[0].__proto__, 'value').set;" +
                "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
                "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));" +
                "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));";
        ((JavascriptExecutor) driver).executeScript(script, element, value);
    }

    public void sendKeyEnterByJs(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Cách 1: Gọi trực tiếp keydown/keyup cho Enter
        js.executeScript(
                "var input = arguments[0];" +
                        "input.dispatchEvent(new KeyboardEvent('keydown', {key: 'Enter', keyCode: 13, which: 13, bubbles: true}));" +
                        "input.dispatchEvent(new KeyboardEvent('keypress', {key: 'Enter', keyCode: 13, which: 13, bubbles: true}));" +
                        "input.dispatchEvent(new KeyboardEvent('keyup', {key: 'Enter', keyCode: 13, which: 13, bubbles: true}));" +
                        "input.form && input.form.dispatchEvent(new Event('submit', {bubbles:true,cancelable:true}));",
                element
        );
    }

    public void clickElementByJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

}
