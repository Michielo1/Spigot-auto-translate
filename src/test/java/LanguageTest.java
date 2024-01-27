import com.michielo.translation.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

public class LanguageTest {

    @ParameterizedTest
    @MethodSource("languageTestData")
    public void testLanguageEnum(String code, String expectedTranslation) {

        String fetched_code = Language.getAbbreviation(code);

        // Assert the expected translation result
        Assertions.assertEquals(expectedTranslation, fetched_code);
    }

    private static Stream<Object[]> languageTestData() {
        return Stream.of(
                new Object[]{"EN", "ENG"},
                new Object[]{"ENG", "ENG"},
                new Object[]{"FR", "FRA"},
                new Object[]{"INVALID", null}
        );
    }

}
