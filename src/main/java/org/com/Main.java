package org.com;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        FileService fileService = new FileService();
        // Đọc danh sách email từ file
        List<String> emailList = fileService.getListEmail();
        if (emailList == null || emailList.isEmpty()) {
            System.out.println("Khong co Email nao trong file!!!");
            System.exit(0); // Exit if no emails are found
        }
        // Đọc dữ liệu từ file
        String postalCode = fileService.getPostalCode();

        SeleniumService seleniumService = new SeleniumService();


        for (String email : emailList) {
            if (email == null || email.isEmpty()) {
                System.out.println("Email Empty! Skip !");
                continue; // Skip registration if email is empty or null
            }
            seleniumService.register(email, postalCode);
        }

        fileService.moveProcessedEmails(emailList);
        // In thông báo hoàn thành
        System.out.println("Hoan thanh dang ky!");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);
    }
}
