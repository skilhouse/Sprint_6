package tests;

import config.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.MainPage;
import pages.OrderPage;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest extends BaseTest {

    @Test
    @DisplayName("Базовый тест — главная страница открывается и грузится")
    public void testBasicFunctionality() {
        MainPage main = new MainPage(driver);
        main.open();
        assertTrue(main.isPageLoaded(), "Главная страница не загрузилась");
        System.out.println("Базовая функциональность работает");
    }

    @ParameterizedTest(name = "Оформление заказа через {0} кнопку (набор #{index})")
    @MethodSource("orderTestDataProvider")
    @DisplayName("Оформление заказа через разные кнопки")
    @Timeout(30)
    public void testOrderWithDifferentButtons(
            String buttonType,
            Consumer<MainPage> orderStarter,
            String name, String surname, String address,
            String metro, String phone, String date,
            String period, String color, String comment
    ) {
        System.out.println("Тест заказа (" + buttonType + " кнопка)");

        MainPage main = new MainPage(driver);
        main.open();

        orderStarter.accept(main);

        OrderPage order = new OrderPage(driver);
        order.fillCustomerInfo(name, surname, address, metro, phone);
        order.goNextToRentalStep();
        order.fillRentalInfo(date, period, color, comment);
        order.submitAndConfirm();

        assertTrue(order.isSuccessVisible(),
                "Не появился попап с успешным оформлением заказа для " + buttonType + " кнопки");
        System.out.println("Заказ успешно оформлен через " + buttonType + " кнопку");
    }

    static Stream<Arguments> orderTestDataProvider() {
        return Stream.of(
                Arguments.of(
                        "верхнюю",
                        (Consumer<MainPage>) MainPage::startOrderFromTop,
                        "Иван", "Иванов", "ул. Ленина, д. 1", "Сокольники",
                        "+79991234567", "01.12.2024", "сутки", "black", "Тестовый заказ 1"
                ),
                Arguments.of(
                        "верхнюю",
                        (Consumer<MainPage>) MainPage::startOrderFromTop,
                        "Мария", "Петрова", "пр. Мира, д. 15", "Черкизовская",
                        "+79997654321", "02.12.2024", "трое суток", "grey", "Тестовый заказ 2"
                ),
                Arguments.of(
                        "нижнюю",
                        (Consumer<MainPage>) MainPage::startOrderFromBottom,
                        "Иван", "Иванов", "ул. Ленина, д. 1", "Сокольники",
                        "+79991234567", "01.12.2024", "сутки", "black", "Тестовый заказ 1"
                ),
                Arguments.of(
                        "нижнюю",
                        (Consumer<MainPage>) MainPage::startOrderFromBottom,
                        "Мария", "Петрова", "пр. Мира, д. 15", "Черкизовская",
                        "+79997654321", "02.12.2024", "трое суток", "grey", "Тестовый заказ 2"
                )
        );
    }
}