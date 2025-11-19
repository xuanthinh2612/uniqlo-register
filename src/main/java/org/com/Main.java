package org.com;

import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {
    private static final int ORDER_NOT_READY = 0;
    private static final int LOGOUT_SYS = 2;
    private static final int ORDER_READY = 1;
    private static final int ADD_PRODUCT_READY = 3;
    private static final int ORDER_TO_STOCK_TYPE = 1;
    private static final int ORDER_TO_SHOP_TYPE = 2;
    private static final int COLUMN_OF_ONE_PRODUCT = 5;

    static WebDriver driver = ChromeHelper.initChromeWithAttachedSelenium();
    static FileService fileService = new FileService();
    static SeleniumService seleniumService = new SeleniumService(driver);

    public static void main(String[] args) {
        // Đọc dữ liệu từ file
        Map<String, String> personalData = fileService.getPersonalDataSet();

        try {
            while (true) {
                int MAX_TIMES_TRY = 5;
                int tryTimes = 0; // reset try times for each account
                // get first email from file
                String email = fileService.getFirstEmail();
                // get list product details in one order from file
                List<String> productDetails = fileService.getProductDetails();
                String productLink = productDetails.get(4); // index 4 la link san pham
                seleniumService.setProductLink(productLink);

                if (email == null) {
                    System.out.println("Khong co Email nao trong file!!!");
                    break; // Exit if no emails are found
                }
                // 1. start register
                while (true) {
                    if (tryTimes < MAX_TIMES_TRY) {
                        try {
                            seleniumService.register(email, personalData);
                            tryTimes = 0;
                            break;
                        } catch (Exception e) {
                            tryTimes++;
                            System.out.println("Loi dang ky tai khoan. Thu lai lan " + tryTimes + "..." + e.getMessage());
                            Thread.sleep(2000);
                        }
                    } else {
                        throw new Exception("Reach max retry times for register account.");
                    }
                }

                // 2. wait for user to verify email and login
                seleniumService.insertCodeToVerifyEmail(email);

                // 3. wait for user to add product to cart
                String storeName;
                while (true) {
                    if (tryTimes < MAX_TIMES_TRY) {
                        try {
                            storeName = waitForAddProductToCart(driver, seleniumService, productDetails);
                            tryTimes = 0;
                            break;
                        } catch (Exception e) {
                            tryTimes++;
                            System.out.println("Loi khi them san pham vao gio hang. Thu lai lan " + tryTimes + "..." + e.getMessage());
                            Thread.sleep(2000);
                        }
                    } else {
                        throw new Exception("Reach max retry times for add product to cart.");
                    }
                }

                // 4. wait for user to start order
                while (true) {
                    if (tryTimes < MAX_TIMES_TRY) {
                        try {
                            waitForStartOrder(driver, personalData, storeName);
                            break;
                        } catch (Exception e) {
                            tryTimes++;
                            System.out.println("Loi dang ky tai khoan. Thu lai lan " + tryTimes + "..." + e.getMessage());
                            Thread.sleep(2000);
                        }
                    } else {
                        throw new Exception("Reach max retry times for register account.");
                    }
                }
                // 5. wait for user to logout
                waitForLogout(driver, seleniumService);
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        ChromeHelper.releasePort();
        System.exit(0);
    }

    public static String waitForAddProductToCart(WebDriver driver, SeleniumService seleniumService, List<String> productDetails)
            throws Exception {
        while (true) {
            // if productDetails is empty, exit the loop
            // go to process order
            if (productDetails == null || productDetails.isEmpty()) {
                System.out.println("Khong co san pham nao trong file!!!");
                break; // Exit if no products are found
            }

            // if the browser is closed, exit the loop
            if (driver.getWindowHandles().isEmpty()) {
                System.out.println("Trinh Duyet da bi dong! Quit.");
                driver.quit(); // đảm bảo tắt driver nếu chưa
                System.exit(0);
                break;
            }

            // Kiểm tra xem có thể tiến hành đặt hàng được chưa
            int actionFlag = Integer.parseInt(fileService.getActionFlag());

            if (actionFlag == ADD_PRODUCT_READY) {

                seleniumService.addProductsToCart(driver, productDetails);

                // Nếu đặt hàng đến cửa hàng, trả về tên cửa hàng
                int orderType = productDetails.size() % COLUMN_OF_ONE_PRODUCT == 0 ? ORDER_TO_STOCK_TYPE : ORDER_TO_SHOP_TYPE;
                if (orderType == ORDER_TO_SHOP_TYPE) {
                    return productDetails.get(productDetails.size() - 1); // return store name
                } else {
                    return null;
                }
                // Thoát vòng lặp sau khi đặt hàng thành công
            }

            Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại

        }
        return null;
    }

    public static void waitForStartOrder(WebDriver driver, Map<String, String> personalDataSet, String storeName) throws Exception {
        while (true) {
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
                String familyName = personalDataSet.get("familyName");
                String givenName = personalDataSet.get("givenName");
                String phoneticFamilyName = personalDataSet.get("phoneticFamilyName");
                String phoneticGivenName = personalDataSet.get("phoneticGivenName");
                String street1 = personalDataSet.get("street1");
                String street2 = personalDataSet.get("street2");
                String phone1 = personalDataSet.get("phone1");
                String phone2 = personalDataSet.get("phone2");

                if (!Objects.isNull(storeName)) {
                    seleniumService.orderToShop(familyName, givenName, phoneticFamilyName, phoneticGivenName, street1, street2, phone1, phone2, storeName);
                } else {
                    seleniumService.order(familyName, givenName, phoneticFamilyName, phoneticGivenName, street1, street2, phone1, phone2);
                }
                break; // Thoát vòng lặp sau khi đặt hàng thành công
            }

            Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại
        }
    }

    public static void waitForLogout(WebDriver driver, SeleniumService seleniumService) throws Exception {
        while (true) {
            if (driver.getWindowHandles().isEmpty()) {
                System.out.println("Trinh Duyet da bi dong! Quit.");
                driver.quit(); // đảm bảo tắt driver nếu chưa
                System.exit(0);
                break;
            }
            // Kiểm tra xem có thể tiến hành đặt hàng được chưa
            int actionFlag = Integer.parseInt(fileService.getActionFlag());

            if (actionFlag == LOGOUT_SYS) {
                System.out.println("logout...");
                seleniumService.logoutAccount();
                break;
            }
            Thread.sleep(3000); // chờ 3 giây rồi kiểm tra lại

        }
    }

}
