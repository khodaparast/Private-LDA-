package lda.classification;

import Jama.Matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by "P.Khodaparast" on 2018-08-18.
 */
public class TrainCommon {
    public TrainCommon() {
    }

    public TrainCommon(String filePath) {
        this.filePath=filePath;
    }

    private double[] mu_of_class_Y_local;
    private double[] mu_of_class_N_local;
    private ArrayList totalMeans_Y = new ArrayList();
    private ArrayList totalMeans_N = new ArrayList();
    private ArrayList<ArrayList<Double>> list_of_attributes_Y = new ArrayList<>();
    private ArrayList<ArrayList<Double>> list_of_attributes_N = new ArrayList<>();

    private double count_Y = 0;
    private double count_N = 0;
    private LDAUtilsCommon utilsCommon = new LDAUtilsCommon();

    private Matrix totalCovariance;
    private  String filePath;
    private BufferedReader br;

    private int countInstance=0;

    public HashMap<String, ArrayList> getLocalAttributesMean() throws Exception {
        HashMap<String, ArrayList> result = new HashMap<>();



        br = new BufferedReader(new FileReader(new File(filePath)));
        String line;

        ArrayList<Double> pregnancyHistory_Y = new ArrayList<>();
        ArrayList<Double> plasma_Y = new ArrayList<>();
        ArrayList<Double> bloodPressure_Y = new ArrayList<>();
        ArrayList<Double> skinThickness_Y = new ArrayList<>();
        ArrayList<Double> insulin_Y = new ArrayList<>();
        ArrayList<Double> bodyMass_Y = new ArrayList<>();
        ArrayList<Double> pedigree_Y = new ArrayList<>();
        ArrayList<Double> age_Y = new ArrayList<>();

        ArrayList<Double> pregnancyHistory_N = new ArrayList<>();
        ArrayList<Double> plasma_N = new ArrayList<>();
        ArrayList<Double> bloodPressure_N = new ArrayList<>();
        ArrayList<Double> skinThickness_N = new ArrayList<>();
        ArrayList<Double> insulin_N = new ArrayList<>();
        ArrayList<Double> bodyMass_N = new ArrayList<>();
        ArrayList<Double> pedigree_N = new ArrayList<>();
        ArrayList<Double> age_N = new ArrayList<>();
        double count_Y = 0;
        double count_N = 0;
        int count = 0;
        while ((line = br.readLine()) != null) {

            String[] attributes = line.split(",");
            String classLable = attributes[attributes.length - 1];

            if (classLable.trim().equalsIgnoreCase("1")) {
                pregnancyHistory_Y.add(Double.valueOf(attributes[0]));
                plasma_Y.add(Double.valueOf(attributes[1]));
                bloodPressure_Y.add(Double.valueOf(attributes[2]));
                skinThickness_Y.add(Double.valueOf(attributes[3]));
                insulin_Y.add(Double.valueOf(attributes[4]));
                bodyMass_Y.add(Double.valueOf(attributes[5]));
                pedigree_Y.add(Double.valueOf(attributes[6]));
                age_Y.add(Double.valueOf(attributes[7]));
                count_Y = count_Y + 1;

            } else {
                pregnancyHistory_N.add(Double.valueOf(attributes[0]));
                plasma_N.add(Double.valueOf(attributes[1]));
                bloodPressure_N.add(Double.valueOf(attributes[2]));
                skinThickness_N.add(Double.valueOf(attributes[3]));
                insulin_N.add(Double.valueOf(attributes[4]));
                bodyMass_N.add(Double.valueOf(attributes[5]));
                pedigree_N.add(Double.valueOf(attributes[6]));
                age_N.add(Double.valueOf(attributes[7]));
                count_N = count_N + 1;
            }
            countInstance = countInstance + 1;
            if (countInstance>1000000)break;
        }

        // add separated instances into two different ArrayList for the next phase that calculation of Covariance become more easy
        list_of_attributes_Y.add(pregnancyHistory_Y);
        list_of_attributes_Y.add(plasma_Y);
        list_of_attributes_Y.add(bloodPressure_Y);
        list_of_attributes_Y.add(skinThickness_Y);
        list_of_attributes_Y.add(insulin_Y);
        list_of_attributes_Y.add(bodyMass_Y);
        list_of_attributes_Y.add(pedigree_Y);
        list_of_attributes_Y.add(age_Y);

        list_of_attributes_N.add(pregnancyHistory_N);
        list_of_attributes_N.add(plasma_N);
        list_of_attributes_N.add(bloodPressure_N);
        list_of_attributes_N.add(skinThickness_N);
        list_of_attributes_N.add(insulin_N);
        list_of_attributes_N.add(bodyMass_N);
        list_of_attributes_N.add(pedigree_N);
        list_of_attributes_N.add(age_N);

        // Mean
        ArrayList<Double> mu_of_class_Y_local = new ArrayList<>();
        mu_of_class_Y_local.add(count_Y);
        mu_of_class_Y_local.add(utilsCommon.getMean(pregnancyHistory_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(plasma_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(bloodPressure_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(skinThickness_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(insulin_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(bodyMass_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(pedigree_Y));
        mu_of_class_Y_local.add(utilsCommon.getMean(age_Y));

        ArrayList<Double> mu_of_class_N_local = new ArrayList<>();
        mu_of_class_N_local.add(count_N);
        mu_of_class_N_local.add(utilsCommon.getMean(pregnancyHistory_N));
        mu_of_class_N_local.add(utilsCommon.getMean(plasma_N));
        mu_of_class_N_local.add(utilsCommon.getMean(bloodPressure_N));
        mu_of_class_N_local.add(utilsCommon.getMean(skinThickness_N));
        mu_of_class_N_local.add(utilsCommon.getMean(insulin_N));
        mu_of_class_N_local.add(utilsCommon.getMean(bodyMass_N));
        mu_of_class_N_local.add(utilsCommon.getMean(pedigree_N));
        mu_of_class_N_local.add(utilsCommon.getMean(age_N));


        result.put("mu_of_class_Y_local", mu_of_class_Y_local);
        result.put("mu_of_class_N_local", mu_of_class_N_local);
        br.close();
        return result;

    }

    public  HashMap<String,ArrayList<ArrayList<BigDecimal>>> getCovarianceMatrix(String classType, ArrayList<BigInteger> meanOfAttribs) {
        ArrayList<ArrayList<Double>> attributes = new ArrayList<>();
        HashMap<String,ArrayList<ArrayList<BigDecimal>>> covariance=new HashMap<>();
        ArrayList<ArrayList<BigDecimal>> covarianceInt = new ArrayList<>();
        ArrayList<ArrayList<BigDecimal>> covarianceDec = new ArrayList<>();

        if (classType.equalsIgnoreCase("yes")) {
            attributes = list_of_attributes_Y;

        } else if (classType.equalsIgnoreCase("no")) {
            attributes = list_of_attributes_N;
        }

        for (int i = 0; i < attributes.size(); i++) {
            ArrayList<BigDecimal> covijInt=new ArrayList<>();
            ArrayList<BigDecimal> covijDec=new ArrayList<>();
            for (int j = 0; j < attributes.size(); j++) {
            BigDecimal[] covij = utilsCommon.getCovariance(
                        attributes.get(i), attributes.get(j), meanOfAttribs.get(i+1).doubleValue(), meanOfAttribs.get(j+1).doubleValue());
                covijInt.add(covij[0]);
                covijDec.add(covij[1]);
            }
            covarianceInt.add(covijInt);
            covarianceDec.add(covijDec);
        }
        if (classType.equalsIgnoreCase("yes")) {
            covariance.put("yInt",covarianceInt);
            covariance.put("yDeci",covarianceDec);
        } else if (classType.equalsIgnoreCase("no")) {
            covariance.put("nInt",covarianceInt);
            covariance.put("nDeci",covarianceDec);
        }
        return covariance;
    }



    public HashMap<String, HashMap> getParameters() throws Exception {
        double mu_atrib1_Y = mu_of_class_Y_local[0];
        double mu_atrib2_Y = mu_of_class_Y_local[1];
        double mu_atrib3_Y = mu_of_class_Y_local[2];
        double mu_atrib4_Y = mu_of_class_Y_local[3];
        double mu_atrib5_Y = mu_of_class_Y_local[4];
        double mu_atrib6_Y = mu_of_class_Y_local[5];
        double mu_atrib7_Y = mu_of_class_Y_local[6];
        double mu_atrib8_Y = mu_of_class_Y_local[7];

        double mu_atrib1_N = mu_of_class_N_local[0];
        double mu_atrib2_N = mu_of_class_N_local[1];
        double mu_atrib3_N = mu_of_class_N_local[2];
        double mu_atrib4_N = mu_of_class_N_local[3];
        double mu_atrib5_N = mu_of_class_N_local[4];
        double mu_atrib6_N = mu_of_class_N_local[5];
        double mu_atrib7_N = mu_of_class_N_local[6];
        double mu_atrib8_N = mu_of_class_N_local[7];

        HashMap<Integer, Double> betas = new HashMap<>();
        ArrayList<Double> diff_attribs = new ArrayList();
        diff_attribs.add(Math.abs(mu_atrib1_Y - mu_atrib1_N));
        diff_attribs.add(Math.abs(mu_atrib2_Y - mu_atrib2_N));
        diff_attribs.add(Math.abs(mu_atrib3_Y - mu_atrib3_N));
        diff_attribs.add(Math.abs(mu_atrib4_Y - mu_atrib4_N));
        diff_attribs.add(Math.abs(mu_atrib5_Y - mu_atrib5_N));
        diff_attribs.add(Math.abs(mu_atrib6_Y - mu_atrib6_N));
        diff_attribs.add(Math.abs(mu_atrib7_Y - mu_atrib7_N));
        diff_attribs.add(Math.abs(mu_atrib8_Y - mu_atrib8_N));
        for (int i = 0; i < mu_of_class_Y_local.length; i++) {
            double beta = 0;
            for (int j = 0; j < mu_of_class_Y_local.length; j++) {

            }
            betas.put((i + 1), beta);

        }

        HashMap<Integer, Double> attribsMu = new HashMap<>();
        attribsMu.put(1, mu_atrib1_Y);
        attribsMu.put(2, mu_atrib2_Y);
        attribsMu.put(3, mu_atrib3_Y);
        attribsMu.put(4, mu_atrib4_Y);
        attribsMu.put(5, mu_atrib5_Y);
        attribsMu.put(6, mu_atrib6_Y);
        attribsMu.put(7, mu_atrib7_Y);
        attribsMu.put(8, mu_atrib8_Y);

        attribsMu.put(9, mu_atrib1_N);
        attribsMu.put(10, mu_atrib2_N);
        attribsMu.put(11, mu_atrib3_N);
        attribsMu.put(12, mu_atrib4_N);
        attribsMu.put(13, mu_atrib5_N);
        attribsMu.put(14, mu_atrib6_N);
        attribsMu.put(15, mu_atrib7_N);
        attribsMu.put(16, mu_atrib8_N);


        HashMap<String, HashMap> params = new HashMap<>();
        params.put("betaList", betas);
        params.put("attribsMu", attribsMu);
        return params;
    }
}





























