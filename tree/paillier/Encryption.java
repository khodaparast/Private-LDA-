package lda.paillier;


import java.math.BigInteger;

/**
 * Created by "P.Khodaparast" on 2018-08-04.
 */
public class Encryption {

    private BigInteger cipherText;


    public BigInteger encrypt(BigInteger message, BigInteger n, BigInteger g, BigInteger r) {
        // c = g^m * r^n mod n^2

        BigInteger nsquare = n.multiply(n);
        cipherText = (g.modPow(message, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
        return cipherText;
    }





}





























