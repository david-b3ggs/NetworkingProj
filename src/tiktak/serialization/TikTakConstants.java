package tiktak.serialization;

/**
 * List of static constants for TIKTAK Protocol
 * @version 1.0
 * @author David Beggs
 */
public abstract class TikTakConstants {

    public static final String NUMERIC_REGEX = "([0-9]*)";
    public static final String HEX_REGEX = "([0-9a-fA-F]*)";
    public static final String ZERO_OR_MORE_ALPHANUMERIC_REGEX = "([0-9a-zA-Z]*)";
    public static final String ID_REGEX = "(ID [0-9a-zA-Z]*)";
    public static final String CHALLENGE_REGEX = "(CLNG [0-9]*)";
    public static final String CREDENTIALS_REGEX = "(CRED [0-9a-fA-F]*)";
    public static final String ALPHANUMERIC_OR_WHITESPACE_REGEX = "([0-9a-zA-Z]*\\s)";
    public static final String BASE_64_COMPATABLE_REGEX = "([0-9a-zA-Z]*\\+*/*)";
    public static final String DOT_DELIMIT = "[.]";

    public static final Integer ERROR_CODE_MINIMUM = 99;
    public static final Integer ERROR_CODE_MAXIMUM = 1000;
    public static final Integer EXPECTED_MESSAGE_SPLIT = 3;
    public static final Integer LTSRL_HASH_CALC = 31;

    public static final String SCANNER_DELIMITER = "(?<=\\r\\n)";
    public static final String VERSION_START = "TIKTAK ";
    public static final String VERSION_MESSAGE = "TIKTAK 1.0\r\n";
    public static final String ACK_MESSAGE = "ACK\r\n";
    public static final String TOST_MESSAGE = "TOST\r\n";
    public static final String SPACE = " ";
    public static final String SPACE_MESSAGE_DELIM = " \r\n";
    public static final String MESSAGE_DELIM = "\r\n";
    public static final String ERROR_START = "ERROR ";
    public static final String LTSRL_START = "LTSRL ";
    public static final String VERSION_NUMBER = "1.0";

}
