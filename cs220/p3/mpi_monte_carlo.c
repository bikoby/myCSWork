/* File:      monte_carlo.c
 * Autor:     Jinpeng Bi (Koby)
 * Purpose:   Calculate pi in Monte Carlo's way in MPI mode in different ways of
 *            sum modes.
 * Input:     cmd (to choose which sum mode to use)
 * Output:    Estimate pi and the total time used
 *
 * Compile:  mpicc -g -Wall -o mpi_monte_carlo mpi_monte_carlo.c -lm
 * Run:      mpiexec -n <number of processes> ./mpi_monte_carlo <n>
 *
 * Notes:
 *    1. number in command line has to be an interger.
 *    2. n has to be evenly divisible by p
 *    3. for BFLY, p has to be the power of 2
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h> 
#include "timer.h"

long long Simulation(long long local_n, int my_rank);
char Get_data(int p, int my_rank, long long* local_n);
double Get_time(double elapesed, int my_rank, int p);
void LOOP(int p, int my_rank,long long sum, double* pi_e, long long local_n);
void TREE(int p, int my_rank,long long sum, double* pi_e, long long local_n);
void REDUCE(int p, int my_rank,long long sum, double* pi_e, long long local_n);
void BFLY(int p, int my_rank,long long sum, double* pi_e, long long local_n);
void ALLREDUCE(int p, int my_rank,long long sum, double* pi_e, long long local_n);
char Get_cmd(void);

int main(int argc,char* argv[]){
    int        my_rank;
    int        p;
    long long  local_n;
    long long  sum;
    double     start;
    double     end;
    double     elapsed;
    double     pi_e; 
    double     pi = 4.0*atan(1.0);  
    char       cmd;

    /* Start up MPI */
    MPI_Init(&argc, &argv);

    /* Get the number of processes */
    MPI_Comm_size(MPI_COMM_WORLD, &p);

    /* Get my rank among all the processes */
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

    if(my_rank==0){
        long long n = strtol(argv[1], NULL, 10);
        local_n = n / p;
    }

    cmd = Get_data(p , my_rank, &local_n);


    MPI_Barrier(MPI_COMM_WORLD); 
    start=MPI_Wtime();    
    sum = Simulation(local_n, my_rank);
    switch (cmd) {
         case 'L':
         case 'l':  
            LOOP(p, my_rank, sum, &pi_e, local_n);
            break;
         case 'T':
         case 't':  
            TREE(p, my_rank, sum, &pi_e, local_n);
            break;
         case 'R':
         case 'r':  
            REDUCE(p, my_rank, sum, &pi_e, local_n);
            break;
         case 'B':
         case 'b':  
            BFLY(p, my_rank, sum, &pi_e, local_n);
            break;
         case 'A':
         case 'a':  
            ALLREDUCE(p, my_rank, sum, &pi_e, local_n);
            break;
    }  /* switch */
    end=MPI_Wtime();
    elapsed= end - start;
    elapsed= Get_time(elapsed, my_rank, p);

    if(my_rank==1){
         printf("n=%lld\nin_circle_count=%lld\npi_atan=%lf\npi_e=%lf\nerror=%lf\ntime=%lf\n",
                local_n*p,sum, pi,pi_e, pi-pi_e,elapsed);
    }
           
    MPI_Finalize();
    exit(0);
}

/*------------------------------------------------------------------
 * Function:    LOOP
 * Purpose:     Processes other than 0 send to 0, and process 0 executes a loop 
 *              of p-1 receives and adds. After caculating the pi_e, process 0
 *              send the pi_e to each process.
 * Input args:  p, my_rank, sum, pi_e, local_n
 * Output args: pi_e
 */
