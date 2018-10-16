package lda.classification;

import Jama.Matrix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by "P.Khodaparast" on 2018-08-08.
 */
public class LDAUtilsCommon {

    public double getMean(ArrayList<Double> values) {
        double result = 0;
        for (int i = 0; i < values.size(); i++) {
            result += values.get(i);
        }
        result = result / values.size();

        int precision = 100; //keep 2 digits
        result = Math.floor(result * precision + .5) / precision;
        return result;
    }

    public double getMean(int[] values) {
        double result = 0;
        for (int i = 0; i < values.length; i++) {
            result += values[i];
        }
        result = result / values.length;

        int precision = 100; //keep 2 digits
        result = Math.floor(result * precision + .5) / precision;
        return result;
    }

    public BigDecimal[] getCovariance(ArrayList<Double> val1, ArrayList<Double> val2, double avg_val_1, double avg_val_2) {

        double avg_val1 = avg_val_1;
        double avg_val2 = avg_val_2;
        double sum = 0;
        for (int i = 0; i < val1.size(); i++) {
            sum += (val1.get(i) - avg_val1) * (val2.get(i) - avg_val2);

        }
        sum = sum / val1.size();
        double precision = 10000; //keep 4 decimal
        double result = Math.floor(sum * precision + .5) / precision;
        String[] val5 = String.valueOf(result).split("\\.");
        BigDecimal[] cov=new BigDecimal[2];
                cov[0]=(BigDecimal.valueOf(Long.parseLong(val5[0])));// integer part of covariance
                cov[1]=(BigDecimal.valueOf(Long.parseLong(val5[1]))); // decimal part of covariance

        return cov;
    }

    // total covariance: sum of each class covariance
    public double[] getTotalCovariance(double[] cov_Y, double[] cov_N, double number_of_Y, double number_of_N) {
        double[] totalCov = new double[3];
        double zaribY = (number_of_Y / (number_of_Y + number_of_N));
        double zaribN = (number_of_N / (number_of_Y + number_of_N));
        int precision = 100; //keep 2 digits

        for (int i = 0; i < cov_Y.length; i++) {
            totalCov[i] = (zaribY * cov_Y[i]) + (zaribN * cov_N[i]);
            totalCov[i] = Math.floor(totalCov[i] * precision + .5) / precision;
        }
        return totalCov;
    }
    // total covariance: sum of each class covariance
    public ArrayList<Double> getTotalCovariance(ArrayList<Double> cov_Y, ArrayList<Double> cov_N, double number_of_Y, double number_of_N) {
        ArrayList<Double> totalCov = new ArrayList();
        double zaribY = (number_of_Y / (number_of_Y + number_of_N));
        double zaribN = (number_of_N / (number_of_Y + number_of_N));
        int precision = 100; //keep 2 digits
        for (int i = 0; i < cov_Y.size(); i++) {
            double temp = ((zaribY * cov_Y.get(i)) + (zaribN * cov_N.get(i)));
            totalCov.add(Math.floor(temp * precision + .5) / precision);
        }

        return totalCov;
    }


    public Matrix getTotalCovariance(Matrix cov_Y, Matrix cov_N, double number_of_Y, double number_of_N) {
        Matrix totalCov = cov_Y.times((number_of_Y / (number_of_Y + number_of_N))).plus(cov_N.times((number_of_N / (number_of_Y + number_of_N))));
        return totalCov;
    }

    public Matrix getMatrixInvere(double[][] matrix) {
        Matrix inputMatrix = new Matrix(matrix);
        Matrix inverse = inputMatrix.inverse();
        return inverse;
    }

    public Matrix getMatrixInvere(Matrix matrix) {
        Matrix inverse = matrix.inverse();
        return inverse;
    }

