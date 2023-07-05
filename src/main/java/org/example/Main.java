/**
 * Copyright(C),2015‐2023,北京清能互联科技有限公司
 */

package org.example;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * <br>
 *
 * @Author: duanrq@tsintergy.com.cn
 * @Date: 2023/7/4 18:32
 * @Version: 1.0.0
 */
public class Main {

    public static final String BASE_URL = "https://qpe5ta.yuque.com";

    public static final String USERNAME = "xxxx";

    public static final String PASSWORD = "xxxxxx";

    public static final String LIST_URL = "/qpe5ta/qstvvu";

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(BASE_URL + LIST_URL);
        String title = driver.getTitle();

        loginMethod(driver);
        Thread.sleep(6000);
        WebElement element = driver.findElement(
            By.xpath("/html/body/div[8]/div/div[2]/div/div[2]/button/span"));
        element.click();
        Thread.sleep(2000);
        Set<String> urls = scrollToFind(driver);
        System.out.println("urls = " + urls);

        // 只过滤出 BASE_URL+LIST_URL
        Set<String> finalUrls = urls.stream().filter(s -> s.contains(BASE_URL + LIST_URL))
            .collect(Collectors.toSet());
        System.out.println("finalUrls = " + finalUrls);

        // 根据Url下载文件
        urlToDownload(driver, finalUrls);

        // 退出
        Thread.sleep(2000);
        driver.quit();
    }

    private static void urlToDownload(WebDriver driver, Set<String> finalUrls) throws InterruptedException {
        for (String url : finalUrls) {
            // 跳转到指定的页面
            driver.navigate().to(url);
            Thread.sleep(1000);
            // 获取右上角的操作按钮
            WebElement element1 = driver.findElement(
                By.xpath("//*[@id=\"docHeadRightWrapper\"]/div/div[2]/div[2]"));
            element1.click();
            Thread.sleep(1000);

            // 获取下载按钮
            WebElement element2 = driver.findElement(
                By.cssSelector("#rc-tabs-1-panel-DocOpertaion > div > div > div:nth-child(3) > div:nth-child(5)"));
            element2.click();
            Thread.sleep(1000);

            // 获取下面元素数量 如果是4个就下载，1个就不下载

            List<WebElement> elements = driver.findElements(By.cssSelector(
                "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > div.ant-spin-nested-loading > div > div > div > div"));

            if (elements == null || elements.size() < 4) {
                System.out.println("文件不能导出成markdown，文件名: " + driver.getTitle());
                Thread.sleep(500);
                driver.navigate().back();
                Thread.sleep(500);
                driver.navigate().back();
                continue;
            }
            WebElement element3 = driver.findElement(
                By.cssSelector(
                    "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > div.ant-spin-nested-loading > div > div > div > div:nth-child(2)"));
            element3.click();

            // 点击下载按钮
            Thread.sleep(1000);
            WebElement element4 = driver.findElement(
                By.cssSelector(
                    "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > div > div:nth-child(3)"));
            element4.click();
            // 返回列表页
            Thread.sleep(2000);
            driver.navigate().back();
        }
    }

    private static void loginMethod(WebDriver driver) throws InterruptedException {
        Thread.sleep(500);
        // 点击密码登录
        WebElement switchButton = driver.findElement(By.cssSelector(
            "#ReactApp > div > div.lark.page-account.pc-web.lark-login > div > div:nth-child(1) > div > div > div > div.lark-form-content.form-pro > div > div.login-more-warp > div > div.switch-login-warp > div"));
        switchButton.click();
        Thread.sleep(300);
        WebElement phone = driver.findElement(By.cssSelector(
            "#ReactApp > div > div.lark.page-account.pc-web.lark-login > div > div:nth-child(1) > div > div > div > div.lark-form-content.form-pro > div > form > div:nth-child(1) > div > div > span > div > span > input"));
        phone.sendKeys(USERNAME);
        Thread.sleep(200);
        WebElement password = driver.findElement(By.cssSelector(
            "#password"));
        password.sendKeys(PASSWORD);
        Thread.sleep(200);
        WebElement checkBox = driver.findElement(By.cssSelector(
            "#ReactApp > div > div.lark.page-account.pc-web.lark-login > div > div:nth-child(1) > div > div > div > div.lark-form-content.form-pro > div > div.lark-login-protocol > label > span.ant-checkbox > input"));
        checkBox.click();
        Thread.sleep(200);
        WebElement login = driver.findElement(By.cssSelector(
            "#ReactApp > div > div.lark.page-account.pc-web.lark-login > div > div:nth-child(1) > div > div > div > div.lark-form-content.form-pro > div > form > div:nth-child(4) > div > div > span > button"));
        login.click();
    }

    private static Set<String> scrollToFind(WebDriver driver) throws InterruptedException {
        Thread.sleep(500);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        // 获取初始页面高度
        // 滚动循环直到滚动到底部
        Set<String> urls = new HashSet<>();
        int i = 0;
        while (true) {
            // 滚动到页面底部
            javascriptExecutor.executeScript("window.scrollTo(0, " + i + ")");
            // 等待页面加载新内容
            Thread.sleep(1000);
            List<String> urlList = getDomUrl(driver);
            if (urlList != null) {
                System.out.println("获取的url数量为 = " + urlList.size());
            }
            urls.addAll(urlList);
            Thread.sleep(1000);

            // 判断是否滚动到底部
            boolean isScrolledToBottom = (boolean) javascriptExecutor.executeScript(
                "return window.pageYOffset >= (document.body.scrollHeight - window.innerHeight)");
            if (isScrolledToBottom) {
                break;
            }
            i += 700;
        }
        // 打印滚动完成消息
        System.out.println("已滚动到底部");
        return urls;
    }

    private static List<String> getDomUrl(WebDriver driver) {
        List<WebElement> a = driver.findElements(By.tagName("a"));
        return a.stream().filter(webElement -> {
                String tagName = webElement.getTagName();
                String href = webElement.getAttribute("href");
                String aClass = webElement.getAttribute("class");
                return tagName.contains("a") && href != null && aClass.contains("catalogTreeItem-module_content_J3D5T");
            })
            .map(webElement -> webElement.getAttribute("href")).distinct().collect(Collectors.toList());
    }
}