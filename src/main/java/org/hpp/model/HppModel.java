/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.river.RiverModel;
import org.hpp.terrain.town.TownModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
@XmlRootElement
public class HppModel {
    private static Logger log = LoggerFactory.getLogger(HppModel.class);
    
    public static final String DEF_FILE_NAME = "hpp-model.xml";
    
    private TerrainModel terrain = null;
    private RiverModel river = null;
    private TownModel town = null;

    private double Pmin = 0; // Watt
    private double Pmax = 0; // Watt
    
    private double Cap_user = 0; // МВт 
    
    private double Dmax = 0; // distance in km
    
    private double Cost = 0; // mln rub, Cost >= 25*Pmin
    
    private double rate = 0; // m^3/s
    
    private double Rate_min = 0.5; // m^3/s
    private double Rate_max = 20; // m^3/s
    
    private double Vstok = 0; // V_stok (=0,1 default), %
    
    private int maxCountRD = 10;
    private int maxCountRF = 10;
    private int maxCountRR = 10;
    
    private int rangeDER = 100; // m
    
    private int maxLenTub = 5; // km
    
    private double efficHP = 0.5;
    
    private double wD = 1;
    private double wL = 1;
    private double wS = 1;
    private double wk = 1;
    private double wc = 1;
    private double wTc = 1;
    
    public static final String RATE_MIDDLE_DAY_FILE = "Rate_middleday.props";
    public static final String DAY_PREFIX = "day";
    private double Rate_midleday[] = null; // 365  values
    
    private double Rate_dbmiddle = 8.5291972;
    
    private double[] cp1 = new double[]{14382, -0.2368, -0.0598};
    private double[] cp2 = new double[]{4906, -0.3722, 0.3866};
    private double[] cp3 = new double[]{62246, -0.2354, -0.0587};
    private double[] cp4 = new double[]{28164, -0.376, -0.624};
    private double[] cp5 = new double[]{70000, -0.2354, -0.0587};
    
    private double[] co1 = new double[]{39485, -0.1902, -0.2167};
    private double[] co2 = new double[]{48568, -0.1867, -0.2090};
    private double[] co3 = new double[]{31712, -0.1900, -0.2122};
    private double[] co4 = new double[]{14062, -0.1817, -0.2082};

    public static final double G = 9.8; // g - gravitation const
    
    public static final Map<String, String> UNITS;
    
    static {
        Map<String, String> map = new HashMap<>();

        map.put("Dmax", "km");
        map.put("Pmin", "watt");
        map.put("Pmax", "watt");
        map.put("Cost", "mln. rub");
        map.put("Rate_min", "m^3/s");
        map.put("Rate_max", "m^3/s");
        map.put("Rate", "m^3/s");
        map.put("Vstok", "%");
        map.put("RangeDER", "m");
        map.put("MaxLenTub", "km");
        map.put("Cap_user", "MWatt");
        
        UNITS = Collections.unmodifiableMap(map);
    }
    
    public HppModel() {
        super();
    }

    @XmlTransient
    public TerrainModel getTerrainModel() {
        return terrain;
    }

    public void setTerrainModel(TerrainModel heightMap) {
        this.terrain = heightMap;
    }

    @XmlTransient
    public RiverModel getRiverModel() {
        return river;
    }

    public void setRiverModel(RiverModel riverMap) {
        this.river = riverMap;
    }

    public TownModel getTownModel() {
        return town;
    }

    public void setTownModel(TownModel town) {
        this.town = town;
    }

    public double getPmin() {
        return Pmin;
    }

    public void setPmin(double Pmin) {
        this.Pmin = Pmin;
    }

    public double getPmax() {
        return Pmax;
    }

    public void setPmax(double Pmax) {
        this.Pmax = Pmax;
    }

    public double getDmax() {
        return Dmax;
    }

    public void setDmax(double Dmax) {
        this.Dmax = Dmax;
    }

    public double getCost() {
        return Cost;
    }

    public void setCost(double Cost) {
        this.Cost = Cost;
    }

    public double getRate_min() {
        return Rate_min;
    }

