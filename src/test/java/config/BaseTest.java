package config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        System.out.println("Запуск тестов в браузере: " + browser);

        switch (browser) {
            case "firefox":
                setupFirefox();
                break;
            case "chrome":
            default:
                setupChrome();
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(12));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));

        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

    private void setupChrome() {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--log-level=1");

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        if ("true".equals(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            System.out.println("Запуск в headless режиме");
        }

        driver = new ChromeDriver(options);
        System.out.println("Chrome драйвер инициализирован");
    }

    private void setupFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);

        options.addArguments("--log-level=3");
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("geo.enabled", false);
        options.addPreference("media.navigator.permission.disabled", true);

        if ("true".equals(System.getProperty("headless", "false"))) {
            options.addArguments("--headless");
            System.out.println("Запуск в headless режиме");
        }

        driver = new FirefoxDriver(options);
        System.out.println("Firefox драйвер инициализирован");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                takeScreenshot("test-end-" + System.currentTimeMillis());
            } catch (Exception e) {
                System.out.println("Не удалось сделать скриншот: " + e.getMessage());
            }
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Ошибка при закрытии драйвера: " + e.getMessage());
            }
            System.out.println("Браузер закрыт");
        }
    }

    private void takeScreenshot(String fileName) throws IOException {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path screenshotsDir = Paths.get("target", "screenshots");
            Files.createDirectories(screenshotsDir);
            Path destination = screenshotsDir.resolve(fileName + ".png");
            Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Скриншот сохранён: " + destination.toAbsolutePath());
        } catch (WebDriverException wde) {
            System.out.println("Скриншот не получен (возможен упавший сеанс): " + wde.getMessage());
        }
    }
}
