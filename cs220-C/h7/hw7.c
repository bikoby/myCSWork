/* File:     global_sum.c
 * Author:   Jinpeng Bi(Koby)
 *
 * Purpose:  Implements a tree-structured global sum.
 * 
 * Input:    None.
 * Output:   Random values generated by processes, and sum of random values.
 *
 * Compile:  mpicc -g -Wall -o hw7 hw7.c
 * Run:      mpiexec -n <number of processes> hw7
 *
 * Notes:     
 *    1.  The result is valid only on process 0.
 */
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

const int MAX_CONTRIB = 10;

int Global_sum(int my_contrib, int my_rank, int p, MPI_Comm comm);

int main(int argc, char* argv[]) {
   int      p, my_rank;
   MPI_Comm comm;
   int      my_contrib;
   int      sum;

   MPI_Init(&argc, &argv);
   comm = MPI_COMM_WORLD;
   MPI_Comm_size(comm, &p);
   MPI_Comm_rank(comm, &my_rank);

   srandom(my_rank+1);
   my_contrib = random() % MAX_CONTRIB;
   printf("Proc %d > my_contrib = %d\n", my_rank, my_contrib);
   sum = Global_sum(my_contrib, my_rank, p, comm);
   if (my_rank == 0)
      printf("Proc %d > global sum = %d\n", my_rank, sum);

   MPI_Finalize();
   return 0;
}  /* main */

/*-----------------------------------------------------------------*/
/* Function:    Global_sum
 * Purpose:     Compute the global sum of ints stored on the processes
 *
 * Input args:  my_contrib = process's contribution to the global sum
 *              my_rank = process's rank
 *              p = number of processes
 *              comm = communicator
 * Return val:  Sum of each process's my_contrib:  valid only
 *              on process 0.
 *
 * Notes:
 *    1.  Uses tree structured communication.
 *    2.  p, the number of processes must be a power of 2.
 *    3.  The return value is valid only on process 0.
 *    4.  The pairing of the processes is done using bitwise
 *        exclusive or.  Here's a table showing the rule for
 *        for bitwise exclusive or
 *           X Y X^Y
 *           0 0  0
 *           0 1  1
 *           1 0  1
 *           1 1  0
 *        Here's a table showing the process pairing with 8
 *        processes (r = my_rank, other column heads are bitmask)
 *           r     001 010 100 
 *           -     --- --- ---
 *           0 000 001 010 100  
 *           1 001 000  x   x
 *           2 010 011 000  x
 *           3 011 010  x   x
 *           4 100 101 110 000
 *           5 101 100  x   x
 *           6 110 111 100  x
 *           7 111 110  x   x
 */
int Global_sum(int my_contrib, int my_rank, int p, MPI_Comm comm) {
    int        sum = my_contrib;
    int        temp;
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
                MPI_Recv(&temp, 1, MPI_INT, partner, 0, comm, &status);
                sum += temp;
                bitmask <<= 1;
            } else {
                MPI_Send(&sum, 1, MPI_INT, partner, 0, comm); 
                done = 1;
            }
        }
    }


    /* Valid only on 0 */
    return sum;
}  /* Global_sum */