    public void setRate_min(double Rate_min) {
        this.Rate_min = Rate_min;
    }

    public double getRate_max() {
        return Rate_max;
    }

    public void setRate_max(double Rate_max) {
        this.Rate_max = Rate_max;
    }

    public double getVstok() {
        return Vstok;
    }

    public void setVstok(double Vstok) {
        this.Vstok = Vstok;
    }

    @XmlTransient
    public double[] getRate_midleday() {
        return Rate_midleday;
    }

    public void setRate_midleday(double[] Rate_midleday) {
        this.Rate_midleday = Rate_midleday;
    }

    public double getRate_dbmiddle() {
        return Rate_dbmiddle;
    }

    public void setRate_dbmiddle(double Rate_dbmiddle) {
        this.Rate_dbmiddle = Rate_dbmiddle;
    }

    public int getMaxCountRD() {
        return maxCountRD;
    }

    public void setMaxCountRD(int maxCountRD) {
        this.maxCountRD = maxCountRD;
    }

    public int getMaxCountRF() {
        return maxCountRF;
    }

    public void setMaxCountRF(int maxCountRF) {
        this.maxCountRF = maxCountRF;
    }

    public int getMaxCountRR() {
        return maxCountRR;
    }

    public void setMaxCountRR(int maxCountRR) {
        this.maxCountRR = maxCountRR;
    }

    public int getRangeDER() {
        return rangeDER;
    }

    public void setRangeDER(int rangeDER) {
        this.rangeDER = rangeDER;
    }

    public int getMaxLenTub() {
        return maxLenTub;
    }

    public void setMaxLenTub(int maxLenTub) {
        this.maxLenTub = maxLenTub;
    }

    public double getEfficHP() {
        return efficHP;
    }

    public void setEfficHP(double efficHP) {
        this.efficHP = efficHP;
    }

    public double getwD() {
        return wD;
    }

    public void setwD(double wD) {
        this.wD = wD;
    }

    public double getwL() {
        return wL;
    }

    public void setwL(double wL) {
        this.wL = wL;
    }

    public double getwS() {
        return wS;
    }

    public void setwS(double wS) {
        this.wS = wS;
    }

    public double getWk() {
        return wk;
    }

    public void setWk(double wk) {
        this.wk = wk;
    }

    public double getWc() {
        return wc;
    }

    public void setWc(double wc) {
        this.wc = wc;
    }

    public double getwTc() {
        return wTc;
    }

    public void setwTc(double wTc) {
        this.wTc = wTc;
    }

    public double getCap_user() {
        return Cap_user;
    }

    public void setCap_user(double Cap_user) {
        this.Cap_user = Cap_user;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double[] getCp1() {
        return cp1;
    }

    public void setCp1(double[] cp1) {
        this.cp1 = cp1;
    }

    public double[] getCp2() {
        return cp2;
    }

    public void setCp2(double[] cp2) {
        this.cp2 = cp2;
    }

    public double[] getCp3() {
        return cp3;
    }

    public void setCp3(double[] cp3) {
        this.cp3 = cp3;
    }

    public double[] getCp4() {
        return cp4;
    }

    public void setCp4(double[] cp4) {
        this.cp4 = cp4;
    }

    public double[] getCp5() {
        return cp5;
    }

    public void setCp5(double[] cp5) {
        this.cp5 = cp5;
    }

    public double[] getCo1() {
        return co1;
    }

    public void setCo1(double[] co1) {
        this.co1 = co1;
    }

    public double[] getCo2() {
        return co2;
    }

    public void setCo2(double[] co2) {
        this.co2 = co2;
    }

    public double[] getCo3() {
        return co3;
    }

    public void setCo3(double[] co3) {
        this.co3 = co3;
    }

    public double[] getCo4() {
        return co4;
    }

    public void setCo4(double[] co4) {
        this.co4 = co4;
    }
    
    /**
     * Convert distance in pixel according current scale.
     * 
     * @param value distance in pixel
     * @return
     */
    public double toKm(double value) {
        if ( terrain == null ) {
            throw new NullPointerException("No terrain model.");
        }
        
        return value * terrain.getPixelScale() / 1000f;
    }

    /**
     * Convert to distance in pixel.
     * 
     * @param value distance in meters
     * @return distance in pixel
     */
    public double toPx(double value) {
        if ( terrain == null ) {
            throw new NullPointerException("No terrain model.");
        }
        
        return value / terrain.getPixelScale();
    }
    
    public double kmToPx(double value) {
        return this.toPx(1000 * value);
    }
    
    public void saveToDefaults(boolean bIsFull) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(HppModel.class);
        
        Marshaller marshaller = ctx.createMarshaller();
        
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        marshaller.marshal(this, new File(DEF_FILE_NAME));
        
        if ( bIsFull ) {
            if ( river != null ) {
                river.saveToFile(null);
            }

            if ( terrain != null ) {
                terrain.saveToFile(null);
            }
        }
    }
    
