package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainPage {
    private final WebDriver driver;
    private WebDriverWait wait(short secs) { return new WebDriverWait(driver, Duration.ofSeconds(secs)); }
    private JavascriptExecutor js() { return (JavascriptExecutor) driver; }

    private final By faqItems  = By.cssSelector(".accordion__item");
    private final By faqButton = By.cssSelector(".accordion__button");
    private final By faqPanel  = By.cssSelector(".accordion__panel");

    private final By cookieBtn = By.id("rcc-confirm-button");

    private final By orderTopBtn    = By.xpath("(//button[contains(normalize-space(.),'Заказать')])[1]");
    private final By orderBottomBtn = By.xpath("(//button[contains(normalize-space(.),'Заказать')])[last()]");

    public MainPage(WebDriver driver) { this.driver = driver; }

    public void open() {
        driver.get("https://qa-scooter.praktikum-services.ru/");
        wait((short)5).until(d -> js().executeScript("return document.readyState").equals("complete"));
        acceptCookiesIfVisible();
    }

    private void acceptCookiesIfVisible() {
        try {
            WebElement btn = wait((short)3).until(ExpectedConditions.elementToBeClickable(cookieBtn));
            btn.click();
            wait((short)2).until(ExpectedConditions.invisibilityOfElementLocated(cookieBtn));
        } catch (TimeoutException ignore) {}
    }

    public boolean isPageLoaded() {
        try {
            return wait((short)5).until(d -> js().executeScript("return document.readyState").equals("complete"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int faqCount() { return driver.findElements(faqItems).size(); }

    private void scrollIntoViewAvoidSticky(WebElement el) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        js().executeScript("window.scrollBy(0, -150);");
    }

    private void safeClick(WebElement el) {
        try {
            wait((short)3).until(ExpectedConditions.elementToBeClickable(el));
            el.click();
        } catch (ElementClickInterceptedException ex) {
            js().executeScript("window.scrollBy(0, -150);");
            try { el.click(); } catch (Exception ignore) {
                js().executeScript("arguments[0].click();", el);
            }
        }
    }

    public boolean isAnswerVisible(int index) {
        List<WebElement> items = driver.findElements(faqItems);
        if (index < 0 || index >= items.size()) {
            return false;
        }
        WebElement item = items.get(index);
        WebElement panel = item.findElement(faqPanel);

        try {
            return wait((short)2).until(ExpectedConditions.visibilityOf(panel)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getAnswerText(int index) {
        List<WebElement> items = driver.findElements(faqItems);
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("FAQ index out of bounds: " + index + ", total: " + items.size());
        }
        WebElement item = items.get(index);
        WebElement panel = item.findElement(faqPanel);

        wait((short)3).until(d -> {
            String text = panel.getText().trim();
            return !text.isEmpty() && panel.isDisplayed();
        });

        return panel.getText().trim();
    }

    public String expandAndGetAnswer(int index) {
        List<WebElement> items = driver.findElements(faqItems);
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("FAQ index out of bounds: " + index + ", total: " + items.size());
        }

        WebElement item = items.get(index);
        WebElement button = item.findElement(faqButton);
        WebElement panel = item.findElement(faqPanel);

        scrollIntoViewAvoidSticky(button);
        safeClick(button);

        wait((short)3).until(ExpectedConditions.visibilityOf(panel));

        return getAnswerText(index);
    }

    public void startOrderFromTop() {
        WebElement btn = wait((short)5).until(ExpectedConditions.elementToBeClickable(orderTopBtn));
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        wait((short)5).until(ExpectedConditions.urlContains("/order"));
    }

    public void startOrderFromBottom() {
        js().executeScript("window.scrollTo(0, document.body.scrollHeight)");
        WebElement btn = wait((short)5).until(ExpectedConditions.elementToBeClickable(orderBottomBtn));
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        wait((short)5).until(ExpectedConditions.urlContains("/order"));
    }
}