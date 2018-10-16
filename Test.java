package lda.paillier;

import java.math.BigInteger;
import java.util.Vector;

/**
 * Created by "P.Khodaparast" on 2018-08-06.
 */
public class Test {

    public static void main(String[] args) {
        KeyGen keyGen = new KeyGen();
        Encryption encryption = new Encryption();
        Decryption decryption = new Decryption();

        Vector pubKey = keyGen.publicKeyGen();
        Vector privateKey = keyGen.privateKeyGen();
        System.out.println(pubKey);

        BigInteger n = (BigInteger) pubKey.get(0);
        BigInteger g = (BigInteger) pubKey.get(1);
        BigInteger r1 = keyGen.randomZStarN();
        BigInteger r2 = keyGen.randomZStarN();
        BigInteger r3 = keyGen.randomZStarN();

        BigInteger lambda = (BigInteger) privateKey.get(0);
        BigInteger mu = (BigInteger) privateKey.get(1);

        BigInteger plainText1 = BigInteger.valueOf(10);
        BigInteger cipher1 = encryption.encrypt(plainText1, n, g, r1);
        BigInteger plainText2 = BigInteger.valueOf(20);
        BigInteger cipher2 = encryption.encrypt(plainText2, n, g, r2);

        BigInteger plainText3 = BigInteger.valueOf(30);
        BigInteger cipher3 = encryption.encrypt(plainText3, n, g, r3);
        BigInteger plainText4 = BigInteger.valueOf(-1024);
        BigInteger cipher4 = encryption.encrypt(plainText4, n, g, r3);

        BigInteger cipher5 = cipher1.multiply(cipher2);
        BigInteger cipher6 = cipher4.multiply(cipher3);
        BigInteger cipher7 = cipher5.multiply(cipher6);


        // Wooooooooooooooooooooooooooow!!!!!! it worked :) multiple of two ciphertext results sum of them



        System.out.println("____________convert BigInteger to String and vice versa for transfer in message__________________");
//        String nString = String.valueOf(n);
//        String gString = String.valueOf(g);
//        BigInteger reversenStr = new BigInteger(nString);
//        BigInteger reversegStr = new BigInteger(nString);
//        System.out.println(reverse);
        System.out.println("___________________________________________________________________________________________________");

//        System.out.println("plain text1:  "+plainText1+" cipher1: " + cipher1+ " decrypted val: "+ decryption.decrypt(cipher1, lambda, mu, n));
//        System.out.println("plain text2:  "+plainText2+" cipher2: " + cipher2+ " decrypted val: "+ decryption.decrypt(cipher2, lambda, mu, n));
//        System.out.println("plain text3:  "+plainText3+" cipher3: " + cipher3+ " decrypted val: "+ decryption.decrypt(cipher3, lambda, mu, n));
//        System.out.println("plain text3:  "+plainText3+" cipher3: " + cipher3+ " decrypted val: "+ decryption.decrypt(cipher3, lambda, mu, n));
        System.out.println( decryption.decrypt2(cipher7, lambda, mu, n));
        System.out.println( decryption.decrypt2(cipher4, lambda, mu, n));
        System.out.println();
//                System.out.println( decryption.decrypt(BigInteger.valueOf(30776), BigInteger.valueOf(176), BigInteger.valueOf(279), BigInteger.valueOf(391)));

    }


}
