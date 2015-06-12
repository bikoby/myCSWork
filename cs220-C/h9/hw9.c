/* File:     
 *     hw9.c
 * 
 * Author:Jinpeng Bi(Koby)
 *
 * Purpose:  
 *     computes a DAXPY(DAXPY stands for "Double precision Alpha X Plus Y.")
 *
 * Input:
 *     n: number of doubles in vector x and y
 *     x, y: the vector to be multiplied
 *     alpah: the nubmer to be multiplied
 *
 * Output:
 *     y: the product vector
 *
 * Compile:  gcc -g -Wall -o hw9 hw9.c -lpthread
 * Usage:
 *     ./hw9 <thread_count>
 *
 * Notes:  
 *     1.  Local storage for n, alpha, x, y is dynamically allocated.
 *     2.  Number of threads (thread_count) should evenly divide both 
 *         n.  The program doesn't check for this.
 *     3.  Distribution of n, alpha, x, and y is logical:  all four are 
 *         globally shared.
 */

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

/* Global variables */
int     thread_count;
int     n;
double alpha;
double* x;
double* y;

/* Serial functions */
void Usage(char* prog_name);
void Read_vector(char* prompt, double x[], int n);
void Print_vector(char* title, double y[], int m);

/* Parallel function */
void *Pth_mat_vect(void* rank);

/*------------------------------------------------------------------*/
int main(int argc, char* argv[]) {
   long       thread;
   pthread_t* thread_handles;

   if (argc != 2) Usage(argv[0]);
   thread_count = strtol(argv[1], NULL, 10);
   thread_handles = malloc(thread_count*sizeof(pthread_t));

   printf("Enter  n\n");
   scanf("%d", &n);


   x = malloc(n*sizeof(double));
   y = malloc(n*sizeof(double));
  

   Read_vector("Enter the vector for x", x, n);
   Print_vector("We read", x, n);

   Read_vector("Enter the vector for y", y, n);
   Print_vector("We read", x, n);

   printf("Enter the vector for alpha\n");
   scanf("%lf",&alpha);

   for (thread = 0; thread < thread_count; thread++)
      pthread_create(&thread_handles[thread], NULL,
         Pth_mat_vect, (void*) thread);

   for (thread = 0; thread < thread_count; thread++)
      pthread_join(thread_handles[thread], NULL);

   Print_vector("The product is", y, n);

   free(x);
   free(y);
   free(thread_handles);

   return 0;
}  /* main */


/*------------------------------------------------------------------
 * Function:  Usage
 * Purpose:   print a message showing what the command line should
 *            be, and terminate
 * In arg :   prog_name
 */
void Usage (char* prog_name) {
   fprintf(stderr, "usage: %s <thread_count>\n", prog_name);
   exit(0);
}  /* Usage */




/*------------------------------------------------------------------
 * Function:        Read_vector
 * Purpose:         Read in the vector x
 * In arg:          prompt, n
 * Out arg:         x
 */
void Read_vector(char* prompt, double x[], int n) {
   int   i;

   printf("%s\n", prompt);
   for (i = 0; i < n; i++) 
      scanf("%lf", &x[i]);
}  /* Read_vector */



/*------------------------------------------------------------------
 * Function:    Print_vector
 * Purpose:     Print a vector
 * In args:     title, y, m
 */
void Print_vector(char* title, double y[], int m) {
   int   i;

   printf("%s\n", title);
   for (i = 0; i < m; i++)
      printf("%4.1f ", y[i]);
   printf("\n");
}  /* Print_vector */

/*------------------------------------------------------------------
 * Function:       Pth_mat_vect
 * Purpose:        Multiply an mxn matrix by an nx1 column vector
 * In arg:         rank
 * Global in vars: A, x, m, n, thread_count
 * Global out var: y
 */
void *Pth_mat_vect(void* rank) {
   long my_rank = (long) rank;
   int i;
   int local_n = n/thread_count; 
   int my_first_row = my_rank*local_n;
   int my_last_row = my_first_row + local_n - 1;

   for (i = my_first_row; i <= my_last_row; i++) {
         y[i] += alpha*x[i];
   }

   return NULL;
}  /* Pth_mat_vect */
