package org.angolight.halfcycle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

public class HalfCycle {

	private static final int POSITIVE_H2V = 0;
	private static final int POSITIVE_H1V = 1;
	private static final int NEGATIVE_H1V = 2;
	private static final int NEGATIVE_H2V = 3;
	
	private double _h = 0; 
	private double _h_etoile = 0; 
	private double _h_high = 0; 
	
	private double _vmin = 0;
	private double _vmax = 0;
	private double _dv = 0;
	
	private double _h1 = 0;
	private double _h2 = 0;

	private static double[][] _aHV = null;
	private static int _aHVLength = 0;

	private Logger _log = Logger.getInstance(this.getClass());

	public static void LoadDefaultHVCurves() {

		_aHV = new double[][] {
				{ 1.5059, 1.7237, 1.6709, 1.6769, 1.6457, 1.6140, 1.5790,
						1.5574, 1.5582, 1.5622, 1.5648, 1.5685, 1.5760, 1.5793,
						1.5921, 1.6223, 1.6696, 1.7138, 1.7259, 1.7211, 1.7224,
						1.7293, 1.7309, 1.7284, 1.7318, 1.7334, 1.7322, 1.7221,
						1.7098, 1.6959, 1.6524, 1.5882, 1.5359, 1.5053, 1.4888,
						1.4641, 1.4300, 1.3960, 1.3780, 1.3641, 1.3491, 1.3310,
						1.2997, 1.2599, 1.2228, 1.2014, 1.1918, 1.1974, 1.1943,
						1.1881, 1.1797, 1.1703, 1.1509, 1.1172, 1.0781, 1.0486,
						1.0369, 1.0334, 1.0321, 1.0169, 1.0076, 1.0010, 0.9905,
						0.9806, 0.9575, 0.9324, 0.9181, 0.9000, 0.8835, 0.8552,
						0.8294, 0.8097, 0.7980, 0.7928, 0.7849, 0.7816, 0.7714,
						0.7538, 0.7316, 0.7123, 0.6908, 0.6745, 0.6513, 0.6395,
						0.6271, 0.6141, 0.5972, 0.5787, 0.5639, 0.5447, 0.5363,
						0.5171, 0.4967, 0.4665, 0.4313, 0.3974, 0.3372, 0.2264,
						0.1018, 0.0062, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000,
						0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000 },
				{ 1.3865, 1.3865, 1.3865, 1.3865, 1.3865, 1.3865, 1.3865,
						1.3865, 1.3865, 1.3865, 1.3944, 1.4010, 1.3935, 1.3559,
						1.3006, 1.2529, 1.2380, 1.2500, 1.2500, 1.2522, 1.2672,
						1.2878, 1.2927, 1.2873, 1.2824, 1.2809, 1.2791, 1.2566,
						1.2622, 1.2781, 1.2754, 1.2601, 1.2418, 1.2328, 1.2111,
						1.1805, 1.1587, 1.1391, 1.1185, 1.0969, 1.0563, 1.0103,
						0.9617, 0.9395, 0.9272, 0.9133, 0.8977, 0.8966, 0.9172,
						0.9378, 0.9437, 0.9265, 0.8851, 0.8401, 0.8099, 0.8017,
						0.8138, 0.8122, 0.8008, 0.7852, 0.7963, 0.8056, 0.8000,
						0.8000, 0.7978, 0.7828, 0.7633, 0.7455, 0.7283, 0.7047,
						0.6708, 0.6510, 0.6590, 0.6695, 0.6666, 0.6447, 0.6185,
						0.5959, 0.5681, 0.5459, 0.5181, 0.4969, 0.4585, 0.4276,
						0.3985, 0.3940, 0.4000, 0.4000, 0.4000, 0.4000, 0.3978,
						0.3806, 0.3439, 0.2948, 0.2446, 0.2001, 0.1460, 0.0792,
						0.0264, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000,
						0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000 },
				{ -1.6860, -1.6859, -1.6860, -1.6863, -1.6866, -1.6871,
						-1.6876, -1.6883, -1.6890, -1.6898, -1.6906, -1.6915,
						-1.6924, -1.6933, -1.6942, -1.6952, -1.6961, -1.6970,
						-1.6979, -1.6988, -1.6996, -1.7003, -1.7010, -1.7015,
						-1.7020, -1.7024, -1.7027, -1.7028, -1.7028, -1.7027,
						-1.7024, -1.7019, -1.7013, -1.7005, -1.6994, -1.6982,
						-1.6968, -1.6951, -1.6932, -1.6910, -1.6884, -1.6855,
						-1.6822, -1.6784, -1.6741, -1.6693, -1.6639, -1.6578,
						-1.6511, -1.6437, -1.6356, -1.6266, -1.6169, -1.6065,
						-1.5952, -1.5833, -1.5706, -1.5572, -1.5430, -1.5281,
						-1.5126, -1.4963, -1.4793, -1.4617, -1.4434, -1.4244,
						-1.4047, -1.3844, -1.3634, -1.3419, -1.3196, -1.2968,
						-1.2733, -1.2492, -1.2246, -1.1993, -1.1734, -1.1470,
						-1.1200, -1.0924, -1.0643, -1.0356, -1.0064, -0.9767,
						-0.9464, -0.9156, -0.8843, -0.8525, -0.8202, -0.7875,
						-0.7542, -0.7205, -0.6863, -0.6516, -0.6165, -0.5810,
						-0.5450, -0.5086, -0.4718, -0.4345, -0.3969, -0.3589,
						-0.3204, -0.2816, -0.2425, -0.2029, -0.1630, -0.1228,
						-0.0822, -0.0413, -0.0000 },
				{ -2.2742, -2.2742, -2.2742, -2.2741, -2.2739, -2.2738,
						-2.2736, -2.2734, -2.2731, -2.2729, -2.2728, -2.2726,
						-2.2725, -2.2724, -2.2724, -2.2724, -2.2725, -2.2727,
						-2.2730, -2.2734, -2.2739, -2.2745, -2.2752, -2.2761,
						-2.2772, -2.2784, -2.2798, -2.2813, -2.2830, -2.2850,
						-2.2871, -2.2893, -2.2917, -2.2940, -2.2964, -2.2988,
						-2.3011, -2.3032, -2.3052, -2.3070, -2.3086, -2.3098,
						-2.3108, -2.3113, -2.3115, -2.3112, -2.3104, -2.3091,
						-2.3072, -2.3047, -2.3015, -2.2976, -2.2930, -2.2875,
						-2.2813, -2.2742, -2.2662, -2.2572, -2.2472, -2.2362,
						-2.2242, -2.2111, -2.1970, -2.1818, -2.1656, -2.1482,
						-2.1297, -2.1101, -2.0893, -2.0674, -2.0443, -2.0201,
						-1.9946, -1.9679, -1.9400, -1.9109, -1.8805, -1.8488,
						-1.8159, -1.7816, -1.7461, -1.7092, -1.6710, -1.6314,
						-1.5905, -1.5481, -1.5044, -1.4593, -1.4128, -1.3648,
						-1.3153, -1.2644, -1.2120, -1.1582, -1.1028, -1.0459,
						-0.9874, -0.9274, -0.8659, -0.8027, -0.7380, -0.6717,
						-0.6037, -0.5341, -0.4629, -0.3900, -0.3154, -0.2391,
						-0.1611, -0.0814, 0.0000 } };

		_aHVLength = Math.min(_aHV[POSITIVE_H2V].length, Math.min(
				_aHV[POSITIVE_H1V].length, Math.min(_aHV[NEGATIVE_H2V].length,
						_aHV[NEGATIVE_H1V].length)));
	}