void LOOP(int p, int my_rank,long long sum, double* pi_e, long long local_n){    
    long long  temp_sum; 
    int        q;
    MPI_Status status;
    
    if (my_rank == 0) {
       for (q = 1; q < p; q++) {
         MPI_Recv(&temp_sum, 1, MPI_LONG_LONG, q, 
            0, MPI_COMM_WORLD, &status);
         sum = sum + temp_sum;
      } 
    } else {    
      MPI_Send(&sum, 1, MPI_LONG_LONG, 0, 0, MPI_COMM_WORLD);
    }

    if(my_rank == 0){
        *pi_e = (4.0 * sum / (local_n * p));
        for (q = 1; q < p; q++) {
            MPI_Send(pi_e, 1, MPI_DOUBLE, q, 0, MPI_COMM_WORLD);
        }
    } else {
           MPI_Recv(pi_e, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, &status);
    } 
}  /* LOOP */


/*------------------------------------------------------------------
 * Function:    TREE
 * Purpose:     Compute the global sum of ints stored on the processes, use a 
 *              tree stucture to calculate the sum, and then process 0 calculate
 *              the pi_e then sent back to other proccesses.
 * Input args:  p, my_rank, sum, pi_e, local_n
 * Output args: pi_e
 */
void TREE(int p, int my_rank,long long sum, double* pi_e, long long local_n){      
    long long  temp;
    int        partner;
    int        done = 0;
    unsigned   bitmask = 1;
    MPI_Status status;

    while (!done && bitmask < p) {
        partner = my_rank ^ bitmask;
        if(partner>=p) bitmask<<=1;
        partner = my_rank ^ bitmask;
        if(partner<p){
            if (my_rank < partner) {
                MPI_Recv(&temp, 1, MPI_LONG_LONG, partner, 0, MPI_COMM_WORLD, &status);
                sum += temp;
                bitmask <<= 1;
            } else {
                MPI_Send(&sum, 1, MPI_LONG_LONG, partner, 0, MPI_COMM_WORLD); 
                done = 1;
            }
        }
    }

    if(my_rank==0) *pi_e = (4.0 * sum / (local_n * p));
    done=0;
    bitmask=1;

    while (!done && bitmask <p) {
        partner = my_rank ^ bitmask;
        if(partner>=p) bitmask>>=1;
        partner = my_rank ^ bitmask;
        if(partner<p){
            if (my_rank < partner) {
                MPI_Send(pi_e, 1, MPI_LONG_LONG, partner, 0, MPI_COMM_WORLD);
                bitmask <<= 1;
            } else { 
                MPI_Recv(pi_e, 1, MPI_LONG, partner, 0, MPI_COMM_WORLD, &status);
                done = 1;
            }
        }
    }


    /* Valid only on all processes*/

}  /* TREE */

/*------------------------------------------------------------------
 * Function:    REDUCE
 * Purpose:     Compute the global sum of ints stored on the processes, use a 
 *              Reduce to get the total in process 0 and then calculate out the
 *              pi_e and then send then to each process.
 * Input args:  p, my_rank, sum, pi_e, local_n
 * Output args: pi_e
 */
void REDUCE(int p, int my_rank,long long sum, double* pi_e, long long local_n){      
    int        partner;
    int        done = 0;
    unsigned   bitmask = 1;
    MPI_Status status;
    long long  total=0;

    MPI_Reduce(&sum, &total, 1, MPI_LONG_LONG, MPI_SUM, 0, MPI_COMM_WORLD);

    if(my_rank==0) *pi_e = (4.0 * total / (local_n * p));
    done=0;
    bitmask=1;

    while (!done && bitmask <p) {
        partner = my_rank ^ bitmask;
        if(partner>=p) bitmask>>=1;
        partner = my_rank ^ bitmask;
        if(partner<p){
            if (my_rank < partner) {
                MPI_Send(pi_e, 1, MPI_LONG_LONG, partner, 0, MPI_COMM_WORLD);
                bitmask <<= 1;
            } else { 
                MPI_Recv(pi_e, 1, MPI_LONG, partner, 0, MPI_COMM_WORLD, &status);
                done = 1;
            }
        }
    }


    /* Valid only on all processes */

}  /* REDUCE */


