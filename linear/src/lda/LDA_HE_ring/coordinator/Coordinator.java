package lda.LDA_HE_ring.coordinator;

import lda.paillier.KeyGen;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by "P.Khodaparast" on 2018-08-20.
 */
public class Coordinator {
public void start() throws IOException {
    KeyGen keyGen = new KeyGen();
    Vector publicKeyGen = keyGen.publicKeyGen();
    Vector privateKey = keyGen.privateKeyGen();

    CoordinatorJMSChannel coordinatorJMSChannel = new CoordinatorJMSChannel();
    coordinatorJMSChannel.setPublicKey(publicKeyGen);
    coordinatorJMSChannel.setPrivateKey(privateKey);
    coordinatorJMSChannel.start();

}
    public static void main(String[] args) throws IOException {
        KeyGen keyGen = new KeyGen();
        Vector publicKeyGen = keyGen.publicKeyGen();
        Vector privateKey = keyGen.privateKeyGen();

        CoordinatorJMSChannel coordinatorJMSChannel = new CoordinatorJMSChannel();
        coordinatorJMSChannel.setPublicKey(publicKeyGen);
        coordinatorJMSChannel.setPrivateKey(privateKey);
        coordinatorJMSChannel.start();

    }

}