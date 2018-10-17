package lda.paillier;

import java.math.BigInteger;
import java.util.Random;
import java.util.Vector;

/**
 * Created by "P.Khodaparast" on 2018-08-04.
 */
public class KeyGen {
    CryptoUtils utils = new CryptoUtils();
    // p and q different from each other but same length
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger nsquare;
    private BigInteger g;
    private BigInteger lambda; // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
    private BigInteger mu;
    //    private final int MOD_LENGTH = 20;
    private final int MOD_LENGTH = 1024;
    private final int CERTAINTY = 64;


    public Vector publicKeyGen() {
        //(n,g)

        p = new BigInteger(MOD_LENGTH / 2, CERTAINTY, new Random());
        do {
            q = new BigInteger(MOD_LENGTH / 2, CERTAINTY, new Random());
        } while (p.equals(q));
        n = p.multiply(q);
        nsquare = n.multiply(n);
        // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
        lambda = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))).
                divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        do {
            g = randZstarNSquare();
//        validate g: gcd(L(g^lambda mod n^2), n)=1
        } while (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1);
        Vector publicKey = new Vector();
        publicKey.add(n);
        publicKey.add(g);

        return publicKey;
    }

    public Vector privateKeyGen() {
//    (lambda, mu)
// lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
        lambda = utils.getLCM(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
        // mu = (L(g^lambda mod n^2))^{-1} mod n, where L(u) = (u-1)/n
        mu = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        Vector sk = new Vector();
        sk.add(lambda);
        sk.add(mu);
        return sk;
    }


    public BigInteger randZstarNSquare() {
        BigInteger r;
// --> Z* for non-zero integers
        do {
// random value of r is membert of Z*_{n^2}, it should not be 1 because of g condition if public key generation
            r = utils.getRandomValue(MOD_LENGTH * 2);
//            r.compareTo(nsquare) --> compare value of r and nsquare, not be equal
        } while (r.compareTo(nsquare) >= 0 || r.gcd(nsquare).intValue() != 1);
        return r;
    }


    public BigInteger randomZStarN() {
        BigInteger r;

        do {
            r = new BigInteger(MOD_LENGTH, new Random());
        }
        while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);

        return r;
    }


    public BigInteger decrypt(BigInteger cipherText, BigInteger lambda, BigInteger mu, BigInteger n) {
        nsquare = n.multiply(n);
        // m = L(c^lambda mod n^2) * mu mod n, where L(u) = (u-1)/n
        return cipherText.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(mu).mod(n);

    }

    public BigInteger decrypt2(BigInteger cipherText, BigInteger lambda, BigInteger mu, BigInteger n) {
        nsquare = n.multiply(n);
        // m = L(c^lambda mod n^2) * mu mod n, where L(u) = (u-1)/n
        BigInteger x = cipherText.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(mu).mod(n);
        x = x.add(BigInteger.valueOf(MOD_LENGTH).divide(BigInteger.valueOf(2))).mod(n).subtract(BigInteger.valueOf(MOD_LENGTH).divide(BigInteger.valueOf(2)));

        return x;
    }


}
