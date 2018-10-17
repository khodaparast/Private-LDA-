package lda;

/**
 * Created by "P.Khodaparast" on 2018-09-20.
 */
public final class Constant{
    public  static final String MEAN_SENDER_TO_COORDINATOR_YES="p6.1";
    public  static final String MEAN_SENDER_TO_COORDINATOR_NO="p6.2";
    public  static final String COV_SENDER_TO_COORDINATOR_YES_INT="p6.3";
    public  static final String COV_SENDER_TO_COORDINATOR_YES_DECI="p6.4";
    public  static final String COV_SENDER_TO_COORDINATOR_NO_INT="p6.5";
    public  static final String COV_SENDER_TO_COORDINATOR_NO_DECI="p6.6";

    public  static final String FILE_PATH_PARTY_1="src/lda/LDA_HE_ring/p1/d_1000_000.csv";
    public  static final String FILE_PATH_PARTY_2="src/lda/LDA_HE_ring/p2/d_1000_000.csv";
    public  static final String FILE_PATH_PARTY_3="src/lda/LDA_HE_ring/p3/d_1000_000.csv";
    public  static final String FILE_PATH_PARTY_4="src/lda/LDA_HE_ring/p4/d_1000_000.csv";
    public  static final String FILE_PATH_PARTY_5="src/lda/LDA_HE_ring/p5/d_1000_000.csv";
    public  static final String FILE_PATH_PARTY_6="src/lda/LDA_HE_ring/p6/d_1000_000.csv";

    public  static final String TIME_COMPLEXITY_PATH=  "src/resources/timeComplexity";

    public  static final String FACTORY_NAME="java:comp/DefaultJMSConnectionFactory";
    public  static final String PARTY_1_INPUT_Q="Queue01";
    public  static final String PARTY_1_OUTPUT_Q="Queue02";
    public  static final String PARTY_2_INPUT_Q="Queue02";
    public  static final String PARTY_2_OUTPUT_Q="Queue03";
    public  static final String PARTY_3_INPUT_Q="Queue03";
    public  static final String PARTY_3_OUTPUT_Q="Queue04";
    public  static final String PARTY_4_INPUT_Q="Queue04";
    public  static final String PARTY_4_OUTPUT_Q="Queue05";
    public  static final String PARTY_5_INPUT_Q="Queue05";
    public  static final String PARTY_5_OUTPUT_Q="Queue06";
    public  static final String PARTY_6_INPUT_Q="Queue06";
    public  static final String PARTY_6_OUTPUT_Q="Queue07";

    public  static final String COORDINATOR_RESEIVE_MESSAGE_Q="Queue07";

}
