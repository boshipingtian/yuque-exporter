package org.example;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class DingDing {

    public static final String BASE_URL = "https://xxx.yuque.com";

    public static final String USERNAME = "xxxx";

    public static final String PASSWORD = "xxxxx";

    public static final String LIST_URL = "/xx/xxx";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/**/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(BASE_URL + LIST_URL);

        
    }
}