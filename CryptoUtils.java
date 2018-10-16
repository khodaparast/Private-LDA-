package lda.paillier;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by "P.Khodaparast" on 2018-08-04.
 */
public class CryptoUtils {

    public BigInteger getGCD(BigInteger val1, BigInteger val2) {
        return val1.gcd(val2);
    }


    public BigInteger getLCM(BigInteger val1, BigInteger val2) {
        BigInteger temp = val1.multiply(val2);
        CryptoUtils utils = new CryptoUtils();

        return temp.divide(utils.getGCD(val1, val2));
    }


    public BigInteger getPrime(int bitLength) {

        return BigInteger.probablePrime(bitLength, new Random());
    }

    public BigInteger getRandomValue(int bitLength){
        BigInteger randomNum=new BigInteger(bitLength,new Random());
        return randomNum;
    }

    public static void main(String[] args) {
        CryptoUtils utils2 = new CryptoUtils();
        BigInteger val1 = BigInteger.valueOf(12);
        BigInteger val2 = BigInteger.valueOf(16);
        BigInteger val3 = BigInteger.valueOf(2);

//        System.out.println(val1.compareTo(val1));
//        System.out.println(utils2.getGCD(val1, val2));
//        System.out.println(utils2.getLCM(val1, val2));

//---------> modpow():   returns a BigInteger whose value is (thisexponent mod m). Unlike pow, this method permits negative exponents.
//        System.out.println(val3.modPow(BigInteger.valueOf(3),BigInteger.valueOf(5)));
//------------------------------------------------------------------------------------------
        /*
        BigInteger test = BigInteger.valueOf(100);
        System.out.println(test);
        System.out.println(test.bitLength());
        */

/*
        BigInteger b1=BigInteger.valueOf(3);
        BigInteger b2=BigInteger.valueOf(7);
        BigInteger b3=BigInteger.valueOf(9);
        System.out.println(b1.isProbablePrime(4));
        System.out.println(b2.isProbablePrime(1));
        System.out.println(b3.isProbablePrime(4));
*/

// return a prime number with length less than 5
      /*  BigInteger prime = BigInteger.probablePrime(5, new Random());
        System.out.println(prime);
       */
 /*
        BigInteger t=new BigInteger("99",10);
        System.out.println(t);
        System.out.println(t.bitLength());
*/


    }
}
