/*------------------------------------------------------------------
 * Function:    BFLY
 * Purpose:     Compute the global sum of ints stored on the processes, use a 
 *              butterfly stucture to calculate the sum and each process gtes 
 *              the global sum and calculate out the pi_e.
 * Input args:  p, my_rank, sum, pi_e, local_n
 * Output args: pi_e
 */
void BFLY(int p, int my_rank,long long sum, double* pi_e, long long local_n){      
    long long  temp;
    int        partner;
    unsigned   bitmask = 1;
    MPI_Status status;

    while (bitmask < p) {
        partner = my_rank ^ bitmask;
        if(partner<p){                
                MPI_Send(&sum, 1, MPI_LONG_LONG, partner, 0, MPI_COMM_WORLD);
                MPI_Recv(&temp, 1, MPI_LONG_LONG, partner, 0, MPI_COMM_WORLD, &status);
                sum += temp;
                bitmask <<= 1;
        }
    }

    *pi_e = (4.0 * sum / (local_n * p));

}  /* BFLY */

/*------------------------------------------------------------------
 * Function:    ALLREDUCE
 * Purpose:     Compute the global sum stored on the processes using Allreudce
 *              and all processes get the total, then calculate the pi_e.
 * Input args:  p, my_rank, sum, pi_e, local_n
 * Output args: pi_e
 */
void ALLREDUCE(int p, int my_rank,long long sum, double* pi_e, long long local_n){      
    long long  total=0;

    MPI_Allreduce(&sum, &total, 1, MPI_LONG_LONG, MPI_SUM, MPI_COMM_WORLD);

    *pi_e = (4.0 * total / (local_n * p));
    /* Valid only on all processes */

}  /* ALLREDUCE */

/*------------------------------------------------------------------
 * Function:    Simulation
 * Purpose:     Thows local_n times random numbers and get the result of thowing
 *              in x*x+y*y<1. 
 * Input args:  local_n, my_rank
 * Output args: in_circle_count
 */
long long Simulation(long long local_n, int my_rank){
     double x,y;
     int i=0;
     long long in_circle_count=0;

     srandom(my_rank+1);

     for(; i < local_n ; i++){
        x = random()/((double) RAND_MAX);
        x = 2.0*x - 1.0;
        y = random()/((double) RAND_MAX);
        y = 2.0*y - 1.0;
        if((x*x+y*y)<1) in_circle_count++;
    }

    return in_circle_count;
}  /* Simulation */

/*------------------------------------------------------------------
 * Function:     Get_data
 * Purpose:      Read in the data on process 0 and send to other
 *               processes
 * Input args:   p, my_rank
 * Output args:  local_n, cmd
 */
char Get_data(int p, int my_rank, long long* local_n) {
   int        q;
   MPI_Status status;
   char       cmd;

   if (my_rank == 0) {
      cmd = Get_cmd();
      for (q = 1; q < p; q++) {
         MPI_Send(local_n, 1, MPI_LONG_LONG, q, 0, MPI_COMM_WORLD);
         MPI_Send(&cmd, 1, MPI_CHAR, q, 0, MPI_COMM_WORLD);
      }
   } else {
      MPI_Recv(local_n, 1, MPI_LONG_LONG, 0, 0, MPI_COMM_WORLD, &status);
      MPI_Recv(&cmd, 1, MPI_CHAR, 0, 0, MPI_COMM_WORLD, &status);
   }
   return cmd;
}  /* Get_data */

/*------------------------------------------------------------------
 * Function:    Get_time
 * Purpose:     Send each process's time to process 0, then come out with the 
 *              longest time.
 * Input args:  elapesed, my_rank, p
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


/*---------------------------------------------------------------------
 * Function:  Get_cmd
 * Purpose:   Get a 1-character command
 * Args:      None
 * Ret val:   The 1-character command
 */
char Get_cmd(void) {
   char cmd;

   printf("Enter a command (l, t, r, b, a):\n");
   scanf(" %c", &cmd);
   return cmd;
}  /* Get_cmd */
