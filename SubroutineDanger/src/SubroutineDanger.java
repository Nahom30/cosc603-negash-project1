import java.math.*;
import java.io.*;
import java.util.*;

/**
 * The Class SubroutineDanger.
 */
public class SubroutineDanger {
	
	/**
	 * The main method.
	 *
	 * @param args            the command line arguments
	 */
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
	
		double dry = 0;
		double wet = 0;
		double iSnow;
		double precip;
		double isWindy;
		double buildIndex;
		double iHerb;
		double grass = 0;
		double timber = 0;
		double dif;
		double df = 0;
		double ffm = 99;
		double adfm = 99;
		double fload = 0;
		
		
		
		double[] a = { -0.185900, -0.85900, -0.059960, -0.077373 };
		double[] b = { 30.0, 19.2, 13.8, 22.4 };
		double[] c = { 4.5, 12.5, 27.5 };
		double[] d = { 16.0, 10.0, 7.0, 5.0, 4.0, 3.0 };
		String isSnowing;
		String isRaining;
		
		System.out.println("Is it snowing? yes/no or y/n");
		isSnowing = reader.next();
		System.out.println("What is the dry bulb temprature?");
		dry = reader.nextDouble();
		System.out.println("What is the wet bulb tempture? ");
		wet = reader.nextDouble();
		System.out.println("What is the current wind speed?");
		isWindy = reader.nextDouble();
		System.out.println("What is the last value of the build up index");
		buildIndex = reader.nextDouble();
		System.out
				.println("What is the current herb state of the district: 1=cured, 2= Transition 3= Green");
		iHerb = reader.nextDouble();
		if (isSnowing.equals("y") || isSnowing.equals("yes")) {
			System.out.println("How many inches of snow on the ground? ");
			iSnow = reader.nextDouble();
			if (iSnow <= 0) {
				dif = dry - wet;
		
				for (int i = 1; i <= 3; i++) {
					if (dif <= c[i]) {
						ffm = b[i] * Math.exp(a[i] * dif);
						
						for (int j = 1; j <= 6; j++) {
							if (ffm <= d[j]) {
								df = 7;
								determineFineFuelMoisture(ffm,iHerb, isWindy); 
								
							} else {
								df = d[j - 1];
							}
						}
					} else {
						c[i] = 4;
					}
				}

			} else {
				timber = 0;
				grass = 0;
				fload = 0;
				System.out.println("Is it raining? yes/no or y/n");
				isRaining = reader.next();
				/**
				 *  checking if it's raining
				 */
				if (isRaining.equals("y") || isRaining.equals("yes")) {
					System.out
							.println("How many inches of rain on the ground?");
					precip = reader.nextDouble();
					buildIndex = calculateBuildUpIndex(precip, buildIndex);
				}
			}
		}

