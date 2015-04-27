package edu.harvard.wcfia.yoshikoder.reporting;

import java.text.NumberFormat;

import javax.swing.text.NumberFormatter;

/**
 * Computes and records various statistics relating to the risk ratio.
 * Quantities are stored as logs and exponentiated on demand.
 * 
 * @author will
 *
 */
public class RiskRatioStatistics{
    protected double p1, p2, N1, N2, logRR, seLogRR;
    
    protected boolean allBetsAreOff; // we have a zero in the works...
    
    public RiskRatioStatistics(int map1count, int map1Total, int map2count, int map2Total){
        
        if ((map1count == 0.0) || (map1Total == 0.0) || (map2count == 0.0) || (map2Total == 0.0))
            allBetsAreOff = true;
        else {
            N1 = map1Total;
            N2 = map2Total;
            p1 = (double)map1count / N1;
            p2 = (double)map2count / N2;
            seLogRR = Math.sqrt( (1-p1) / (N1*p1) + (1-p2) / (N2*p2) );
            logRR = Math.log(p1) - Math.log(p2);
            
            System.err.println(logRR);
        }
    }
    
    public double getLogRiskRatio() throws UncomputableRiskRatioException {
        if (allBetsAreOff) throw new UncomputableRiskRatioException("Cannot compute risk ratio");
        return logRR;
    }
    
    public double[] getLogRiskRatioInterval(double z) throws UncomputableRiskRatioException {
        if (allBetsAreOff) throw new UncomputableRiskRatioException("Cannot compute risk ratio");
        return new double[]{ logRR - z*seLogRR, logRR + z*seLogRR };
    }
    
    public double[] getLogRiskRatio95Interval() throws UncomputableRiskRatioException {
        if (allBetsAreOff) throw new UncomputableRiskRatioException("Cannot compute risk ratio");
        return getLogRiskRatioInterval(1.96);
    }
    
    public double getRiskRatio() throws UncomputableRiskRatioException {
        if (allBetsAreOff) throw new UncomputableRiskRatioException("Cannot compute risk ratio");
        return Math.exp(logRR);
    }
    
    /**
     * Gets upper and lower ends of a 95% interval for the risk ratio.  Computed
     * using 1.96 as the z score.
     * @return 95% confidence interval
     */
    public double[] getRiskRatio95Interval() throws UncomputableRiskRatioException{
        if (allBetsAreOff) throw new UncomputableRiskRatioException("Cannot compute risk ratio");
        double[] lrrInterval = getLogRiskRatio95Interval();
        return new double[]{Math.exp(lrrInterval[0]), Math.exp(lrrInterval[1])};
    }
    
    /**
     * States whether an interval excludes the specified value.  For
     * example, whether a risk raio interval excludes 1 (no effect).
     * @param value
     * @param interval
     * @return whether the interval excludes the value
     */
    public boolean intervalExcludes(double value, double[] interval){
        return ((interval[0] > value && interval[1] > value) ||
                (interval[0] < value && interval[1] < value));
    }

    public String toString(){
        if (allBetsAreOff) 
            return "NA";
        else {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(4);
            try {
                StringBuffer sb = new StringBuffer();        
                sb.append(nf.format(getRiskRatio()));
                sb.append(" [");
                double[] interval = getRiskRatio95Interval();
                sb.append( nf.format(interval[0]) );
                sb.append(", ");
                sb.append( nf.format(interval[1]) );
                sb.append("]");
                if (intervalExcludes(1, interval))
                    sb.append(" *");
                return sb.toString();
            } catch (UncomputableRiskRatioException urre){
                return "NA";
            }
        }
    }
    
    public static void main(String[] args) {
        int c1 = 11;
        int N1 = 25;
        int c2 = 3;
        int N2 = 34;
        RiskRatioStatistics rrs = new RiskRatioStatistics(c1, N1, c2, N2);
        System.out.println(rrs);
        
    }
}