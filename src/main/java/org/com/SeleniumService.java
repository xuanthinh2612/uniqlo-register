package org.com;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumService {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Random random = new Random();
    private String productLink = null;

    private final String UNIQLO_LINK = "https://www.uniqlo.com";

    private final UniqloSeleniumService uniqloSeleniumService;
    private final GUSeleniumService guSeleniumService;

    public SeleniumService(WebDriver driver) {
        this.driver = driver;
        this.uniqloSeleniumService = new UniqloSeleniumService(driver);
        this.guSeleniumService = new GUSeleniumService(driver);
        // Khởi tạo WebDriverWait với timeout 10s
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
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

//            WebElement emailConfirm = driver.findElement(By.id("id-emailConfirm"));
//            clickElementByJs(emailConfirm);
//            sendKeyByJs(emailConfirm, email);

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
//            WebElement agreementLabel = wait.until(
//                    ExpectedConditions.elementToBeClickable(
//                            By.cssSelector("label[for='agreement']")
//                    )
//            );
//            ((JavascriptExecutor) driver)
//                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", agreementLabel);
//            Thread.sleep(600 + random.nextInt(500));
//            clickElementByJs(agreementLabel);

            // 9. Click nút submit form (lớn)
            By submitBtnBy = By.xpath("//button[contains(.,'会員登録する')]");
            WebElement submitBtn = driver.findElement(submitBtnBy);
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", submitBtn);
            Thread.sleep(600 + random.nextInt(500));
            clickElementByJs(submitBtn);

            // 10. Chờ 3s để chuyển trang
//            Thread.sleep(1000 + random.nextInt(500));

            // 11. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
//            By confirmInfoBtnBy = By.xpath("//button[contains(.,'認証コードを送る')]");
//            WebElement confirmInfoBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmInfoBtnBy));
//
//            ((JavascriptExecutor) driver)
//                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmInfoBtn);
//            Thread.sleep(600 + random.nextInt(500));
//            clickElementByJs(confirmInfoBtn);

            // Hoàn thành
            System.out.println("Dang ky thanh cong! email: " + email);

        } catch (Exception e) {
            throw new Exception("Loi khi dang ky: " + e.getMessage());
        }

    }

    public void insertCodeToVerifyEmail(String email) {
        // get code from emailAndCode file
        String code;
        try {
            do {
                code = FileService.getCodeFromFile(email);
                // if the browser is closed, exit the loop
                if (driver.getWindowHandles().isEmpty()) {
                    driver.quit(); // đảm bảo tắt driver nếu chưa
                }
                Thread.sleep(3000);
            } while (code == null || code.isEmpty());

            // 1. Chờ input code xuất hiện
            By codeInputBy = By.id("id-verificationCode");
            WebElement codeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(codeInputBy));
            Thread.sleep(1000 + random.nextInt(500));

            // 2. Điền code
            clickElementByJs(codeInput);
            sendKeyByJs(codeInput, code);

            // 3. Click nút xác nhận (variant-primary, nhưng không có mt-spacing)
            By confirmBtnBy = By.xpath("//button[contains(.,'認証する')]");
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmBtnBy));
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", confirmBtn);
            Thread.sleep(600 + random.nextInt(500));

            clickElementByJs(confirmBtn);

            // Hoàn thành
            System.out.println("Xac thuc email thanh cong! Vui long dat hang.");

        } catch (Exception e) {
            System.out.println("Loi khi xac thuc email: " + e.getMessage());
        }

    }

    public void addProductsToCart(WebDriver driver, List<String> productsDetailList, String email) throws Exception {
        // check if gu page then login
        if (!productLink.contains(UNIQLO_LINK)) {
            driver.get("https://www.gu-global.com/jp/ja/member");
            Thread.sleep(1000 + random.nextInt(500));
        }

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

        System.out.println("Da them tat ca san pham vao gio hang.");
        // after add product to cart, go to cart and check
        checkProductInCart(driver, email);

    }

    public void addOneProductToCartAction(WebDriver driver, String productUrl, int amount, String size, String color) {
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
            System.out.println("Loi khi them san pham: " + productUrl + "vao gio hang: " + e.getMessage());
        }

    }

    public void checkProductInCart(WebDriver driver, String email) {

        try {
            // 1. Vào giỏ hàng
            if (productLink.contains(UNIQLO_LINK)) {
                driver.get("https://www.uniqlo.com/jp/ja/cart");
            } else {
                driver.get("https://www.gu-global.com/jp/ja/cart");
            }

            Thread.sleep(1000 + random.nextInt(1000));

            WebElement container = driver.findElement(
                    By.xpath("//div[contains(@class, 'fr-ec-mb-spacer-32')]")
            );

            List<WebElement> products = container.findElements(
                    By.xpath(".//div[contains(@class,'fr-ec-product-tile fr-ec-product-tile__horizontal fr-ec-product-tile__horizontal-small')]")
            );

            StringBuilder orderingProductInfo = new StringBuilder(email);

            for (WebElement product : products) {
                // Lấy code từ ảnh
                WebElement img = product.findElement(By.xpath(".//div[contains(@class,'fr-ec-product-tile__image')]//img"));
                String imgSrc = img.getAttribute("src");
                Pattern p = Pattern.compile("/(\\d{6})/");
                assert imgSrc != null;
                Matcher m = p.matcher(imgSrc);
                String code = null;
                if (m.find()) {
                    code = m.group(1);
                }

                // Lấy màu
                String colorText = product.findElement(By.xpath(".//p[contains(text(),'カラー')]")).getText();
                String color = colorText.replaceAll("カラー:\\s*\\d+\\s+", "");
                // Lấy size
                String sizeText = product.findElement(By.xpath(".//p[contains(text(),'サイズ')]")).getText();
                String size = sizeText.replaceAll(".*\\s", "");
                // Lấy giá
                String amount = product.findElement(By.xpath(".//div[contains(@class,'fr-ec-text-transform-normal fr-ec-counter__value')]")).getText();

                // combine to 1 line string
                orderingProductInfo.append(",").append(code).append(",").append(amount).append(",").append(size).append(",").append(color);

            }

            FileService.appendOrderedProductListInfo(orderingProductInfo.toString());

        } catch (Exception e) {
            System.out.println("Loi khi Check gio hàng.....!");
        }

    }

    public void order(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                      String street1, String street2, String phone1, String phone2) {
        try {
            if (productLink.contains(UNIQLO_LINK)) {
                uniqloSeleniumService.order(familyName, givenName, phoneticFamilyName, phoneticGivenName,
                        street1, street2, phone1, phone2);
            } else {
                guSeleniumService.order(familyName, givenName, phoneticFamilyName, phoneticGivenName,
                        street1, street2, phone1, phone2);
            }

        } catch (Exception e) {
            System.out.println("Loi khi thuc hien thanh toan don hang: " + e.getMessage());
        }

    }

    public void orderToShop(String familyName, String givenName, String phoneticFamilyName, String phoneticGivenName,
                            String street1, String street2, String phone1, String phone2, String storeName) {
        try {
            if (productLink.contains(UNIQLO_LINK)) {
                uniqloSeleniumService.orderToShop(familyName, givenName, phoneticFamilyName, phoneticGivenName,
                        street1, street2, phone1, phone2, storeName);
            } else {
                guSeleniumService.orderToShop(familyName, givenName, phoneticFamilyName, phoneticGivenName,
                        street1, street2, phone1, phone2, storeName);

            }
        } catch (Exception e) {
            System.out.println("Loi khi thuc hien thanh toan don hang: " + e.getMessage());
        }

    }

    public void logoutAccount() throws Exception {
        try {
            if (productLink.contains(UNIQLO_LINK)) {
                uniqloSeleniumService.logoutAccount();
            } else {
                guSeleniumService.logoutAccount();
            }

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

    public void clickElementByJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

}