    public double[] getInverseCov(double[] totalCovariance) {
        double[] inverseCov = new double[3];
        int precision = 10000; //keep 4 digits

        double zarib = 1 / ((totalCovariance[0] * totalCovariance[2]) - (totalCovariance[1] * totalCovariance[1]));
        for (int i = 0; i < totalCovariance.length; i++) {
            inverseCov[i] = zarib * totalCovariance[i];
            inverseCov[i] = Math.floor(inverseCov[i] * precision + .5) / precision;
        }
        double temp = inverseCov[0];
        inverseCov[0] = inverseCov[2];
        inverseCov[2] = temp;
        inverseCov[1] = -inverseCov[1];
        return inverseCov;
    }

    public double[] getBetas(double[] inversedCov, double[] muOfAttributeInEachClass) {

        double[] betas = new double[2];
        double mu_Temp_Y = muOfAttributeInEachClass[0];
        double mu_Temp_N = muOfAttributeInEachClass[1];
        double mu_Hum_Y = muOfAttributeInEachClass[2];
        double mu_Hum_N = muOfAttributeInEachClass[3];


        double b1 = inversedCov[0] * Math.abs(mu_Temp_Y - mu_Temp_N) + inversedCov[1] * Math.abs(mu_Hum_Y - mu_Hum_N);
        double b2 = inversedCov[1] * Math.abs(mu_Temp_Y - mu_Temp_N) + inversedCov[2] * Math.abs(mu_Hum_Y - mu_Hum_N);
        betas[0] = b1;
        betas[1] = b2;
        return betas;
    }

    public double[] getZs(HashMap<Integer, Double> betas, HashMap<Integer, Double> means) {

        double[] results = new double[3];
        double b1 = betas.get(1);
        double b2 = betas.get(2);
        double b3 = betas.get(3);
        double b4 = betas.get(4);
        double b5 = betas.get(5);
        double b6 = betas.get(6);
        double b7 = betas.get(7);
        double b8 = betas.get(8);


        double Z_0 = b1 * ((means.get(1) + means.get(9)) / 2) +
                b2 * ((means.get(2) + means.get(10)) / 2) +
                b3 * ((means.get(3) + means.get(11)) / 2) +
                b4 * ((means.get(4) + means.get(12)) / 2) +
                b5 * ((means.get(5) + means.get(13)) / 2) +
                b6 * ((means.get(6) + means.get(14)) / 2) +
                b7 * ((means.get(7) + means.get(15)) / 2) +
                b8 * ((means.get(8) + means.get(16)) / 2);

        double Z_Y = b1 * means.get(1) + b2 * means.get(2) + b3 * means.get(3) + b4 * means.get(4) +
                b5 * means.get(5) + b6 * means.get(6) + b7 * means.get(7) + b8 * means.get(8);
        double Z_N = b1 * means.get(9) + b2 * means.get(10) + b3 * means.get(11) + b4 * means.get(12) +
                b5 * means.get(13) + b6 * means.get(14) + b7 * means.get(15) + b8 * means.get(16);
        results[0] = Z_0;
        results[1] = Z_Y;
        results[2] = Z_N;
        return results;
    }

    public String classify(double[] newInstanse, double[] Zs, HashMap<Integer, Double> betas) {
        double Z_0 = Zs[0];
        double Z_Y = Zs[1];
        double Z_N = Zs[2];
        double Z = 0;
        for (int i = 0; i < newInstanse.length - 1; i++) {
            Z = Z + betas.get(i + 1) * newInstanse[i];
        }
//        Z= betas.get(1) * newInstanse[0] + betas.get(2) * newInstanse[1];

        if (Z_Y > Z_N && Z > Z_0) {
//            System.out.println("(Z_Y > Z_N && Z > Z_0)");
            return "1";
        }
        if (Z_Y <= Z_N && Z > Z_0) {
//            System.out.println("(Z_Y <= Z_N && Z > Z_0)");
            return "0";
        }

        if (Z_Y > Z_N && Z < Z_0) {
//            System.out.println("(Z_Y > Z_N && Z < Z_0)");
            return "0";
        }


        if (Z_Y <= Z_N && Z < Z_0) {
//            System.out.println("(Z_Y <= Z_N && Z < Z_0)");
            return "1";
        } else return "UnKnown";
    }

}
