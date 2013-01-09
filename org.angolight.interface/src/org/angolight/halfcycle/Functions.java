package org.angolight.halfcycle;

public class Functions {
	// Fonction d'appartenance logique floue
	public static double FuzMemShip(double x, double alpha, double gamma) {
		if (alpha == gamma) {
			if (x <= alpha)
				return 0;
			else
				return 1;
		} else if (alpha < gamma) {
			if (x < alpha)
				return 0;
			else if (x < ((alpha + gamma) / 2)) {
				return 2 * Math.pow((x - alpha) / (gamma - alpha), 2);
			} else if (x < gamma) {
				return 1 - 2 * Math.pow((x - gamma) / (gamma - alpha), 2);
			} else
				return 1;
		} else {
			if (x < gamma)
				return 0;
			else if (x < ((alpha + gamma) / 2)) {
				return 2 * Math.pow((x - gamma) / (alpha - gamma), 2);
			} else if (x < alpha) {
				return 1 - 2 * Math.pow((x - alpha) / (alpha - gamma), 2);
			} else
				return 1;
		}
	}
}