	public static void LoadHVCurves(String filename) throws Exception {
		int nbCurves = 0;
		int nbPoints = 0;

		try {
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);

			if (fileReader != null) {
				BufferedReader buffReader = new BufferedReader(fileReader);
				String sLine = buffReader.readLine().trim();
				nbCurves = Integer.parseInt(sLine);
				sLine = buffReader.readLine().trim();
				nbPoints = Integer.parseInt(sLine);

				_aHV = new double[nbCurves][nbPoints];

				for (int curve = 0; curve < nbCurves; curve++) {
					sLine = buffReader.readLine().trim();
					int start = 0;
					int index = -1;
					String sValue;
					double value;

					for (int speed = 0; speed < nbPoints; speed++) {
						index = sLine.indexOf(',', start);

						if (index >= 0) {
							sValue = sLine.substring(start, index);
						} else {
							sValue = sLine.substring(start);
						}

						value = Double.parseDouble(sValue);

						_aHV[curve][speed] = value;

						start = index + 1;
					}
				}

				fileReader.close();
			}

			_aHVLength = nbPoints;

		} catch (Exception ex) {
			_aHV = null;

			throw new Exception(("LoadCurves Exception : " + ex.getMessage()));
		}
	}

	public HalfCycle() {
		_h = 0;
		_h_etoile = 0;
		_h = 0;
		_vmax = 0;
		_vmin = 0;
		_dv = 0;
		_h1 = 0;
		_h2 = 0;
	}

	public void initilizeHalfCycleSpeed(double speed) {
		_vmax = speed;
		_vmin = speed;
	}

	public void updateNegativeH1H2(double speed) {
		speed /= 3.6;
		int index = (int) Math.min(Math.max(0, Math.round(speed)),
				_aHVLength - 1);

		_h1 = _aHV[NEGATIVE_H1V][index];
		_h2 = _aHV[NEGATIVE_H2V][index];
		if (_log.isDebugEnabled()) {
			_log.debug("Speed/s[" + speed + "/" + index + "] H1[" + _h1
					+ "] H2[" + _h2 + "] H*[" + _h_high
					+ "]");
		}
	}

	public void updatePositiveH1H2(double speed) {
		speed /= 3.6;
		int index = (int) Math.min(Math.max(0, Math.round(speed)),
				_aHVLength - 1);

		_h1 = _aHV[POSITIVE_H1V][index];
		_h2 = _aHV[POSITIVE_H2V][index];
		if (_log.isDebugEnabled()) {
			_log.debug("Speed/s[" + speed + "/" + index + "] H1[" + _h1
					+ "] H2[" + _h2 + "] H*[" + _h_high
					+ "]");
		}
	}

	public void updateNegativeHalfCycle(double speed, double acceleration) {

		if (speed < _vmin)
			_vmin = speed;
		_dv = _vmax - _vmin;

		if (acceleration < _h)
			_h = acceleration;

		double app = Functions.FuzMemShip(-acceleration, -_h1, -_h2);

		if (app > _h_high) {
			_h_etoile = -acceleration; // _h_etoile
			_h_high = app; // _h_high
			if (_log.isDebugEnabled()) {
				_log.debug("UpdateNegativeCycle speed[" + speed
						+ "] acceleration[" + acceleration + "] accNearH2["
						+ _h_etoile + "] MSFAccBetweenH1H2Appartenance["
						+ _h_high + "] currentH1[" + _h1
						+ "] currentH2[" + _h2 + "]");
			}
		}

	}

	public void updatePositiveHalfCycle(double speed, double acceleration) {

		if (speed > _vmax)
			_vmax = speed;

		_dv = _vmax - _vmin;

		if (acceleration > _h)
			_h = acceleration;

		double app = Functions.FuzMemShip(acceleration, _h1, _h2);

		if (app > _h_high) {
			_h_etoile = acceleration; // _h_etoile
			_h_high = app; // _h_high
			if (_log.isDebugEnabled()) {
				_log.debug("UpdatePositiveCycle  speed[" + speed
						+ "] acceleration[" + acceleration + "] accNearH2["
						+ _h_etoile + "] MSFAccBetweenH1H2Appartenance["
						+ _h_high + "] currentH1[" + _h1
						+ "] currentH2[" + _h2 + "]");
			}
		}

	}

	public double getCycleAccMax() {
		return _h;
	}

	public double getAccNearH2() {
		return _h_etoile;
	}

	public double getMSFAccBetweenH1H2() {
		return _h_high;
	}

	public double getCycleDeltaSpeed() {
		return _dv;
	}

	public double getCycleVMax() {
		return _vmax;
	}

	public double getCycleVMin() {
		return _vmin;
	}

	public String toString() {
		String tostring = "halfcycle;";
		tostring += Double.toString(_vmin);
		tostring += ";" + Double.toString(_vmax);
		tostring += ";" + Double.toString(_dv);
		tostring += ";" + Double.toString(_h);
		tostring += ";" + Double.toString(_h_high);
		tostring += ";" + Double.toString(_h_etoile);

		return tostring;
	}

}
