/* File:    trap.c
 * Purpose: Calculate area using trapezoidal rule.
 *
 * Input:   a, b, n
 * Output:  estimate of area between x-axis, x = a, x = b, and graph of f(x)
 *          using n trapezoids.
 *
 * Compile: gcc -g -Wall -o trap trap.c
 * Run:     ./trap
 *
 * Note:    The function f(x) is hardwired.
 */

#include <stdio.h>
#include <math.h>

double Trap(double  a, double  b, int n, double h);
double Simpson(double  a, double  b, int n, double h);
double f(double x);    /* Function we're integrating */

int main(void) {
   double  areaTrap, areaSimpson;       /* Store result in area       */
   double  a, b;       /* Left and right endpoints   */
   int     n;          /* Number of trapezoids       */
   double  h;          /* Trapezoid base width       */

   printf("Enter a, b, and n\n");
   scanf("%lf", &a);
   scanf("%lf", &b);
   scanf("%d", &n);

   h = (b-a)/n;
   areaTrap = Trap(a, b, n, h);
   areaSimpson = Simpson(a,b,n,h);

   printf("With n = %d trapezoids, our estimate\n", n);
   printf("(by Trap) of the area from %f to %f = %.15f\n",
      a, b, areaTrap);
   printf("(by Simpson) of the area from %f to %f = %.15f\n",
         a, b, areaSimpson);
   printf("the difference between this two approximations of the area from %f to %f = %.15f\n",
         a, b, fabs(areaTrap-areaSimpson));

   return 0;
}  /* main */

/*------------------------------------------------------------------
 * Function:     Trap
 * Purpose:      Estimate area using the trapezoidal rule
 * Input args:   a: left endpoint
 *               b: right endpoint
 *               n: number of trapezoids
 *               h: stepsize = length of base of trapezoids
 * Return val:   Trapezoidal rule estimate of area between x-axis,
 *               x = a, x = b, and graph of f(x)
 */
double Trap(double a, double b, int n, double h) {
    double area;   /* Store result in area  */
    double x;
    int i;

    area = (f(a) + f(b))/2.0;
    for (i = 1; i <= n-1; i++) {
        x = a + i*h;
        area = area + f(x);
    }
    area = area*h;

    return area;
} /*  Trap  */

double Simpson(double a, double b, int n, double h){
	double area;
	double x;
	int i;

	area = f(a) + f(b);
	for (i = 1; i <= n-1; i++) {
		x = a + i*h;
	    if(i%2!=0){
	    	area = area + 2*f(x);
	    }else{
	    	area = area + 4*f(x);
	    }
	}
	area = area*(h/3);

	return area;

}


/*------------------------------------------------------------------
 * Function:    f
 * Purpose:     Compute value of function to be integrated
 * Input args:  x
 */
double f(double x) {
   double return_val;

   return_val = x*x + 1;
   return return_val;
}  /* f */
