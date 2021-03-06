package lda.LDA_HE_tree.p5;

import lda.LDA_HE_tree.coordinator.PK;
import lda.classification.TrainCommon;
import lda.paillier.Encryption;

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
public class Party5 implements MessageListener {
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
        new Party5().start();
    }

    public void start() {

        try {
            InitialContext initContext = new InitialContext();
            ConnectionFactory factory =
                    (ConnectionFactory) initContext.lookup(FACTORY_NAME);
            Destination senDestination = (Destination) initContext.lookup(PARTY_5_OUTPUT_Q);
            Destination reaDestination = (Destination) initContext.lookup(PARTY_5_INPUT_Q);
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
            TrainCommon trainCommon = new TrainCommon(FILE_PATH_PARTY_5);
            HashMap<String, ArrayList> localAttributesMean = trainCommon.getLocalAttributesMean();

            System.out.println("state[1] >>>>>>>>> 0");
            while (hashMessage.get("p3.1") == null || hashMessage.get("p4.1") == null) {//wait here until receive encrypted value of p2
                Thread.sleep(50);
            }
            ArrayList<BigInteger> sumOfEnMean_P12_Y =
                    getSumOfTwoEncryptedList((ArrayList<BigInteger>) hashMessage.get("p3.1"), (ArrayList<BigInteger>) hashMessage.get("p4.1"));
            ArrayList<BigInteger> sumOfEnMean_P123_Y = getSumOfTwoEncryptedList(sumOfEnMean_P12_Y, getEncrypted(localAttributesMean.get("mu_of_class_Y_local"), n, g, r));
            //Send messages
            ObjectMessage message = session.createObjectMessage();
            message.setObject(sumOfEnMean_P123_Y);
            message.setStringProperty("sender", "p5.1");
            message.setStringProperty("type", "mean");
            sender.send(message);

            System.out.println("state[2] >>>>>>>>> 0");
            while (hashMessage.get("co.2") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> totalAvgClass_Y = (ArrayList<BigInteger>) hashMessage.get("co.2");
            System.out.println("state[3] >>>>>>>>> 0");
            while (hashMessage.get("p3.2") == null || hashMessage.get("p4.2") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> sumOfEnMean_P12_N =
                    getSumOfTwoEncryptedList((ArrayList<BigInteger>) hashMessage.get("p3.2"), (ArrayList<BigInteger>) hashMessage.get("p4.2"));
            ArrayList<BigInteger> sumOfEnMean_P123_N = getSumOfTwoEncryptedList(sumOfEnMean_P12_N, getEncrypted(localAttributesMean.get("mu_of_class_N_local"), n, g, r));
            //Send messages
            message.setObject(sumOfEnMean_P123_N);
            message.setStringProperty("sender", "p5.2");
            message.setStringProperty("type", "mean");
            sender.send(message);
            System.out.println("state[4] >>>>>>>>> 0");
            while (hashMessage.get("co.3") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> totalAvgClass_N = (ArrayList<BigInteger>) hashMessage.get("co.3");
//--------------------------------------------------------------------------------------------------------------------------------------
            //covariance Yes
            HashMap<String, ArrayList<ArrayList<BigDecimal>>> covariance_Yes_local = trainCommon.getCovarianceMatrix("yes", totalAvgClass_Y);
            ArrayList<ArrayList<BigDecimal>> cov_Y_Int_local = covariance_Yes_local.get("yInt");
            ArrayList<ArrayList<BigDecimal>> cov_Y_Deci_local = covariance_Yes_local.get("yDeci");

            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_local = new ArrayList<>();
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_local = new ArrayList<>();

            for (int i = 0; i < cov_Y_Int_local.size(); i++) {
                en_cov_Yes_int_local.add(getEncryptedBigDeci(cov_Y_Int_local.get(i), n, g, r));
                en_cov_Yes_deci_local.add(getEncryptedBigDeci(cov_Y_Deci_local.get(i), n, g, r));
            }
            System.out.println("state[4] >>>>>>>>> 0");
            while (hashMessage.get("p3.3") == null || hashMessage.get("p4.3") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_P1 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p3.3");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_P2 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p4.3");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_P13 = new ArrayList<>();
            for (int i = 0; i < en_cov_Yes_int_P1.size(); i++) {
                en_cov_Yes_int_P13.add(getSumOfTwoEncryptedList(en_cov_Yes_int_P1.get(i), en_cov_Yes_int_local.get(i)));
            }
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_P123 = new ArrayList<>();
            for (int i = 0; i < en_cov_Yes_int_P2.size(); i++) {
                en_cov_Yes_int_P123.add(getSumOfTwoEncryptedList(en_cov_Yes_int_P2.get(i), en_cov_Yes_int_P13.get(i)));
            }
            message.setStringProperty("sender", "p5.3");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_Yes_int_P123);
            sender.send(message);

            System.out.println("state[5] >>>>>>>>> 0");
            while (hashMessage.get("p3.4") == null || hashMessage.get("p4.4") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_P1 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p3.4");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_P2 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p4.4");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_P13 = new ArrayList<>();
            for (int i = 0; i < en_cov_Yes_deci_P1.size(); i++) {
                en_cov_Yes_deci_P13.add(getSumOfTwoEncryptedList(en_cov_Yes_deci_P1.get(i), en_cov_Yes_deci_local.get(i)));
            }
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_P123 = new ArrayList<>();
            for (int i = 0; i < en_cov_Yes_deci_P2.size(); i++) {
                en_cov_Yes_deci_P123.add(getSumOfTwoEncryptedList(en_cov_Yes_deci_P2.get(i), en_cov_Yes_deci_P13.get(i)));
            }

            message.setStringProperty("sender", "p5.4");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_Yes_deci_P123);
            sender.send(message);

            System.out.println("state[6] >>>>>>>>> 0 ");
            while (hashMessage.get("co.4") == null || hashMessage.get("co.5") == null) {
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> totalCovarianceYesInt = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.4");
            ArrayList<ArrayList<BigInteger>> totalCovarianceYesDeci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.5");
//--------------------------------------------------------------------------------------------------------------------------------------
            //covariance No
            HashMap<String, ArrayList<ArrayList<BigDecimal>>> covariance_No_local = trainCommon.getCovarianceMatrix("no", totalAvgClass_N);
            ArrayList<ArrayList<BigDecimal>> cov_N_Int_local = covariance_No_local.get("nInt");
            ArrayList<ArrayList<BigDecimal>> cov_N_Deci_local = covariance_No_local.get("nDeci");

            ArrayList<ArrayList<BigInteger>> en_cov_No_int_local = new ArrayList<>();
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_local = new ArrayList<>();

            for (int i = 0; i < cov_N_Int_local.size(); i++) {
                en_cov_No_int_local.add(getEncryptedBigDeci(cov_N_Int_local.get(i), n, g, r));
                en_cov_No_deci_local.add(getEncryptedBigDeci(cov_N_Deci_local.get(i), n, g, r));
            }
            System.out.println("state[4] >>>>>>>>> 0");
            while (hashMessage.get("p3.5") == null || hashMessage.get("p4.5") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_P1 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p3.5");
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_P2 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p4.5");
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_P13 = new ArrayList<>();
            for (int i = 0; i < en_cov_No_int_P1.size(); i++) {
                en_cov_No_int_P13.add(getSumOfTwoEncryptedList(en_cov_No_int_P1.get(i), en_cov_No_int_local.get(i)));
            }
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_P123 = new ArrayList<>();
            for (int i = 0; i < en_cov_No_int_P2.size(); i++) {
                en_cov_No_int_P123.add(getSumOfTwoEncryptedList(en_cov_No_int_P2.get(i), en_cov_No_int_P13.get(i)));
            }

            message.setStringProperty("sender", "p5.5");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_No_int_P123);
            sender.send(message);

            System.out.println("state[5] >>>>>>>>> 0");
            while (hashMessage.get("p3.6") == null || hashMessage.get("p4.6") == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_P1 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p3.6");
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_P2 = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("p4.6");
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_P13 = new ArrayList<>();
            for (int i = 0; i < en_cov_No_deci_P1.size(); i++) {
                en_cov_No_deci_P13.add(getSumOfTwoEncryptedList(en_cov_No_deci_P1.get(i), en_cov_No_deci_local.get(i)));
            }
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_P123 = new ArrayList<>();
            for (int i = 0; i < en_cov_No_deci_P2.size(); i++) {
                en_cov_No_deci_P123.add(getSumOfTwoEncryptedList(en_cov_No_deci_P2.get(i), en_cov_No_deci_P13.get(i)));
            }

            message.setStringProperty("sender", "p5.6");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_No_deci_P123);
            sender.send(message);

            System.out.println("state[6] >>>>>>>>> 0 ");
            while (hashMessage.get("co.6") == null || hashMessage.get("co.7") == null) {
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> totalCovarianceNoInt = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.6");
            ArrayList<ArrayList<BigInteger>> totalCovarianceNoDeci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.7");
            System.out.println("Total Covariance No Int: " + totalCovarianceNoInt);
            System.out.println("Total Covariance No Deci: " + totalCovarianceNoDeci);

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
