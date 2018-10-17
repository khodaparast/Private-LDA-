package lda.LDA_HE_ring.coordinator;

import lda.paillier.Decryption;

import javax.jms.*;
import javax.naming.InitialContext;
import java.io.*;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static lda.Constant.*;


/**
 * Created by "P.Khodaparast" on 2018-08-20.
 */
public class CoordinatorJMSChannel implements MessageListener {
    private Vector publicKey;
    private Vector privateKey;
    private Decryption decryption = new Decryption();
    private BigInteger lambda;
    private BigInteger mu;
    private ArrayList arrayMessage;
    private String msgProperty = "";
    private HashMap<String, Object> hashMessage = new HashMap<>();
    private static long start;
    private static long end;
    private static BufferedWriter bw;
    private static NumberFormat formatter;

    private BigInteger n;
    private BigInteger g;
    private ArrayList<ArrayList<BigInteger>> msgCovariance = new ArrayList<>();

    void setPublicKey(Vector publicKey) {
        this.publicKey = publicKey;
        n = (BigInteger) publicKey.get(0);
        g = (BigInteger) publicKey.get(1);

    }

    void setPrivateKey(Vector privateKey) {
        this.privateKey = privateKey;
        lambda = (BigInteger) privateKey.get(0);
        mu = (BigInteger) privateKey.get(1);
    }

