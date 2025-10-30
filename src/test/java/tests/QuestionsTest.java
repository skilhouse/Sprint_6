package tests;

import config.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pages.MainPage;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestionsTest extends BaseTest {

    @Test
    @DisplayName("Каждый вопрос FAQ раскрывается и содержит текст ответа")
    public void testAllQuestions() {
        MainPage main = new MainPage(driver);
        main.open();
        assertTrue(main.isPageLoaded(), "Страница не загрузилась");

        int total = main.faqCount();
        assertTrue(total > 0, "FAQ список пуст");

        for (int i = 0; i < total; i++) {
            String answer = main.expandAndGetAnswer(i);
            assertFalse(answer.isBlank(), "Ответ пустой у вопроса " + (i + 1));
        }
    }

    @ParameterizedTest(name = "FAQ вопрос с индексом {0} раскрывается")
    @MethodSource("questionIndexProvider")
    @DisplayName("Выбранные вопросы FAQ корректно раскрываются")
    public void testSpecificQuestions(int idx) {
        MainPage main = new MainPage(driver);
        main.open();
        assertTrue(main.isPageLoaded(), "Страница не загрузилась");

        String answer = main.expandAndGetAnswer(idx);
        assertFalse(answer.isBlank(), "Ответ пустой у вопроса " + (idx + 1));
    }

    static Stream<Integer> questionIndexProvider() {
        return IntStream.range(0, 8).boxed();
    }
}
