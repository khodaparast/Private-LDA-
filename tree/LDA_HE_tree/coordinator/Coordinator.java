package lda.LDA_HE_tree.coordinator;


import java.io.IOException;
import java.util.Vector;
import lda.paillier.*;
/**
 * Created by "P.Khodaparast" on 2018-08-20.
 */
public class Coordinator {

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