    public void start() throws IOException {

        try {
            InitialContext initContext = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) initContext.lookup(FACTORY_NAME);

            //Create JMS objects
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sender1 = session.createProducer((Destination) initContext.lookup("Queue01"));
            MessageProducer sender2 = session.createProducer((Destination) initContext.lookup("Queue02"));
            MessageProducer sender3 = session.createProducer((Destination) initContext.lookup("Queue03"));
            MessageProducer sender4 = session.createProducer((Destination) initContext.lookup("Queue04"));
            MessageProducer sender5 = session.createProducer((Destination) initContext.lookup("Queue05"));
            MessageProducer sender6 = session.createProducer((Destination) initContext.lookup("Queue06"));
            MessageProducer[] senders = new MessageProducer[]{sender1, sender2, sender3,sender4,sender5,sender6};

            MessageConsumer receiver = session.createConsumer((Destination) initContext.lookup(COORDINATOR_RESEIVE_MESSAGE_Q));
            receiver.setMessageListener(this);
            initContext.close();
            connection.start();

            //Send messages
            ObjectMessage message = session.createObjectMessage();
            PK pk = new PK();
            pk.setN(n);
            pk.setG(g);
            System.out.println("n: " + n + "    g: " + g);
            message.setObject(pk);
            message.setStringProperty("sender", "co.1");
            sendObjMessage(senders, message);
            start = System.currentTimeMillis();
            System.out.println("state[0] >>>>>> 0");
            while (hashMessage.get(MEAN_SENDER_TO_COORDINATOR_YES) == null) {// wait until P3 sends encrypted mean of class Yes
                Thread.sleep(50);
            }
            // do decryption mean of class Yes
            ArrayList<BigInteger> meanOfClassYes = (ArrayList<BigInteger>) hashMessage.get(MEAN_SENDER_TO_COORDINATOR_YES);
            ArrayList<BigInteger> de_meanOfClassYes = getDecrypted(meanOfClassYes);
            ArrayList<BigInteger> final_mu_class_Yes = new ArrayList<>();
            final_mu_class_Yes.add(de_meanOfClassYes.get(0));
            for (int i = 1; i < de_meanOfClassYes.size(); i++) {
                final_mu_class_Yes.add(de_meanOfClassYes.get(i).divide(BigInteger.valueOf(3)));
            }
            message.setObject(final_mu_class_Yes);
            message.setStringProperty("sender", "co.2");
            message.setStringProperty("type", "mean");
            sendObjMessage(senders, message);

            System.out.println("state[1] >>>>>> 0 ");
            while (hashMessage.get(MEAN_SENDER_TO_COORDINATOR_NO) == null) {// wait until P3 sends encrypted mean of class No
                Thread.sleep(50);
            }
            ArrayList<BigInteger> meanOfClassNo = (ArrayList<BigInteger>) hashMessage.get(MEAN_SENDER_TO_COORDINATOR_NO);
            ArrayList<BigInteger> de_meanOfClassNo = getDecrypted(meanOfClassNo);
            ArrayList<BigInteger> final_mu_class_No = new ArrayList<>();
            final_mu_class_No.add(de_meanOfClassNo.get(0));
            for (int i = 1; i < de_meanOfClassNo.size(); i++) {
                final_mu_class_No.add(de_meanOfClassNo.get(i).divide(BigInteger.valueOf(3)));
            }
            message.setObject(de_meanOfClassNo);
            message.setStringProperty("sender", "co.3");
            message.setStringProperty("type", "mean");
            sendObjMessage(senders, message);
            System.out.println("Total Mean of No: " + final_mu_class_No);

//-----------------------------------------------------------------------------------------------------------------------------------------------------
//Covariance Yes
            System.out.println("state[2] >>>>>>>> 0 ");
            while (hashMessage.get(COV_SENDER_TO_COORDINATOR_YES_INT) == null) { // wait until party 3 sends encrypted of  covariance yes
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_allParties_Y_int = (ArrayList<ArrayList<BigInteger>>) hashMessage.get(COV_SENDER_TO_COORDINATOR_YES_INT);
            ArrayList<ArrayList<BigInteger>> de_cov_allParties_Yes_int = new ArrayList<>();
            for (int i = 0; i < en_cov_allParties_Y_int.size(); i++) {
                de_cov_allParties_Yes_int.add(getDecrypted(en_cov_allParties_Y_int.get(i)));
            }
            message.setObject(de_cov_allParties_Yes_int);
            message.setStringProperty("sender", "co.4");
            message.setStringProperty("type", "covariance");
            sendObjMessage(senders, message);

            System.out.println("state[3] >>>>>>>> 0 ");
            while (hashMessage.get(COV_SENDER_TO_COORDINATOR_YES_DECI) == null) { // wait until party 3 sends encrypted of  covariance yDeci
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> en_cov_allParties_Y_deci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get(COV_SENDER_TO_COORDINATOR_YES_DECI);
            ArrayList<ArrayList<BigInteger>> de_cov_allParties_Yes_deci = new ArrayList<>();
            for (int i = 0; i < en_cov_allParties_Y_deci.size(); i++) {
                de_cov_allParties_Yes_deci.add(getDecrypted(en_cov_allParties_Y_deci.get(i)));
            }
            message.setObject(de_cov_allParties_Yes_deci);
            message.setStringProperty("sender", "co.5");
            message.setStringProperty("type", "covariance");
            sendObjMessage(senders, message);

//-----------------------------------------------------------------------------------------------------------------------------------------------------
//Covariance No

            System.out.println("state[2] >>>>>>>> 0 ");
            while (hashMessage.get(COV_SENDER_TO_COORDINATOR_NO_INT) == null) {
                Thread.sleep(50);
            }
            ArrayList<ArrayList<BigInteger>> en_cov_allParties_N_int = (ArrayList<ArrayList<BigInteger>>) hashMessage.get(COV_SENDER_TO_COORDINATOR_NO_INT);
            ArrayList<ArrayList<BigInteger>> de_cov_allParties_No_int = new ArrayList<>();
            for (int i = 0; i < en_cov_allParties_N_int.size(); i++) {
                de_cov_allParties_No_int.add(getDecrypted(en_cov_allParties_N_int.get(i)));
            }
            message.setObject(de_cov_allParties_No_int);
            message.setStringProperty("sender", "co.6");
            message.setStringProperty("type", "covariance");
            sendObjMessage(senders, message);

            System.out.println("state[3] >>>>>>>> 0 ");
            while (hashMessage.get(COV_SENDER_TO_COORDINATOR_NO_DECI) == null) {
                Thread.sleep(50);
            }

            ArrayList<ArrayList<BigInteger>> en_cov_allParties_N_deci = (ArrayList<ArrayList<BigInteger>>) hashMessage.get(COV_SENDER_TO_COORDINATOR_NO_DECI);
            ArrayList<ArrayList<BigInteger>> de_cov_allParties_No_deci = new ArrayList<>();
            for (int i = 0; i < en_cov_allParties_N_deci.size(); i++) {
                de_cov_allParties_No_deci.add(getDecrypted(en_cov_allParties_N_deci.get(i)));
            }
            message.setObject(de_cov_allParties_No_deci);
            message.setStringProperty("sender", "co.7");
            message.setStringProperty("type", "covariance");
            sendObjMessage(senders, message);

            System.out.println("Total covariance No deci: \n" + de_cov_allParties_No_deci);
            end = System.currentTimeMillis();
            bw.write("\n Execution time of total mean and covariance is: " + formatter.format((end - start) / 1000d) + " seconds \n");
            bw.close();
//-----------------------------------------------------------------------------------------------------------------------------------------------------
            while (true) {
                Thread.sleep(1000);
                System.out.println("true ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sendObjMessage(MessageProducer[] senders, ObjectMessage message) throws JMSException {
        for (int i = 0; i < senders.length; i++) {
            senders[i].send(message);
        }
    }

    private ArrayList<BigInteger> getDecrypted(ArrayList<BigInteger> ciphertextList) throws IOException {
        ArrayList<BigInteger> decryptedList = new ArrayList<>();
        for (int i = 0; i < ciphertextList.size(); i++) {
            decryptedList.add(decryption.decrypt2(ciphertextList.get(i), lambda, mu, n));
        }
        return decryptedList;
    }

    public void onMessage(Message message) {

        try {
            ObjectMessage om = (ObjectMessage) message;
            Object obj = om.getObject();
            msgProperty = om.getStringProperty("sender");
            if (obj instanceof ArrayList) {
                if (om.getStringProperty("type").equalsIgnoreCase("mean")) {

                    arrayMessage = (ArrayList) obj;
                    hashMessage.put(msgProperty, arrayMessage);

                } else if (om.getStringProperty("type").equalsIgnoreCase("covariance")) {

                    msgCovariance = (ArrayList<ArrayList<BigInteger>>) obj;
                    hashMessage.put(msgProperty, msgCovariance);

                }
            }

        } catch (Exception e) {

        }
    }
}