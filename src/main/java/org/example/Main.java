package org.example;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

    public static final String BASE_URL = "https://xxx.yuque.com";

    public static final String USERNAME = "xxxx";

    public static final String PASSWORD = "xxxxx";

    public static final String LIST_URL = "/xx/xxx";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/**/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(BASE_URL + LIST_URL);

        loginMethod(driver);
        Thread.sleep(5000);
        Set<String> urls = scrollToFind(driver);
        System.out.println("urls = " + urls);

        // 只过滤出 BASE_URL+LIST_URL
        Set<String> finalUrls = urls.stream().filter(s -> s.contains(BASE_URL + LIST_URL))
            .collect(Collectors.toSet());
        System.out.println("finalUrls = " + finalUrls);
        System.out.println("需要下载 = " + finalUrls.size());
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

            List<WebElement> elements = driver.findElements(
                By.cssSelector("div.docOperationTabContent-module_menuText_kVtjJ"));
            WebElement export = elements.stream().filter(webElement -> {
                    String text = webElement.getText();
                    return text.contains("导出");
                }).findFirst()
                .orElse(null);
            if (Objects.isNull(export)) {
                System.out.println("导出失败，没有找到导出按钮：" + driver.getTitle());
                continue;
            }

            export.click();
            Thread.sleep(1000);

            WebElement element3 = driver.findElement(
                By.cssSelector(
                    "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > div.ant-spin-nested-loading > div > div > div > div:nth-child(1)"));
            element3.click();

            boolean downloading = true;

            // 添加进度条
            while (downloading) {
                Thread.sleep(200);
                try {
                    WebElement element = driver.findElement(By.cssSelector(
                        "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > div > p.icon"));
                    if (Objects.nonNull(element)) {
                        downloading = false;
                        System.out.println("下载完成 = " + driver.getTitle());
                    } else {
                        try {
                            WebElement error = driver.findElement(By.cssSelector(
                                "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > p.icon"));
                            if (Objects.nonNull(error)) {
                                downloading = false;
                                System.out.println("文件下载失败: " + driver.getTitle());
                            }
                        } catch (Exception ignore) {
                        }
                    }
                } catch (Exception e) {
                    try {
                        WebElement error = driver.findElement(By.cssSelector(
                            "div.ant-modal-wrap > div > div.ant-modal-content > div > div > div > p.icon"));
                        if (Objects.nonNull(error)) {
                            downloading = false;
                            System.out.println("文件下载失败: " + driver.getTitle());
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
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
            "#ReactApp > div > div.lark.page-account.pc-web.lark-login > div > div:nth-child(1) > div > div > div > div.lark-form-content.form-pro > div > form > div:nth-child(5) > div > div > span > button"));
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