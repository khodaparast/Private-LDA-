package lda.LDA_HE_tree.p3;

import lda.classification.TrainCommon;
import lda.paillier.Encryption;
import lda.LDA_HE_tree.coordinator.PK;
import javax.jms.*;
import javax.naming.InitialContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import static lda.Constant.*;

/**
 * Created by "P.Khodaparast" on 2018-08-15.
 */
public class Party3 implements MessageListener {
    private String msgProperty = "";
    private HashMap<String, Object> hashMessage = new HashMap<>();
    private ArrayList<BigInteger> meanMsg;
    private ArrayList<ArrayList<BigInteger>> covarianceMsg = new ArrayList<>();
    private Encryption encryption = new Encryption();
    private PK pk;
    private BigInteger n;
    private BigInteger g;
    private BigInteger r;

    public static void main(String[] args) {
        new Party3().start();
    }

    public void start() {

        try {
            InitialContext initContext = new InitialContext();
            ConnectionFactory factory =
                    (ConnectionFactory) initContext.lookup(FACTORY_NAME);
            Destination senDestination = (Destination) initContext.lookup(PARTY_3_OUTPUT_Q);
            Destination reaDestination = (Destination) initContext.lookup(PARTY_3_INPUT_Q);
            initContext.close();

            //Create JMS objects
            Connection connection = factory.createConnection();
            Session session =
                    connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(senDestination);

            MessageConsumer receiver = session.createConsumer(reaDestination);
            receiver.setMessageListener(this);
            connection.start();

            System.out.println("state[0] >>>>>>>>> 0");
            while (hashMessage.get("co.1") == null) {
                Thread.sleep(50);
            }
            PK pkMsg = (PK) hashMessage.get("co.1");
            n = pkMsg.getN();
            g = pkMsg.getG();
            r = randomZStarN();
            System.out.println("n: " + n + "    g: " + g);
//--------------------------------------------------------------------------------------------------------------------------------------
//Mean
            TrainCommon trainCommon=new TrainCommon(FILE_PATH_PARTY_3);
            HashMap<String, ArrayList> localAttributesMean = trainCommon.getLocalAttributesMean();

            System.out.println("state[1] >>>>>>>>> 0");
            while (hashMessage.get("p1.1") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> sumOfTwoEncryptedMean_Y = getSumOfTwoEncryptedList((ArrayList<BigInteger>) hashMessage.get("p1.1"), getEncrypted(localAttributesMean.get("mu_of_class_Y_local"), n, g, r));
            //Send messages
            ObjectMessage message = session.createObjectMessage();
            message.setObject(sumOfTwoEncryptedMean_Y);
            message.setStringProperty("sender", "p3.1");
            message.setStringProperty("type", "mean");
            sender.send(message);

            System.out.println("state[2] >>>>>>>>> 0");
            while (hashMessage.get("co.2") == null) {
                Thread.sleep(50);
            }

            ArrayList<BigInteger> totalAvgClass_Y = (ArrayList<BigInteger>) hashMessage.get("co.2");

            System.out.println("state[3] >>>>>>>>> 0");
            while (hashMessage.get("p1.2") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> sumOfTwoEncryptedMean_N = getSumOfTwoEncryptedList((ArrayList<BigInteger>) hashMessage.get("p1.2"), getEncrypted(localAttributesMean.get("mu_of_class_N_local"), n, g, r));
            message.setObject(sumOfTwoEncryptedMean_N);
            message.setStringProperty("sender", "p3.2");
            message.setStringProperty("type", "mean");
            sender.send(message);

            System.out.println("state[4] >>>>>>>>> 0");
            while (hashMessage.get("co.3") == null) {
                Thread.sleep(50);
            }

            ArrayList<BigInteger> totalAvgClass_N = (ArrayList<BigInteger>) hashMessage.get("co.3");
//--------------------------------------------------------------------------------------------------------------------------------------
            //covariance Yes
            HashMap<String,ArrayList<ArrayList<BigDecimal>>> covariance_Yes_local = trainCommon.getCovarianceMatrix("yes", totalAvgClass_Y);
            ArrayList<ArrayList<BigDecimal>> cov_Y_Int_local=covariance_Yes_local.get("yInt");
            ArrayList<ArrayList<BigDecimal>> cov_Y_Deci_local=covariance_Yes_local.get("yDeci");

            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_local = new ArrayList<>();
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_local = new ArrayList<>();

            for (int i = 0; i < cov_Y_Int_local.size(); i++) {
                en_cov_Yes_int_local.add(getEncryptedBigDeci(cov_Y_Int_local.get(i), n, g, r));
                en_cov_Yes_deci_local.add(getEncryptedBigDeci(cov_Y_Deci_local.get(i), n, g, r));
            }
            System.out.println("state[4] >>>>>>>>> 0");
            while (hashMessage.get("p1.3") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_P1= (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p1.3");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_P23=new ArrayList<>();
            for (int i = 0; i < en_cov_Yes_int_P1.size(); i++) {
                en_cov_Yes_int_P23.add(getSumOfTwoEncryptedList(en_cov_Yes_int_P1.get(i), en_cov_Yes_int_local.get(i)));
            }
            message.setStringProperty("sender", "p3.3");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_Yes_int_P23);
            sender.send(message);

            System.out.println("state[5] >>>>>>>>> 0");
            while (hashMessage.get("p1.4") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_P1= (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p1.4");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_P23=new ArrayList<>();
            for (int i = 0; i < en_cov_Yes_deci_P1.size(); i++) {
                en_cov_Yes_deci_P23.add(getSumOfTwoEncryptedList(en_cov_Yes_deci_P1.get(i), en_cov_Yes_deci_local.get(i)));
            }

            message.setStringProperty("sender", "p3.4");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_Yes_deci_P23);
            sender.send(message);

            System.out.println("state[6] >>>>>>>>> 0 ");
            while (hashMessage.get("co.4") == null || hashMessage.get("co.5") == null) {
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> totalCovarianceYesInt = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.4");
            ArrayList<ArrayList<BigInteger>> totalCovarianceYesDeci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.5");
//--------------------------------------------------------------------------------------------------------------------------------------
            //covariance No
            HashMap<String,ArrayList<ArrayList<BigDecimal>>> covariance_No_local = trainCommon.getCovarianceMatrix("no", totalAvgClass_N);
            ArrayList<ArrayList<BigDecimal>> cov_N_Int_local=covariance_No_local.get("nInt");
            ArrayList<ArrayList<BigDecimal>> cov_N_Deci_local=covariance_No_local.get("nDeci");

            ArrayList<ArrayList<BigInteger>> en_cov_No_int_local = new ArrayList<>();
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_local = new ArrayList<>();

            for (int i = 0; i < cov_N_Int_local.size(); i++) {
                en_cov_No_int_local.add(getEncryptedBigDeci(cov_N_Int_local.get(i), n, g, r));
                en_cov_No_deci_local.add(getEncryptedBigDeci(cov_N_Deci_local.get(i), n, g, r));
            }
            System.out.println("state[4] >>>>>>>>> 0");
            while (hashMessage.get("p1.5") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_P1= (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p1.5");
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_P12=new ArrayList<>();
            for (int i = 0; i < en_cov_No_int_P1.size(); i++) {
                en_cov_No_int_P12.add(getSumOfTwoEncryptedList(en_cov_No_int_P1.get(i), en_cov_No_int_local.get(i)));
            }
            message.setStringProperty("sender", "p3.5");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_No_int_P12);
            sender.send(message);

            System.out.println("state[5] >>>>>>>>> 0");
            while (hashMessage.get("p1.6") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_P2= (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p1.6");
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_P23=new ArrayList<>();
            for (int i = 0; i < en_cov_No_deci_P2.size(); i++) {
                en_cov_No_deci_P23.add(getSumOfTwoEncryptedList(en_cov_No_deci_P2.get(i), en_cov_No_deci_local.get(i)));
            }
            message.setStringProperty("sender", "p3.6");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_No_deci_P23);
            sender.send(message);

            System.out.println("state[6] >>>>>>>>> 0 ");
            while (hashMessage.get("co.6") == null || hashMessage.get("co.7") == null) {
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> totalCovarianceNoInt = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.6");
            ArrayList<ArrayList<BigInteger>> totalCovarianceNoDeci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.7");
//--------------------------------------------------------------------------------------------------------------------------------------
            while (true) {
                Thread.sleep(1000);
                System.out.println("true ");
            }
// ---------------------------------------------------------------------------------------------------------------------------------------
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private ArrayList<BigInteger> getSumOfTwoEncryptedList(ArrayList<BigInteger> cipherArray1, ArrayList<BigInteger> cipherArray2) {
        ArrayList<BigInteger> sumOfTwoCipherArray = new ArrayList<>();

        for (int i = 0; i < cipherArray1.size(); i++) {
            BigInteger cipher1 = cipherArray1.get(i);
            BigInteger cipher2 = cipherArray2.get(i);
            BigInteger cipnerSum = cipher1.multiply(cipher2);
            sumOfTwoCipherArray.add(cipnerSum);
        }
        return sumOfTwoCipherArray;
    }

    public void onMessage(Message message) {
        try {

            if (message instanceof ObjectMessage) {

                ObjectMessage om = (ObjectMessage) message;
                Object obj = om.getObject();
                msgProperty = om.getStringProperty("sender");
                if (obj instanceof PK) {
                    pk = (PK) obj;
                    hashMessage.put(msgProperty, pk);
                } else if ((obj instanceof ArrayList)) {
                    if (om.getStringProperty("type").equalsIgnoreCase("mean")) {
                        meanMsg = (ArrayList) obj;
                        hashMessage.put(msgProperty, meanMsg);
                    } else if (om.getStringProperty("type").equalsIgnoreCase("covariance")) {
                        covarianceMsg = (ArrayList<ArrayList<BigInteger>>) obj;
                        hashMessage.put(msgProperty, covarianceMsg);

                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private ArrayList<BigInteger> getEncryptedBigDeci(ArrayList<BigDecimal> values, BigInteger n, BigInteger g, BigInteger r) throws IOException {
        ArrayList<BigDecimal> plainText = values;
        ArrayList<BigInteger> encrypted = new ArrayList<>();
        for (int i = 0; i < plainText.size(); i++) {

            BigInteger val = (plainText.get(i)).toBigInteger();
            encrypted.add(encryption.encrypt(val, n, g, r));
        }
        return encrypted;
    }

    private ArrayList<BigInteger> getEncrypted(ArrayList<Double> values, BigInteger n, BigInteger g, BigInteger r) {
        ArrayList<Double> plainText = values;
        ArrayList<BigInteger> encrypted = new ArrayList<>();
        for (int i = 0; i < plainText.size(); i++) {
            BigInteger val = BigDecimal.valueOf(plainText.get(i)).toBigInteger();
            encrypted.add(encryption.encrypt(val, n, g, r));
        }
        return encrypted;
    }

    BigInteger randomZStarN() {
        BigInteger r;

        do {
            r = new BigInteger(20, new Random());
        }
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);

        return r;
    }
}
