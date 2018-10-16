package lda.LDA_HE_tree.coordinator;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by "P.Khodaparast" on 2018-08-28.
 */
public class PK implements Serializable {
    BigInteger n;
    BigInteger g;
    BigInteger lambda;
    BigInteger mu;


    public void setLambda(BigInteger lambda) {
        this.lambda = lambda;
    }
    public void setMu(BigInteger mu) {
        this.mu = mu;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }
}
