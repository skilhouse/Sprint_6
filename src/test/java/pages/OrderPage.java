package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrderPage {

    private final WebDriver driver;

    private final By nameInput    = By.xpath("//input[@placeholder='* Имя']");
    private final By surnameInput = By.xpath("//input[@placeholder='* Фамилия']");
    private final By addressInput = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");
    private final By metroInput   = By.xpath("//input[@placeholder='* Станция метро']");
    private final By phoneInput   = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private final By nextButton   = By.xpath("//button[normalize-space(.)='Далее']");

    private final By dateInput            = By.xpath("//input[@placeholder='* Когда привезти самокат']");
    private final By rentalDropdown       = By.cssSelector(".Dropdown-control");
    private final By rentalMenuAnyOption  = By.cssSelector(".Dropdown-menu .Dropdown-option");
    private By rentalOption(String text)  { return By.xpath("//div[contains(@class,'Dropdown-menu')]//div[contains(@class,'Dropdown-option') and normalize-space(.)='" + text + "']"); }

    private final By colorBlack = By.id("black");
    private final By colorGrey  = By.id("grey");
    private final By commentInput = By.xpath("//input[@placeholder='Комментарий для курьера']");

    private final By orderButton = By.xpath("//div[contains(@class,'Order_Buttons__')]//button[normalize-space(.)='Заказать']");
    private final By modalRoot   = By.cssSelector("div.Order_Modal__YZ-d3");
    private final By confirmYesButton = By.xpath("//div[contains(@class,'Order_Modal__')]//button[normalize-space(.)='Да']");
    private final By successHeader = By.xpath("//div[contains(@class,'Order_ModalHeader__')][contains(.,'Заказ оформлен')]");
    private final By orderText     = By.cssSelector(".Order_Modal__YZ-d3 .Order_Text__2broi");
    private final By anyOrderText  = By.xpath("//*[contains(normalize-space(.),'Номер заказа')]");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
    }

    private WebDriverWait waitFor(long s) { return new WebDriverWait(driver, Duration.ofSeconds(s)); }
    private JavascriptExecutor js() { return (JavascriptExecutor) driver; }
    private void tinyPause() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
    }

    public void fillCustomerInfo(String name, String surname, String address, String metroStation, String phone) {
        waitFor(5).until(ExpectedConditions.visibilityOfElementLocated(nameInput));

        type(nameInput, name, 4);
        type(surnameInput, surname, 4);
        type(addressInput, address, 4);

        selectMetroStation(metroStation);

        type(phoneInput, phone, 4);
    }

    private void type(By locator, String value, long timeout) {
        WebElement el = waitFor(timeout).until(ExpectedConditions.elementToBeClickable(locator));
        el.clear();
        el.sendKeys(value);
    }

    private void selectMetroStation(String metroStation) {

        WebElement field = waitFor(4).until(ExpectedConditions.elementToBeClickable(metroInput));
        field.click();
        field.clear();
        field.sendKeys(metroStation);

        By menuAny = By.cssSelector(".select-search__options, .select-search__select, .select-search__row");
        waitFor(3).until(ExpectedConditions.presenceOfElementLocated(menuAny));

        By exact = By.xpath("//div[contains(@class,'select-search__option') and normalize-space(.)='" + metroStation + "']");
        if (!clickIfClickable(exact, 2)) {
            By firstOption = By.cssSelector(".select-search__option");
            clickIfClickable(firstOption, 2);
        }

        waitFor(3).until(d -> {
            String v = d.findElement(metroInput).getAttribute("value");
            return v != null && !v.isBlank();
        });
    }

    private boolean clickIfClickable(By locator, long timeout) {
        try {
            WebElement el = waitFor(timeout).until(ExpectedConditions.elementToBeClickable(locator));
            js().executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            tinyPause();
            try { el.click(); } catch (Exception e) { js().executeScript("arguments[0].click();", el); }
            return true;
        } catch (TimeoutException te) {
            return false;
        }
    }

    public void goNextToRentalStep() {
        WebElement next = waitFor(4).until(ExpectedConditions.elementToBeClickable(nextButton));
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", next);
        next.click();
        waitFor(4).until(ExpectedConditions.visibilityOfElementLocated(dateInput));
    }

    public void fillRentalInfo(String date, String period, String color, String comment) {
        setDeliveryDate(date);
        setRentalPeriod(period);
        setScooterColor(color);
        if (comment != null && !comment.isBlank()) setComment(comment);
    }

    private void setDeliveryDate(String date) {
        WebElement dateField = waitFor(4).until(ExpectedConditions.elementToBeClickable(dateInput));
        dateField.click();
        dateField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        dateField.sendKeys(Keys.DELETE);
        dateField.sendKeys(date);
        dateField.sendKeys(Keys.ENTER);

        try {
            driver.findElement(By.tagName("body")).click();
        } catch (Exception ignored) {}
    }

    private void setRentalPeriod(String periodText) {
        WebElement dd = waitFor(4).until(ExpectedConditions.elementToBeClickable(rentalDropdown));
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", dd);
        dd.click();

        waitFor(3).until(ExpectedConditions.visibilityOfElementLocated(rentalMenuAnyOption));
        WebElement opt = waitFor(4).until(ExpectedConditions.elementToBeClickable(rentalOption(periodText)));
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", opt);
        tinyPause();
        opt.click();
    }

    private void setScooterColor(String color) {
        if (color == null || color.isBlank()) return;
        By target = "black".equalsIgnoreCase(color) ? colorBlack : colorGrey;
        WebElement el = waitFor(3).until(ExpectedConditions.elementToBeClickable(target));
        js().executeScript("arguments[0].click();", el);
    }

    private void setComment(String comment) {
        WebElement el = waitFor(4).until(ExpectedConditions.elementToBeClickable(commentInput));
        el.clear();
        el.sendKeys(comment);
    }

    public void submitAndConfirm() {
        WebElement orderBtn = waitFor(5).until(ExpectedConditions.elementToBeClickable(orderButton));
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", orderBtn);
        orderBtn.click();

        waitFor(5).until(ExpectedConditions.visibilityOfElementLocated(modalRoot));
        WebElement yes = waitFor(4).until(ExpectedConditions.elementToBeClickable(confirmYesButton));
        yes.click();

        WebDriverWait w = waitFor(5);
        w.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(successHeader),
                ExpectedConditions.presenceOfElementLocated(anyOrderText)
        ));

        java.util.regex.Pattern num = java.util.regex.Pattern.compile("\\d{3,}");
        w.until(ExpectedConditions.or(
                ExpectedConditions.textMatches(orderText, num),
                ExpectedConditions.textMatches(anyOrderText, num)
        ));
    }

    public boolean isSuccessVisible() {
        try {
            WebDriverWait w = waitFor(5);
            w.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(successHeader),
                    ExpectedConditions.presenceOfElementLocated(anyOrderText)
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
