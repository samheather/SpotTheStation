package main;

import java.lang.Math;

public class Locate {
	
	// Earth
	double radiusEarth = 6378.1*1000; // Radius in meters
	
	// ISS
	// Position
	double ISSLat = 34.4/180*Math.PI; // phi in algebra in degrees
	double ISSLong = 140/180*Math.PI; // theta in algebra in degrees
	double ISSHASL = 421*10^3; // m /260 (Height above sea level)
	
	// User
	// Position
	double userLat = 50.203491/180*Math.PI; // 50.203491
	double userLong = -5.120502/180*Math.PI; // -5.120502
	double userHASL = 0; // Measured in meters (Height above sea level)
	
	// Looking direction
	double userBearing = 0;
	double userTilt = 0; // In one dimention, looking up or down.
	
	// Cartesian Coordinates
	// ISS
	double ISSCartesianXPosition = (radiusEarth+ISSHASL)*Math.cos(ISSLat)*Math.sin(ISSLong);
	double ISSCartesianYPosition = -(radiusEarth+ISSHASL)*Math.cos(ISSLat)*Math.cos(ISSLong);
	double ISSCartesianZPosition = (radiusEarth+ISSHASL)*Math.sin(ISSLat);
	
	// User
	double userCartesianXPosition = (radiusEarth+userHASL)*Math.cos(userLat)*Math.sin(userLong);
	double userCartesianYPosition = -(radiusEarth+userHASL)*Math.cos(userLat)*Math.cos(userLong);
	double userCartesianZPosition = (radiusEarth+userHASL)*Math.sin(userLat);
	
	// Cartesian Positon Vector format
	double[] ISSCartesianPosition = new double[]{ISSCartesianXPosition,ISSCartesianYPosition,ISSCartesianZPosition};
	double[] userCartesianPosition = new double[]{userCartesianXPosition,userCartesianYPosition,userCartesianZPosition};
	double[] northPoleCartesianPosition = new double[]{0,0,radiusEarth};
	
	// Vector FROM the user TO the ISS.
	double[] cartesianVectorFromUserToISS = vecSub(ISSCartesianPosition,userCartesianPosition);
	// Vector FROM the user TO the North Pole
	double[] cartesianVectorFromUserToNorthPole = vecSub(northPoleCartesianPosition,userCartesianPosition);
	
	// Working out the bearing.
	double[] bearingPlaneUnitNormal = vectDivide(userCartesianPosition, vectNorm(userCartesianPosition));
	
	
	
	// Taking two cartesian points and give the direction vector FROM the first point
	// TO the second point ON the bearing plane of the user.
	// Bearing plane of the user is the plane perpendicular to the surface of the
	// earth at the point the user is standing.
	double[] cartesianDirectionVectorOnBearingPlaneFromUserToISS = vecSub(
			cartesianVectorFromUserToISS,
			scaleVec(bearingPlaneUnitNormal,
					(vecDot(
					cartesianVectorFromUserToISS,
					bearingPlaneUnitNormal))
					)
	);
	
	double[] cartesianDirectionVectorOnBearingPlaneFromUserToNorthPole = vecSub(
			cartesianVectorFromUserToNorthPole,
			scaleVec(bearingPlaneUnitNormal,
					(vecDot(
					cartesianVectorFromUserToNorthPole,
					bearingPlaneUnitNormal))
					)
	);

	// Bearing of the ISS FROM the user.
	double bearingOfTheISSInRadians = Math.acos((vecDot(cartesianDirectionVectorOnBearingPlaneFromUserToISS,cartesianDirectionVectorOnBearingPlaneFromUserToNorthPole))/(vectNorm(cartesianDirectionVectorOnBearingPlaneFromUserToISS)*vectNorm(cartesianDirectionVectorOnBearingPlaneFromUserToNorthPole)));
	double bearingOfTheISS = (bearingOfTheISSInRadians*180)/Math.PI;
	
	// Working out the 'Head Tilt'
	double[] normalisedCartesianVectorFromUserToISS = vectDivide(cartesianVectorFromUserToISS,vectNorm(cartesianVectorFromUserToISS));
	double angleBetweenUserISSVecAndBearingPlaneNormalInRadians = Math.asin(vecDot(normalisedCartesianVectorFromUserToISS,bearingPlaneUnitNormal));
	double angleBetweenUserISSVecAndBearingPlaneNormal = (angleBetweenUserISSVecAndBearingPlaneNormalInRadians*180)/Math.PI;
	
	// Since I'm on a train and can't download la4j, I'm forced to write trivial rubbish myself - yay!
	/**
	 * Does vec1-vec2		
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	public double[] vecSub(double[] vec1, double[] vec2) {
		checkVectors(vec1, vec2);
		for (int i = 0; i<vec1.length; i++) {
			vec1[i] = vec1[i] - vec2[i];
		}
		return vec1;
	}
	
	public double[] scaleVec(double[] vec1, double scalar) {
		for (int i = 0; i<vec1.length; i++) {
				vec1[i] = vec1[i]*scalar;
		}
		return vec1;
	}
	
	public double vecDot(double[] vec1, double[] vec2) {
		checkVectors(vec1, vec2);
		double sum = 0;
		for (int i = 0; i<vec1.length; i++) {
			sum = sum + (vec1[i] * vec2[i]);
		}
		return sum;
	}
	
	public void checkVectors(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) {
			try {
				throw new Exception("Vector calculations must have equal sized inputs");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public double vectNorm(double[] vec1) {
		double workingSquareSum = 0;
		for (int i = 0; i<vec1.length; i++) {
			workingSquareSum += Math.pow(vec1[i],2);
		}
		return Math.sqrt(workingSquareSum);
	}
	
	public double[] vectDivide(double[] vec1, double divisor) {
		for (int i = 0; i<vec1.length; i++) {
			vec1[i] = vec1[i]/divisor;
		}
		return vec1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
