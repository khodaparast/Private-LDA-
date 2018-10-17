package lda.LDA_HE_ring.coordinator;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by "P.Khodaparast" on 2018-08-28.
 */
public class PK implements Serializable {
    BigInteger n;
    BigInteger g;

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
