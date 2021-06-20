import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import Validation.PasswordValidation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestPasswordValidation {
  @ParameterizedTest
  @ValueSource(strings = {"U$&H64zvBrT6J*4D!ouaa^QA",
      "R7sJRC@7!62U!FqQqXEWUvsq&W5F9@FvPr#QaXo2Xfx3bkVBaXiMshVqtrs4gSEG", "tQ2#qroQ112TG##"})
  public void testValidPassword(String input) {
    PasswordValidation passwordValidation = new PasswordValidation();
    assertTrue(passwordValidation.validatePasword(input));
  }

  @ParameterizedTest
  @ValueSource(strings = {"57VYJL34",
      "st$RGeqQumHq5qxC5oBhN%2$As8Et7vG$8RwwtBt*GfZ5Vtcnr4eQNo5RMxzHZnz6", "#6KEY$r", "p2hZA7gc8BvZn3cJG6dD42bB",
      "hpBTCEYiwgwhz@K^gCiaxtdB", "V!H%4WAM##N&KREX46STMUJB", "P@ssw0rd", "tQ2#qroQ", "rjoannk7v7%h@dp6@c!#j7fo", ""})
  public void testInvalidPassword(String input) {
    PasswordValidation passwordValidation = new PasswordValidation();
    assertFalse(passwordValidation.validatePasword(input));
  }
}
