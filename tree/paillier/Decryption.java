package lda.paillier;

import java.math.BigInteger;

/**
 * Created by "P.Khodaparast" on 2018-08-04.
 */
public class Decryption {
    KeyGen keyGen = new KeyGen();

    public BigInteger decrypt( BigInteger cipherText, BigInteger lambda, BigInteger mu, BigInteger n) {
        return keyGen.decrypt(cipherText, lambda, mu, n);

    }
    public BigInteger decrypt2( BigInteger cipherText, BigInteger lambda, BigInteger mu, BigInteger n) {
        return keyGen.decrypt2(cipherText, lambda, mu, n);

    }


}
