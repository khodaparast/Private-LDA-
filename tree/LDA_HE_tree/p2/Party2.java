package lda.LDA_HE_tree.p2;

import lda.LDA_HE_tree.coordinator.PK;
import lda.classification.TrainCommon;
import lda.paillier.Encryption;

import javax.jms.*;
import javax.naming.InitialContext;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static lda.Constant.*;

/**
 * Created by "P.Khodaparast" on 2018-08-15.
 */
public class Party2 implements MessageListener {
    private String msgProperty = "";
    private HashMap<String, Object> hashMessage = new HashMap<>();
    private ArrayList<BigInteger> arrayMessage;
    private Encryption encryption = new Encryption();
    private ArrayList<ArrayList<BigInteger>> covarianceMsg = new ArrayList<>();
    private PK pk;
    private BigInteger n;
    private BigInteger g;
    private BigInteger r;

    public static void main(String[] args) throws IOException {
        new Party2().start();

    }

    public void start() throws IOException {
        try {
            InitialContext initContext = new InitialContext();
            ConnectionFactory factory =
                    (ConnectionFactory) initContext.lookup(FACTORY_NAME);
            Destination reaDestination = (Destination) initContext.lookup(PARTY_2_INPUT_Q);
            Destination senDestination = (Destination) initContext.lookup(PARTY_2_OUTPUT_Q);

            initContext.close();

            //Create JMS objects
            Connection connection = factory.createConnection();
            Session session =
                    connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender = session.createProducer(senDestination);
            MessageConsumer receiver = session.createConsumer(reaDestination);
            receiver.setMessageListener(this);
            connection.start();

            //Send messages
            ObjectMessage message = session.createObjectMessage();

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
            // Mean

            TrainCommon trainCommon = new TrainCommon(FILE_PATH_PARTY_2);
            HashMap<String, ArrayList> localAttributesMean = trainCommon.getLocalAttributesMean();
            message.setObject(getEncrypted(localAttributesMean.get("mu_of_class_Y_local"), n, g, r));
            message.setStringProperty("sender", "p2.1");
            message.setStringProperty("type", "mean");
            sender.send(message);

            System.out.println("state[1] >>>>>>>>> 0");
            while (hashMessage.get("co.2") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> totalAvgClass_Y = (ArrayList<BigInteger>) hashMessage.get("co.2");
            System.out.println("Total mean class Yes: " + totalAvgClass_Y);

            message.setObject(getEncrypted(localAttributesMean.get("mu_of_class_N_local"), n, g, r));
            message.setStringProperty("sender", "p2.2");
            message.setStringProperty("type", "mean");
            sender.send(message);

            System.out.println("state[2] >>>>>>>>> 0 ");
            while (hashMessage.get("co.3") == null) {
                Thread.sleep(50);
            }
            ArrayList<BigInteger> totalAvgClass_N = (ArrayList<BigInteger>) hashMessage.get("co.3");
            System.out.println("Total mean class No: " + totalAvgClass_N);
//--------------------------------------------------------------------------------------------------------------------------------------
//            covariance Yes
            HashMap<String, ArrayList<ArrayList<BigDecimal>>> covariance_Yes_local = trainCommon.getCovarianceMatrix("yes", totalAvgClass_Y);
            ArrayList<ArrayList<BigDecimal>> cov_Y_Int = covariance_Yes_local.get("yInt");
            ArrayList<ArrayList<BigDecimal>> cov_Y_Deci = covariance_Yes_local.get("yDeci");
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_int_local = new ArrayList<>();
            ArrayList<ArrayList<BigInteger>> en_cov_Yes_deci_local = new ArrayList<>();

            for (int i = 0; i < cov_Y_Int.size(); i++) {
                en_cov_Yes_int_local.add(getEncryptedBigDeci(cov_Y_Int.get(i), n, g, r));
                en_cov_Yes_deci_local.add(getEncryptedBigDeci(cov_Y_Deci.get(i), n, g, r));
            }

            message.setStringProperty("sender", "p2.3");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_Yes_int_local);
            sender.send(message);

            message.setStringProperty("sender", "p2.4");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_Yes_deci_local);
            sender.send(message);

            System.out.println("state[3] >>>>>>>>> 0 ");
            while (hashMessage.get("co.4") == null || hashMessage.get("co.5") == null) {
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> totalCovarianceYesInt = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.4");
            ArrayList<ArrayList<BigInteger>> totalCovarianceYesDeci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get("co.5");
//--------------------------------------------------------------------------------------------------------------------------------------
//            covariance No
            HashMap<String, ArrayList<ArrayList<BigDecimal>>> covariance_No_local = trainCommon.getCovarianceMatrix("no", totalAvgClass_N);
            ArrayList<ArrayList<BigDecimal>> cov_N_Int = covariance_No_local.get("nInt");
            ArrayList<ArrayList<BigDecimal>> cov_N_Deci = covariance_No_local.get("nDeci");
            ArrayList<ArrayList<BigInteger>> en_cov_No_int_local = new ArrayList<>();
            ArrayList<ArrayList<BigInteger>> en_cov_No_deci_local = new ArrayList<>();

            for (int i = 0; i < cov_N_Int.size(); i++) {
                en_cov_No_int_local.add(getEncryptedBigDeci(cov_N_Int.get(i), n, g, r));
                en_cov_No_deci_local.add(getEncryptedBigDeci(cov_N_Deci.get(i), n, g, r));
            }

            message.setStringProperty("sender", "p2.5");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_No_int_local);
            sender.send(message);
            message.setStringProperty("sender", "p2.6");
            message.setStringProperty("type", "covariance");
            message.setObject(en_cov_No_deci_local);
            sender.send(message);

            System.out.println("state[4] >>>>>>>>> 0 ");
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
//--------------------------------------------------------------------------------------------------------------------------------------
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void onMessage(Message message) {

        try {
            ObjectMessage om = (ObjectMessage) message;
            Object obj = om.getObject();
            msgProperty = om.getStringProperty("sender");
            if (obj instanceof PK) {
                pk = (PK) obj;
                hashMessage.put(msgProperty, pk);
            } else if ((obj instanceof ArrayList)) {
                if (om.getStringProperty("type").equalsIgnoreCase("mean")) {
                    arrayMessage = (ArrayList) obj;
                    hashMessage.put(msgProperty, arrayMessage);
                } else if (om.getStringProperty("type").equalsIgnoreCase("covariance")) {
                    covarianceMsg = (ArrayList<ArrayList<BigInteger>>) obj;
                    hashMessage.put(msgProperty, covarianceMsg);
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


    private ArrayList<BigInteger> getEncrypted(ArrayList<Double> values, BigInteger n, BigInteger g, BigInteger r) throws IOException {
        ArrayList<Double> plainText = values;
        ArrayList<BigInteger> encrypted = new ArrayList<>();
        for (int i = 0; i < plainText.size(); i++) {
            BigInteger val = BigDecimal.valueOf(plainText.get(i)).toBigInteger();
            encrypted.add(encryption.encrypt(val, n, g, r));
        }
        return encrypted;
    }

    private BigInteger randomZStarN() {
        BigInteger r;

        do {
            r = new BigInteger(20, new Random());
        }
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);

        return r;
    }
}