		System.out.println("Drying Factor:         "+ df );
		System.out.println("Fine Fuel Mooisture:   "+ffm );
		System.out.println("Adjusted Fine Fuel:    "+ adfm );
		System.out.println("Grass Spread Index:    "+ grass);
		System.out.println("Timber Spread Index:   "+timber);
		System.out.println("Fire Load Rating:      "+fload );
		System.out.println("Build Up Index:        "+ buildIndex );
	}
	public static double determineFineFuelMoisture(double ffm, double iHerb, double isWindy){
		Scanner reader = new Scanner(System.in);
		String isRaining; 
		double grass= 0; 
		double fload =0; 
		double buildIndex =0;  
		double timber =0;
		double df =0; 
		double percip; 
		if (ffm < 1) {
			ffm = 1;
		} else {
			ffm = ffm + ((iHerb - 1) * 5);
			System.out.println("Is it raining?yes/no or y/n");
			isRaining = reader.next();
			if (isRaining.equals("y")|| isRaining.equals("yes")) {
				System.out.println("How many inches of rain on the ground?");
				percip = reader.nextDouble();
				buildIndex = adjustBuildIndex(percip,
						isWindy, buildIndex, grass,
						timber, df, ffm, fload);
			}
		}
		return ffm; 
	}

	public static double adjustBuildIndex(double precip, double isWindy,
			double buildIndex, double grass, double timber, double df,
			double ffm, double fload) {
		if (precip <= .1) {
			CurrentBuildUp(buildIndex, df, ffm,
					isWindy, timber, grass, fload);
		} else {
			calculateBuildUpIndex(precip, buildIndex);

			if (buildIndex < 0) {
				buildIndex = 0.0;
			} else {
				CurrentBuildUp(buildIndex, df,
						ffm, isWindy, timber,
						grass, fload);
			}
		}
		return buildIndex;
	}

	/**
	 * Fload.
	 *
	 * @param timber the timber
	 * @param buildIndex the buo
	 * @param fload the fload
	 * @return the double
	 */
	public static double calculateFireLoads(double timber, double buildIndex, double fload) {

		if (timber > 0) {
			if (buildIndex > 0) {
				fload = 1.75 * Math.log10(timber) + 32
						* (Math.log10(buildIndex) - 1.640);
				if (fload <= 0) {
					fload = 0;
				}

			} else {
				fload = Math.pow(10, fload);
			}
		} else {
			timber = 0;
			buildIndex = 0;
		}
		return fload;
	}

	/**
	 * Calculate buo.
	 *
	 * @param perciptation the perciptation
	 * @param buildIndex double
	 * @return the double
	 */
	public static double calculateBuildUpIndex(double perciptation, double buildIndex){
		if (perciptation > .1) { // #2
			buildIndex = 50 * Math.log((1 - Math.exp(-buildIndex / 50))
					* Math.exp(-1.175 * (perciptation - .1)));
			if (buildIndex < 0) {
				buildIndex = 0;
			}
		}
		return buildIndex;
	}
	
	/**
	 * Calculating current build up.
	 *
	 * @param buildIndex the buo
	 * @param df the df
	 * @param adfm the adfm
	 * @param ffm the ffm
	 * @param wind the wind
	 * @param timber the timber
	 * @param grass the grass
	 * @param fload the fload
	 * @return the double
	 */
	public static double CurrentBuildUp(double buildIndex, double df,
			double ffm, double wind, double timber, double grass, double fload) {
		double adfm; 
		buildIndex= buildIndex + df;
		
		adfm = .9 * ffm + 9.5 * Math.exp(-buildIndex / 50);
		
		if (adfm < 30) {
			calculateTimber(wind, ffm, adfm); 
		} else {
			if (ffm > 30) {
				determineWind(buildIndex, ffm, wind, timber, fload);
			} else {
				grass = 1;
				timber = 1;
			}
		}
		return adfm;
	}

	public static void determineWind(double buildIndex, double ffm,
			double wind, double timber, double fload) {
		double grass;
		if (wind < 14) {
			double n =6; 
			grass = calculateGrass(ffm, wind, n);
			if (timber <= 1) {
				timber = 1;
				fireLoad(buildIndex, timber, grass, fload);
			} else {
				calculateFireLoads(timber, buildIndex, fload);
			}

		} else {
			double n= 14; 
			grass = calculateGrass(ffm, wind, n);
			if (grass <= 99) {
				calculateFireLoads(timber, buildIndex, fload);
			} else {
				grass = 99;
			}
		}
	}

	public static double calculateGrass(double ffm, double wind, double n) {
		double grass;
		grass = (.01312 * (wind + n))
				* (Math.pow((33 - ffm), 1.65) - 3);
		return grass;
	}

	public static void fireLoad(double buildIndex, double timber, double grass,
			double fload) {
		if (grass < 1){ 
			grass=1; 
		}else {
			calculateFireLoads(timber, buildIndex, fload);	
		}
	}
	public static double calculateTimber(double wind, double ffm, double adfm ){
		double timber = 0; 
		if (wind < 14) {
			timber = calculateGrass(ffm, wind, timber);

		} else {
			timber = (.000918 * (wind + 14))
					* (Math.pow((33 - adfm), 1.65) - 3);
		}
		return timber; 
	}
}
