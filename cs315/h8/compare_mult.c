/* File:     compare_mult.c
 * Purpose:  Compare the performance of the hardware multiplication
 *           algorithm with a proposed algorithm.
 *
 * Compile:  gcc -g -Wall -o compare_mult compare_mult.c
 * Run:      ./compare_mult <number of multiplications>
 *
 * Input:    None
 * Output:   Elapsed time for the multiplications done in hardware
 *           and the multiplications done with the proposed algorithm.
 * Result:   100
 *           Time for hardware = 2.145767e-06 seconds
 *           Time for proposed = 2.094030e-03 seconds
 *           1000
 *           Time for hardware = 1.311302e-05 seconds
 *           Time for proposed = 1.399803e-02 seconds
 *           10000
 *           Time for hardware = 1.299381e-04 seconds
 *           Time for proposed = 1.128201e-01 seconds
 *           10000
 *           Time for hardware = 1.306057e-03 seconds
 *           Time for proposed = 1.050684e+00 seconds
 * Conclusion: Running time of them increase linearly. The function in
 *             hardware is always 10^3 times faser than the proposed
 *             function. 
 *           The propsed function uses 
 *           min(x[1]+y[1])+min(x[2]+y[2])+...+min(x[n]+y[n]) iterations;
 *           The hardware function uses
 *             iters * 32 iterations;
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include "timer.h"

/* Largest value for one of the factors */
const int MAX = 10000;

int Hardware(int x, int y);
int Proposed(int x, int y);

int main(int argc, char* argv[]) {
    int iters, i, *x, *y, product;
    double start, finish, mult_elapsed, proposed_elapsed;
    
    if (argc != 2) {
        fprintf(stderr, "usage: %s <number of multiplies>\n",
                argv[0]);
        exit(0);
    }
    iters = strtol(argv[1], NULL, 10);
    x = malloc(iters*sizeof(int));
    y = malloc(iters*sizeof(int));
    
    /* Arrays of factors */
    srandom(1);
    for (i = 0; i < iters; i++) {
        x[i] = random() % MAX;
        y[i] = random() % MAX;
    }
    
    GET_TIME(start);
    for (i = 0; i < iters; i++)
        product = Hardware(x[i],y[i]);
    GET_TIME(finish);
    mult_elapsed = finish-start;
    
    GET_TIME(start);
    for (i = 0; i < iters; i++)  {
        product = Proposed(x[i], y[i]);
    }
    GET_TIME(finish);
    proposed_elapsed = finish-start;
    
    printf("Time for hardware = %e seconds\n", mult_elapsed);
    printf("Time for proposed = %e seconds\n", proposed_elapsed);
    
    free(x);
    free(y);
    
    return 0;
}

/*-------------------------------------------------------------------
 * Function:  Hardware
 * Purpose:   Multiply two numbers and return their product
 */
int Hardware(int x, int y) {
    return x*y;
}  /* Mult */


/*-------------------------------------------------------------------
 * Function:  Proposed
 * Purpose:   Multiply two numbers using a proposed algorithm and
 *            return their product
 */
int Proposed(int x, int y) {
    // multiplier and multiplicand >= 0
    int i,multiplicand, multiplier;
    if (x > y) {
        multiplier = y;
        multiplicand =x;
    }else{
        multiplicand = y;
        multiplier = x;
    }
    int product = 0;
    for (i = 0; i < multiplier; i++)
        product += multiplicand;
    return product;
}  /* Proposed */