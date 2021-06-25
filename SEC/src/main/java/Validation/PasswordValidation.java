package Validation;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PasswordValidation {

  private final Logger logger = LogManager.getLogger(PasswordValidation.class);
  private final Zxcvbn zxcvbn = new Zxcvbn();

  /**
   * test if a password is strong enough for our application
   * @param password string to test
   * @return true if strong enough
   */
  public boolean validatePasword(String password) {
    logger.trace("validatePasword");
    Strength strength = zxcvbn.measure(password);
    ArrayList<Pattern> regexSet = new ArrayList<>();
    regexSet.add(Pattern.compile("^[\\w!@#$%^&*]{8,64}$", Pattern.CASE_INSENSITIVE));
    regexSet.add(Pattern.compile("[a-z]+"));
    regexSet.add(Pattern.compile("[A-Z]+"));
    regexSet.add(Pattern.compile("[!@#$%^&*]+", Pattern.CASE_INSENSITIVE));
    regexSet.add(Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE));
    boolean isMatch = true;
    for (Pattern p : regexSet) {
      isMatch &= p.matcher(password).find();
    }

    if (strength.getScore() <= 2 || !isMatch) {
      System.out.println("Il semblerait que votre mot de passe n'est pas assez fort!");
      System.out.println("Il pourrait être deviné en " + (int) strength.getGuesses() + " tentatives.");
      System.out.println("Voici quelques suggestions pour vous aider :");
      for (String s : strength.getFeedback().getSuggestions()) {
        System.out.println(s);
      }
      return false;
    }
    return true;
  }
}
