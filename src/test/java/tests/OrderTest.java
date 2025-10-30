package tests;

import config.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import pages.MainPage;
import pages.OrderPage;

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

    @ParameterizedTest(name = "Top: оформление заказа (набор #{index})")
    @MethodSource("orderDataProvider")
    @DisplayName("Оформление заказа через верхнюю кнопку")
    @Timeout(30)
    public void testOrderFromTopButton(
            String name, String surname, String address,
            String metro, String phone, String date,
            String period, String color, String comment
    ) {
        System.out.println("Тест заказа (верхняя кнопка)");

        MainPage main = new MainPage(driver);
        main.open();
        main.startOrderFromTop();

        OrderPage order = new OrderPage(driver);
        order.fillCustomerInfo(name, surname, address, metro, phone);
        order.goNextToRentalStep();
        order.fillRentalInfo(date, period, color, comment);
        order.submitAndConfirm(); // единственный вызов

        assertTrue(order.isSuccessVisible(), "Не появился попап с успешным оформлением/номером заказа");
        System.out.println("Заказ успешно оформлен");
    }

    @ParameterizedTest(name = "Bottom: оформление заказа (набор #{index})")
    @MethodSource("orderDataProvider")
    @DisplayName("Оформление заказа через нижнюю кнопку")
    @Timeout(30)
    public void testOrderFromBottomButton(
            String name, String surname, String address,
            String metro, String phone, String date,
            String period, String color, String comment
    ) {
        System.out.println("Тест заказа (нижняя кнопка)");

        MainPage main = new MainPage(driver);
        main.open();
        main.startOrderFromBottom();

        OrderPage order = new OrderPage(driver);
        order.fillCustomerInfo(name, surname, address, metro, phone);
        order.goNextToRentalStep();
        order.fillRentalInfo(date, period, color, comment);
        order.submitAndConfirm(); // единственный вызов

        assertTrue(order.isSuccessVisible(), "Не появился попап с успешным оформлением/номером заказа");
        System.out.println("Заказ успешно оформлен");
    }

    static Stream<Arguments> orderDataProvider() {
        return Stream.of(
                Arguments.of("Иван", "Иванов", "ул. Ленина, д. 1", "Сокольники",
                        "+79991234567", "01.12.2024", "сутки", "black", "Тестовый заказ 1"),
                Arguments.of("Мария", "Петрова", "пр. Мира, д. 15", "Черкизовская",
                        "+79997654321", "02.12.2024", "трое суток", "grey", "Тестовый заказ 2")
        );
    }
}
