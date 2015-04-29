/* File:    hw6.c
 * Author:  Jinpeng Bi(Koby)
 * Purpose: Calculate area using Simpson's rule and print out the time it used.
 *
 * Input:   a, b, n
 * Output:  estimate time of calculating Simpson's rule with multipul processes
 *
 * Compile: mpicc -g -Wall -o hw6 hw6.c -lm
 * Usage:   mpiexec -n <number of processes> ./hw6
 *
 * Results for: p=1 a=0 b=1 n=100,000. the total time is: 0.038141 ms
 *              p=2 a=0 b=1 n=100,000. the total time is: 0.035570 ms    
 *              p=4 a=0 b=1 n=100,000. the total time is: 0.017834 ms
 *
 * Notes:    
 * 1.  The number of subintervals, n, should be even
 */
 
#include <stdio.h>
#include <math.h>
#include <mpi.h>     /* For MPI functions, etc */

double Simpson(double  a, double  b, int n, double h);
double f(double x);    /* Function we're integrating */
double Get_time(double elapesed, int my_rank, int p);
void   Get_data(int p, int my_rank, double* a_p, double* b_p, int* n_p);

int main(int argc, char** argv) {
   double  area_s;     /* Store result in area       */
   double  a;          /* Left endpoints   */
   double  b;          /* Right endpoints   */
   double  ia;         /* Right endpoints of each process  */ 
   double  ib;         /* Left endpoints of each process  */
   int     n;          /* Number of subintervals     */
   double  h;          /* Length of subintervals     */
   int     my_rank;
   int     p;          /* Number of processes  */
   int     q;
   int     e;          /* Local n for each process  */
   double  sum=0;  
   double  start;
   double  end;
   double  elapsed; 
   MPI_Status  status;

   /* Start up MPI */
   MPI_Init(&argc, &argv);

   /* Get the number of processes */
   MPI_Comm_size(MPI_COMM_WORLD, &p);

   /* Get my rank among all the processes */
   MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    
   Get_data(p, my_rank, &a, &b, &n);

   h = (b-a)/n;
   e = n/p;
  
   ia= a + my_rank * h * e;
   ib= a + (my_rank+1) * h * e;

   MPI_Barrier(MPI_COMM_WORLD);
   start=MPI_Wtime();
   area_s = Simpson(ia, ib, e, h);
   
   if (my_rank == 0) {
       sum = area_s;
       for (q = 1; q < p; q++) {
         MPI_Recv(&area_s, 1, MPI_DOUBLE, q, 
            0, MPI_COMM_WORLD, &status);
         sum = sum + area_s;
      } 
   } else {
        
      MPI_Send(&area_s, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
   }
   end=MPI_Wtime();
   elapsed= end - start;
   elapsed= Get_time(elapsed, my_rank, p);
   if(my_rank==0){
        printf("the total time is:%f ms\n", elapsed);
   }
   
   /* Shut down MPI */
   MPI_Finalize();    

   return 0;
}  /* main */



/*------------------------------------------------------------------
 * Function:     Simpson
 * Purpose:      Estimate area using Simpson's rule
 * Input args:   a: left endpoint
 *               b: right endpoint
 *               n: number of subintervals
 *               h: stepsize = length of subintervals
 * Return val:   Simpson's rule estimate of area between x-axis,
 *               x = a, x = b, and graph of f(x)
 * Note:         Function assumes n is even.
 */
double Simpson(double  a, double  b, int n, double h) {
   double area, x, temp;
   int i;

   area = f(a) + f(b);

   temp = 0.0;
   for (i = 1; i <= n-1; i += 2) {
      x = a + i*h;
      temp += f(x);
   }
   area += 4*temp;
   
   temp = 0;
   for (i = 2; i <= n-2; i += 2) {
      x = a + i*h;
      temp += f(x);
   }
   area += 2*temp;

   area *= h;
   area /= 3;

   return area;
}  /* Simpson */


/*------------------------------------------------------------------
 * Function:    f
 * Purpose:     Compute value of function to be integrated
 * Input args:  x
 */
double f(double x) {
   double return_val;

   return_val = exp(sin(x));
   return return_val;
}  /* f */

/*------------------------------------------------------------------
 * Function:    Get_time
 * Purpose:     Send each process's time to process 0, then come out with the 
 *              longest time.
 * Input args:  x
 */
double Get_time(double elapesed, int my_rank, int p) {
   MPI_Status status;
   if(my_rank==0){
        double temp = elapesed;
        int q;
        for (q = 1; q < p; q++) {
            MPI_Recv(&elapesed, 1, MPI_DOUBLE, q, 
            0, MPI_COMM_WORLD, &status);
            if(elapesed>temp) temp=elapesed;
        }
    }else{
       MPI_Send(&elapesed, 1, MPI_DOUBLE, 0, 0,
            MPI_COMM_WORLD);
    } 
    return elapesed;
}  /* Get_time */

/*------------------------------------------------------------------
 * Function:     Get_data
 * Purpose:      Read in the data on process 0 and send to other
 *               processes
 * Input args:   p, my_rank
 * Output args:  a_p, b_p, n_p
 */
void Get_data(int p, int my_rank, double* a_p, double* b_p, int* n_p) {
   int        q;
   MPI_Status status;

   if (my_rank == 0) {
      printf("Enter a, b, and n\n");
      scanf("%lf %lf %d", a_p, b_p, n_p);

      for (q = 1; q < p; q++) {
         MPI_Send(a_p, 1, MPI_DOUBLE, q, 0, MPI_COMM_WORLD);
         MPI_Send(b_p, 1, MPI_DOUBLE, q, 0, MPI_COMM_WORLD);
         MPI_Send(n_p, 1, MPI_INT, q, 0, MPI_COMM_WORLD);
      }
   } else {
      MPI_Recv(a_p, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(b_p, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(n_p, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status);
   }
}  /* Get_data */
