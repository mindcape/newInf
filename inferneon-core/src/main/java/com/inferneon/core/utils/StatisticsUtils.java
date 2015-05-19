package com.inferneon.core.utils;

public class StatisticsUtils {

	/*************************************************
	 * COEFFICIENTS FOR METHOD normalInverse() *
	 *************************************************/
	/* approximation for 0 <= |y - 0.5| <= 3/8 */
	protected static final double P0[] = { -5.99633501014107895267E1,
		9.80010754185999661536E1, -5.66762857469070293439E1,
		1.39312609387279679503E1, -1.23916583867381258016E0, };
	protected static final double Q0[] = {
		/* 1.00000000000000000000E0, */
		1.95448858338141759834E0, 4.67627912898881538453E0, 8.63602421390890590575E1,
		-2.25462687854119370527E2, 2.00260212380060660359E2,
		-8.20372256168333339912E1, 1.59056225126211695515E1,
		-1.18331621121330003142E0, };

	/*
	 * Approximation for interval z = sqrt(-2 log y ) between 2 and 8 i.e., y
	 * between exp(-2) = .135 and exp(-32) = 1.27e-14.
	 */
	protected static final double P1[] = { 4.05544892305962419923E0,
		3.15251094599893866154E1, 5.71628192246421288162E1,
		4.40805073893200834700E1, 1.46849561928858024014E1,
		2.18663306850790267539E0, -1.40256079171354495875E-1,
		-3.50424626827848203418E-2, -8.57456785154685413611E-4, };
	protected static final double Q1[] = {
		/* 1.00000000000000000000E0, */
		1.57799883256466749731E1, 4.53907635128879210584E1, 4.13172038254672030440E1,
		1.50425385692907503408E1, 2.50464946208309415979E0,
		-1.42182922854787788574E-1, -3.80806407691578277194E-2,
		-9.33259480895457427372E-4, };
	
	/*
	   * Approximation for interval z = sqrt(-2 log y ) between 8 and 64 i.e., y
	   * between exp(-32) = 1.27e-14 and exp(-2048) = 3.67e-890.
	   */
	  protected static final double P2[] = { 3.23774891776946035970E0,
	    6.91522889068984211695E0, 3.93881025292474443415E0,
	    1.33303460815807542389E0, 2.01485389549179081538E-1,
	    1.23716634817820021358E-2, 3.01581553508235416007E-4,
	    2.65806974686737550832E-6, 6.23974539184983293730E-9, };
	  protected static final double Q2[] = {
	  /* 1.00000000000000000000E0, */
	  6.02427039364742014255E0, 3.67983563856160859403E0, 1.37702099489081330271E0,
	    2.16236993594496635890E-1, 1.34204006088543189037E-2,
	    3.28014464682127739104E-4, 2.89247864745380683936E-6,
	    6.79019408009981274425E-9, };

	/**
	 * Returns the value, <tt>x</tt>, for which the area under the Normal
	 * (Gaussian) probability density function (integrated from minus infinity to
	 * <tt>x</tt>) is equal to the argument <tt>y</tt> (assumes mean is zero,
	 * variance is one).
	 * <p>
	 * For small arguments <tt>0 < y < exp(-2)</tt>, the program computes
	 * <tt>z = sqrt( -2.0 * log(y) )</tt>; then the approximation is
	 * <tt>x = z - log(z)/z  - (1/z) P(1/z) / Q(1/z)</tt>. There are two rational
	 * functions P/Q, one for <tt>0 < y < exp(-32)</tt> and the other for
	 * <tt>y</tt> up to <tt>exp(-2)</tt>. For larger arguments,
	 * <tt>w = y - 0.5</tt>, and <tt>x/sqrt(2pi) = w + w**3 R(w**2)/S(w**2))</tt>.
	 * 
	 * @param y0 the area under the normal pdf
	 * @return the z-value
	 */
	public static double normalInverse(double y0) {

		double x, y, z, y2, x0, x1;
		int code;

		final double s2pi = Math.sqrt(2.0 * Math.PI);

		if (y0 <= 0.0) {
			throw new IllegalArgumentException();
		}
		if (y0 >= 1.0) {
			throw new IllegalArgumentException();
		}
		code = 1;
		y = y0;
		if (y > (1.0 - 0.13533528323661269189)) { /* 0.135... = exp(-2) */
				y = 1.0 - y;
				code = 0;
		}

		if (y > 0.13533528323661269189) {
			y = y - 0.5;
			y2 = y * y;
			x = y + y * (y2 * polevl(y2, P0, 4) / p1evl(y2, Q0, 8));
			x = x * s2pi;
			return (x);
		}

		x = Math.sqrt(-2.0 * Math.log(y));
		x0 = x - Math.log(x) / x;

		z = 1.0 / x;
		if (x < 8.0) {
			x1 = z * polevl(z, P1, 8) / p1evl(z, Q1, 8);
		} else {
			x1 = z * polevl(z, P2, 8) / p1evl(z, Q2, 8);
		}
		x = x0 - x1;
		if (code != 0) {
			x = -x;
		}
		return (x);
	}

	/**
	   * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
	   * 
	   * <pre>
	   *                     2          N
	   * y  =  C  + C x + C x  +...+ C x
	   *        0    1     2          N
	   * 
	   * Coefficients are stored in reverse order:
	   * 
	   * coef[0] = C  , ..., coef[N] = C  .
	   *            N                   0
	   * </pre>
	   * 
	   * In the interest of speed, there are no checks for out of bounds arithmetic.
	   * 
	   * @param x argument to the polynomial.
	   * @param coef the coefficients of the polynomial.
	   * @param N the degree of the polynomial.
	   */
	  public static double polevl(double x, double coef[], int N) {

	    double ans;
	    ans = coef[0];

	    for (int i = 1; i <= N; i++) {
	      ans = ans * x + coef[i];
	    }

	    return ans;
	  }

	  /**
	   * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
	   * Evaluates polynomial when coefficient of N is 1.0. Otherwise same as
	   * <tt>polevl()</tt>.
	   * 
	   * <pre>
	   *                     2          N
	   * y  =  C  + C x + C x  +...+ C x
	   *        0    1     2          N
	   * 
	   * Coefficients are stored in reverse order:
	   * 
	   * coef[0] = C  , ..., coef[N] = C  .
	   *            N                   0
	   * </pre>
	   * 
	   * The function <tt>p1evl()</tt> assumes that <tt>coef[N] = 1.0</tt> and is
	   * omitted from the array. Its calling arguments are otherwise the same as
	   * <tt>polevl()</tt>.
	   * <p>
	   * In the interest of speed, there are no checks for out of bounds arithmetic.
	   * 
	   * @param x argument to the polynomial.
	   * @param coef the coefficients of the polynomial.
	   * @param N the degree of the polynomial.
	   */
	  public static double p1evl(double x, double coef[], int N) {

	    double ans;
	    ans = x + coef[0];

	    for (int i = 1; i < N; i++) {
	      ans = ans * x + coef[i];
	    }

	    return ans;
	  }	  
}
