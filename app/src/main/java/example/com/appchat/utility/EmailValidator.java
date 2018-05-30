package example.com.appchat.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class is used to validate email
 */
public class EmailValidator {

    private Pattern pattern;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public EmailValidator() {

        pattern = Pattern.compile(EMAIL_PATTERN);

    }

    /**
     * Validate hex with regular expression
     *
     * @param email hex for validation
     * @return true valid hex, false invalid hex
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean validate(final String email) {

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }
}