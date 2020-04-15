package topic.serialization;

public abstract class TopicConstants {
    public static final long MAX_QUERYID = 4294967295L;
    public static final int RESERVE_MAX = 3;
    public static final int MIN_HEADER = 8;
    public static final int QFLAG_BIT = 3;

    public static final int VERSION_FOUR = 7;
    public static final int VERSION_THREE = 6;
    public static final int VERSION_TWO = 5;
    public static final int VERSION_ONE = 4;

    public static final int MAX_REQUESTS = 65535;
    public static final int POST_LENGTH_LENGTH = 2;

    public static final int TIMEOUT = 3000;   // Resend timeout (milliseconds)
    public static final int MAXTRIES = 3;     // Maximum retransmissions

    public static final Integer EPHEMERAL_MIN = 1024;
    public static final Integer EPHEMERAL_MAX = 65535;

}
