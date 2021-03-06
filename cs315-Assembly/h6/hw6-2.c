/* File:    hw6-2.c
 *
 * Purpose: Use bubble sort to sort a list of ints.
 *
 * Compile: gcc -g -Wall -o hw6-2 hw6-2.c bubbleSort.s
 * Usage:   ./hw6-2 <n> <g|i>
 *             n:   number of elements in list
 *            'g':  generate list using a random number generator
 *            'i':  user input list
 *
 * Input:   list (optional)
 * Output:  sorted list
 */
#include <stdio.h>
#include <stdlib.h>

/* For random list, 0 <= keys < RMAX */
const int RMAX = 100;

void Usage(char* prog_name);
void Get_args(int argc, char* argv[], long* n_p, char* g_i_p);
void Generate_list(long a[], long n);
void Print_list(long a[], long n, char* title);
void Read_list(long a[], long n);
void Bubble_sort(long a[], long n);
void Swap(long* x_p, long* y_p);

/*-----------------------------------------------------------------*/
int main(int argc, char* argv[]) {
   long  n;
   char g_i;
   long* a;

   Get_args(argc, argv, &n, &g_i);
   a = malloc(n*sizeof(long));
   if (g_i == 'g') {
      Generate_list(a, n);
      Print_list(a, n, "Before sort");
   } else {
      Read_list(a, n);
   }

   Bubble_sort(a, n);

   Print_list(a, n, "After sort");
   
   free(a);
   return 0;
}  /* main */


/*-----------------------------------------------------------------
 * Function:  Usage
 * Purpose:   Summary of how to run program
 */
void Usage(char* prog_name) {
   fprintf(stderr, "usage:   %s <n> <g|i>\n", prog_name);
   fprintf(stderr, "   n:   number of elements in list\n");
   fprintf(stderr, "  'g':  generate list using a random number generator\n");
   fprintf(stderr, "  'i':  user input list\n");
}  /* Usage */


/*-----------------------------------------------------------------
 * Function:  Get_args
 * Purpose:   Get and check command line arguments
 * In args:   argc, argv
 * Out args:  n_p, g_i_p
 */
void Get_args(int argc, char* argv[], long* n_p, char* g_i_p) {
   if (argc != 3 ) {
      Usage(argv[0]);
      exit(0);
   }
   *n_p = strtol(argv[1], NULL, 10);
   *g_i_p = argv[2][0];

   if (*n_p <= 0 || (*g_i_p != 'g' && *g_i_p != 'i') ) {
      Usage(argv[0]);
      exit(0);
   }
}  /* Get_args */


/*-----------------------------------------------------------------
 * Function:  Generate_list
 * Purpose:   Use random number generator to generate list elements
 * In args:   n
 * Out args:  a
 */
void Generate_list(long a[], long n) {
   long i;

   srandom(1);
   for (i = 0; i < n; i++)
      a[i] = random() % RMAX;
}  /* Generate_list */


/*-----------------------------------------------------------------
 * Function:  Print_list
 * Purpose:   Print the elements in the list
 * In args:   a, n
 */
void Print_list(long a[], long n, char* title) {
   long i;

   printf("%s:\n", title);
   for (i = 0; i < n; i++)
      printf("%ld ", a[i]);
   printf("\n\n");
}  /* Print_list */


/*-----------------------------------------------------------------
 * Function:  Read_list
 * Purpose:   Read elements of list from stdin
 * In args:   n
 * Out args:  a
 */
void Read_list(long a[], long n) {
   long i;

   printf("Please enter the elements of the list\n");
   for (i = 0; i < n; i++)
      scanf("%ld", &a[i]);
}  /* Read_list */