    /**
     *  Save models.
     * models[0] - hpp model path
     * models[1] - terrain model path
     * models[2] - river model path
     * 
     * @param models
     * @param bIsFull
     * @throws Exception
     */
    public void saveToFile(String models[], boolean bIsFull) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(HppModel.class);
        
        Marshaller marshaller = ctx.createMarshaller();
        
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        marshaller.marshal(this, new File(models[0] == null ? DEF_FILE_NAME : models[0]));
        
        if ( bIsFull ) {
            if ( terrain != null ) {
                terrain.saveToFile(models[1]);
            }
            
            if ( river != null ) {
                river.saveToFile(models[2]);
            }
        }
    }
    
    public static HppModel loadFromDefaults(boolean bIsFull) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(HppModel.class);
        
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        
        HppModel model = (HppModel)unmarshaller.unmarshal(new File(DEF_FILE_NAME));
        
        double days[] = loadRateMiddleDayFromFile(RATE_MIDDLE_DAY_FILE);
        
        model.setRate_midleday(days);
        
        if ( bIsFull ) {
            model.setRiverModel(RiverModel.loadFromFile(null));

            model.setTerrainModel( TerrainModel.loadFromFile(null));
        }
        
        return model;
    }
    
    /**
     * Load models from files. 
     * models[0] - hpp model
     * models[1] - terrain model
     * models[2] - river model
     * models[3] - rate model (properties)
     * @param models
     * @param bIsFull
     * @return
     * @throws Exception
     */
    public static HppModel loadFromFile(String[] models, boolean bIsFull) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(HppModel.class);
        
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        
        HppModel model = (HppModel)unmarshaller.unmarshal(new File(models[0] == null ? DEF_FILE_NAME : models[0]));
        
        double days[] = loadRateMiddleDayFromFile(models[3]);
        
        model.setRate_midleday(days);
        
        if ( bIsFull ) {
            model.setTerrainModel( TerrainModel.loadFromFile(models[1]));
            
            model.setRiverModel(RiverModel.loadFromFile(models[2]));
        }
        
        return model;
    }
    
    public static double[] loadRateMiddleDayFromFile(String fileName) throws Exception {
        File file = new File(fileName == null ? RATE_MIDDLE_DAY_FILE : fileName);
        
        if ( !file.exists() ) {
            log.debug("No rate middle day file : " + fileName);
            return null;
        }
        
        Properties props = new Properties();
        
        try (FileReader reader = new FileReader( file )) {
            props.load( reader );
        }
        
        double days[] = new double[365];
        
        int count = 0;
        for (String name : props.stringPropertyNames()) {
            if ( !name.startsWith(DAY_PREFIX) ) {
                continue;
            }
            
            int dayNum = Integer.parseInt( name.substring( DAY_PREFIX.length() ) );
            DecimalFormat format = new DecimalFormat(".00###");
            DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            format.setDecimalFormatSymbols(symbols);
            days[dayNum-1] = format.parse( props.getProperty(name) ).doubleValue();
            
            count ++;
        }
        log.debug("Loaded from (" + fileName + ") " + count + " values.");
        
        return days;
    }
}
