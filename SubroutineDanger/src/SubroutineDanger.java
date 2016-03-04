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
		/**
		 *  initializing variables
		 */
		
		double dry = 0;
		double wet = 0;
		double iSnow;
		double precip;
		double wind;
		double buo;
		double iHerb;
		double grass = 0;
		double timber = 0;
		double dif;
		double df = 0;
		double ffm = 99;
		double adfm = 99;
		double fload = 0;
		
		/**
		 *  table values used in computing danger ratings
		 */
		double[] a = { -0.185900, -0.85900, -0.059960, -0.077373 };
		double[] b = { 30.0, 19.2, 13.8, 22.4 };
		double[] c = { 4.5, 12.5, 27.5 };
		double[] d = { 16.0, 10.0, 7.0, 5.0, 4.0, 3.0 };
		String isSnowing;
		String pOutput;
		String pOutput2;
		
		/**
		 *  test to see if any snow on the ground
		 */
		System.out.println("Is it snowing? yes/no or y/n");
		isSnowing = reader.next();
		System.out.println("What is the dry bulb temprature?");
		dry = reader.nextDouble();
		System.out.println("What is the wet bulb tempture? ");
		wet = reader.nextDouble();
		System.out.println("What is the current wind speed?");
		wind = reader.nextDouble();
		System.out.println("What is the last value of the build up index");
		buo = reader.nextDouble();
		System.out
				.println("What is the current herb state of the district: 1=cured, 2= Transition 3= Green");
		iHerb = reader.nextDouble();
		if (isSnowing.equals("y") || isSnowing.equals("yes")) {
			System.out.println("How many inches of snow on the ground? ");
			iSnow = reader.nextDouble();
			/**
			 *  testing to see if there is snow on the ground
			 */
			if (iSnow <= 0) {
				dif = dry - wet;
				/**
				 *  compute spread indexes and fire loads
				 */
				for (int i = 1; i <= 3; i++) {
					if (dif <= c[i]) {
						ffm = b[i] * Math.exp(a[i] * dif);
						/**
						 *  Drying Factor for the day
						 */
						for (int j = 1; j <= 6; j++) {
							if (ffm <= d[j]) {
								df = 7;
								if (ffm < 1) {
									ffm = 1;
								} else {
									ffm = ffm + ((iHerb - 1) * 5);
									System.out
											.println("Is it raining?yes/no or y/n");
									pOutput2 = reader.next();
									if (pOutput2.equals("y")
											|| pOutput2.equals("yes")) {
										System.out
												.println("How many inches of rain on the ground?");
										precip = reader.nextDouble();
										/**
										 *  adjust BUI for precipitation before
										 *  adding drying factor.
										 */
										if (precip <= .1) {
											CurrentBuildUp(buo, df, adfm, ffm,
													wind, timber, grass, fload);
										} else {
											buo = 50 * Math.log((1 - Math
													.exp(-buo / 50))
													* Math.exp(-1.175
															* (precip - .1)));
											if (buo < 0) {
												buo = 0.0;
											} else {
												CurrentBuildUp(buo, df, adfm,
														ffm, wind, timber,
														grass, fload);
											}
										}
									}
								}
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
				pOutput = reader.next();
				/**
				 *  checking if it's raining
				 */
				if (pOutput.equals("y") || pOutput.equals("yes")) {
					System.out
							.println("How many inches of rain on the ground?");
					precip = reader.nextDouble();
					buo = calculateBuildUpIndex(precip, buo);
				}
			}
		}

		System.out.println("Drying Factor:         "+ df );
		System.out.println("Fine Fuel Mooisture:   "+ffm );
		System.out.println("Adjusted Fine Fuel:    "+ adfm );
		System.out.println("Grass Spread Index:    "+ grass);
		System.out.println("Timber Spread Index:   "+timber);
		System.out.println("Fire Load Rating:      "+fload );
		System.out.println("Build Up Index:        "+ buo );
	}

	/**
	 * Fload.
	 *
	 * @param timber the timber
	 * @param buo the buo
	 * @param fload the fload
	 * @return the double
	 */
	public static double Fload(double timber, double buo, double fload) {

		if (timber > 0) {
			if (buo > 0) {
				fload = 1.75 * Math.log10(timber) + 32
						* (Math.log10(buo) - 1.640);
				if (fload <= 0) {
					fload = 0;
				}

			} else {
				fload = Math.pow(10, fload);
			}
		} else {
			timber = 0;
			buo = 0;
		}
		return fload;
	}

	/**
	 * Calculate buo.
	 *
	 * @param perciptation the perciptation
	 * @param buo double
	 * @return the double
	 */
	public static double calculateBuildUpIndex(double perciptation, double buo){
		if (perciptation > .1) { // #2
			buo = 50 * Math.log((1 - Math.exp(-buo / 50))
					* Math.exp(-1.175 * (perciptation - .1)));
			if (buo < 0) {
				buo = 0;
			}
		}
		return buo;
	}
	
	/**
	 * Calculating current build up.
	 *
	 * @param buo the buo
	 * @param df the df
	 * @param adfm the adfm
	 * @param ffm the ffm
	 * @param wind the wind
	 * @param timber the timber
	 * @param grass the grass
	 * @param fload the fload
	 * @return the double
	 */
	public static double CurrentBuildUp(double buo, double df, double adfm,
			double ffm, double wind, double timber, double grass, double fload) {
		
		buo= buo + df;
		/**
		 * adjust the grass spread index for heavy fuel lags
		 */
		adfm = .9 * ffm + 9.5 * Math.exp(-buo / 50);
		/**
		 *  test to see if the fuel mixture are greater than 30 percent
		 */
		if (adfm < 30) {
			if (wind < 14) {
				timber = (.01312 * (wind + 6))
						* (Math.pow((33 - ffm), 1.65) - 3);

			} else {
				timber = (.000918 * (wind + 14))
						* (Math.pow((33 - adfm), 1.65) - 3);
			}
		} else {
			if (ffm > 30) {
				timber = 1;
				/**
				 *  test to see if the wind speed is greater than 14
				 */
				if (wind < 14) {
					grass = (.01312 * (wind + 6))
							* (Math.pow((33 - ffm), 1.65) - 3);
					if (timber <= 1) {
						timber = 1;
						if (grass < 1) {
							grass = 1;
							/**
							 *  computing fire loads.
							 */
							Fload(timber, buo, fload);
						} else {
							/**
							 *  compute the fire load 
							 */
							Fload(timber, buo, fload);
						}
					} else {
						/**
						 *  compute the fire load
						 */
						Fload(timber, buo, fload);
					}

				} else {
					grass = (.00918 * (wind + 14))
							* Math.pow((33 - ffm), (1.65 - 3));
					if (grass <= 99) {
						/**
						 *  compute the fire load
						 */
						Fload(timber, buo, fload);
					} else {
						grass = 99;
					}
				}

			} else {
				grass = 1;
				timber = 1;
			}
		}
		return buo;
	}
}
