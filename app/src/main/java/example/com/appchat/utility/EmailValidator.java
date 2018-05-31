/**
 * Module Name/Class			:	EmailValidator
 * Author Name					:	Sachin Arora
 * Date							:	May,31 2018
 * Purpose						:	This class validated the email which user entered
 */

package example.com.appchat.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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