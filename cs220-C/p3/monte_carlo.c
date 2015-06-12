/* File:      monte_carlo.c
 * Author:    Jinpeng Bi (Koby)
 * Purpose:   Calculate pi in Monte Carlo's way
 * Input:     None
 * Output:    Estimate pi and the time used
 *
 *
 * Compile:  gcc -g -Wall -o monte_carlo monte_carlo.c -lm
 * Run:      ./monte_carlo <n>
 *
 * Notes:
 *    1. number in command line has to be an interger.
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "timer.h"

int main(int argc,char* argv[]){
    long long n = strtol(argv[1], NULL, 10);
    double pi = 4.0*atan(1.0);
    double pi_e;
    double x,y;
    long long in_circle_count = 0;
    double start,finish,elapsed;
    int i=0;
    
    srandom(1);

    GET_TIME(start);
    for(; i < n ; i++){
        x = random()/((double) RAND_MAX);
        x = 2.0*x - 1.0;/
        y = random()/((double) RAND_MAX);
        y = 2.0*y - 1.0;
        if((x*x+y*y)<1) in_circle_count++;
    }
    pi_e = 4.0 * in_circle_count / n;

    GET_TIME(finish);
    elapsed = finish - start;

    printf("n=%lld\nin_circle_count=%lld\npi_atan=%lf\npi_e=%lf\nerror=%lf\ntime=%lf\n",
           n, in_circle_count, pi,pi_e, pi-pi_e,elapsed);
    return 0;
}