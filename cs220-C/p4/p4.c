/*
 * File:     parallel_odd_even.c
 * Purpose:  Implement parallel odd-even sort of an array of 
 *           nonegative ints
 * Input:
 *    A:     elements of array (optional)
 * Output:
 *    A:     elements of A after sorting
 *
 * Compile:  mpicc -g -Wall -o parallel_odd_even parallel_odd_even.c
 * Run:
 *    mpiexec -n <p> parallel_odd_even <g|i> <global_n> 
 *       - p: the number of processes
 *       - g: generate random, distributed list
 *       - i: user will input list on process 0
 *       - global_n: number of elements in global list
 *
 * Notes:
 * 1.  global_n must be evenly divisible by p
 * 2.  Except for debug output, process 0 does all I/O
 * 3.  Optional -DDEBUG compile flag for verbose output
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <mpi.h>

// const int RMAX = 1000000000;
const int RMAX = 100;

/* Local functions */
void Usage(char* program);
void Print_list(int local_A[], int local_n, int rank);
void Merge_split_low(int local_A[], int temp_B[], int temp_C[], 
         int local_n);
void Merge_split_high(int local_A[], int temp_B[], int temp_C[], 
        int local_n);
int  Compare(const void* a_p, const void* b_p);

/* Functions involving communication */
void Get_args(int argc, char* argv[], int* global_n_p, int* local_n_p, 
         int* global_s_p, int my_rank, int p, MPI_Comm comm);
void Sort(int** local_A_p, int* local_n_p, int my_rank, 
         int p, MPI_Comm comm, int global_s);
void Odd_even_iter(int local_A[], int temp_B[], int temp_C[],
         int local_n, int phase, int even_partner, int odd_partner,
         int my_rank, int p, MPI_Comm comm);
void Print_local_lists(int local_A[], int local_n, 
         int my_rank, int p, MPI_Comm comm);
void Print_global_list(int local_A[], int local_n, int my_rank,
         int p, MPI_Comm comm);
void Read_list(int local_A[], int local_n, int my_rank, int p,
         MPI_Comm comm);
void Swap(int** local_A, int* local_n_p, int my_rank,int p, double spliters[],
         MPI_Comm comm);
void GetSpliter(int sample[], int num, double* leftSpliter_p, int my_rank, int p, 
                int phase, int even_partner, int odd_partner, 
                MPI_Comm comm);
void GetSample(int local_A[], int local_n, int sample[], int p, int global_s,
                int my_rank);                



/*-------------------------------------------------------------------*/
int main(int argc, char* argv[]) {
   int my_rank, p;
   int *local_A;
   int global_n;
   int global_s;
   int local_n;
   MPI_Comm comm;

   MPI_Init(&argc, &argv);
   comm = MPI_COMM_WORLD;
   MPI_Comm_size(comm, &p);
   MPI_Comm_rank(comm, &my_rank);

   Get_args(argc, argv, &global_n, &local_n, &global_s, my_rank, p, comm);
   local_A = (int*) malloc(local_n*sizeof(int));
   Read_list(local_A, local_n, my_rank, p, comm);

   Sort(&local_A, &local_n, my_rank, p, comm, global_s);
   MPI_Barrier(comm);
   Print_global_list(local_A, local_n, my_rank, p, comm);

   free(local_A);

   MPI_Finalize();

   return 0;
}  /* main */


/*-------------------------------------------------------------------
 * Function:  Usage
 * Purpose:   Print command line to start program
 * In arg:    program:  name of executable
 * Note:      Purely local, run only by process 0;
 */
void Usage(char* program) {
   fprintf(stderr, "usage:  mpiexec -n <p> %s <global_s> <global_n>\n",
       program);
   fprintf(stderr, "   - p: the number of processes \n");
   fprintf(stderr, "   - global_s: sample size\n");
   fprintf(stderr, "   - global_n: number of elements in global list\n");
   fprintf(stderr, " (must be evenly divisible by p)\n");
   fflush(stderr);
}  /* Usage */


/*-------------------------------------------------------------------
 * Function:    Get_args
 * Purpose:     Get and check command line arguments
 * Input args:  argc, argv, my_rank, p, comm
 * Output args: global_n_p, local_n_p, gi_p
 */
void Get_args(int argc, char* argv[], int* global_n_p, int* local_n_p, 
         int* global_s_p, int my_rank, int p, MPI_Comm comm) {

   if (my_rank == 0) {
      if (argc != 3) {
         Usage(argv[0]);
         *global_n_p = -1;  /* Bad args, quit */
      } else {
            *global_s_p = strtol(argv[1], NULL, 10);
            *global_n_p = strtol(argv[2], NULL, 10);
            if (*global_n_p % p != 0 || *global_s_p % p != 0) {
               Usage(argv[0]);
               *global_n_p = -1;
            }
         
      }
   }  /* my_rank == 0 */

   MPI_Bcast(global_n_p, 1, MPI_INT, 0, comm);
   MPI_Bcast(global_s_p, 1, MPI_INT, 0, comm);

   if (*global_n_p <= 0) {
      MPI_Finalize();
      exit(-1);
   }

   *local_n_p = *global_n_p/p;
}  /* Get_args */


/*-------------------------------------------------------------------
 * Function:   Read_list
 * Purpose:    process 0 reads the list from stdin and scatters it
 *             to the other processes.
 * In args:    local_n, my_rank, p, comm
 * Out arg:    local_A
 */
void Read_list(int local_A[], int local_n, int my_rank, int p,
         MPI_Comm comm) {
   int i;
   int *temp = NULL;

   if (my_rank == 0) {
      temp = (int*) malloc(p*local_n*sizeof(int));
      printf("Enter the elements of the list\n");
      for (i = 0; i < p*local_n; i++)
         scanf("%d", &temp[i]);
   } 

   MPI_Scatter(temp, local_n, MPI_INT, local_A, local_n, MPI_INT,
       0, comm);

   if (my_rank == 0)
      free(temp);
}  /* Read_list */


/*-------------------------------------------------------------------
 * Function:   Print_global_list
 * Purpose:    Print the contents of the global list A
 * Input args:  
 *    n, the number of elements 
 *    A, the list
 * Note:       Purely local, called only by process 0
 */
void Print_global_list(int local_A[], int local_n, int my_rank, int p, 
      MPI_Comm comm) { 
   if(my_rank==0){
        MPI_Status status;
        int i,j,r;
        int* rec;
        printf("process %d:",my_rank);
        for(j=0;j<local_n;j++)
               printf("%d ",local_A[j]);
        printf("\n");
        for(i=1;i<p;i++){
             MPI_Probe(i, 0, comm, &status);
             MPI_Get_count(&status, MPI_INT, &r);
             rec=realloc(rec,r*sizeof(int));
             MPI_Recv(rec,r,MPI_INT,i,0,comm,&status);
             printf("process %d:",i);
             for(j=0;j<r;j++)
                    printf("%d ",rec[j]);
             printf("\n");
             
        }
    }else{
       
       MPI_Send(local_A, local_n, MPI_INT, 0, 0, comm);
       
    }

}  /* Print_global_list */

/*-------------------------------------------------------------------
 * Function:    Compare
 * Purpose:     Compare 2 ints, return -1, 0, or 1, respectively, when
 *              the first int is less than, equal, or greater than
 *              the second.  Used by qsort.
 */
int Compare(const void* a_p, const void* b_p) {
   int a = *((int*)a_p);
   int b = *((int*)b_p);

   if (a < b)
      return -1;
   else if (a == b)
      return 0;
   else /* a > b */
      return 1;
}  /* Compare */

/*-------------------------------------------------------------------
 * Function:    Sort
 * Purpose:     Use odd-even sort to sort global list.
 * Input args:  local_n, my_rank, p, comm
 * In/out args: local_A 
 */
void Sort(int** local_A_p, int* local_n_p, int my_rank, 
         int p, MPI_Comm comm, int global_s) {
   int *sample;
   int phase;
   int *temp_B, *temp_C;
   int even_partner;  /* phase is even or left-looking */
   int odd_partner;   /* phase is odd or right-looking */
   double leftSpliter;
   int num=global_s/p;
   double *spliters;
   int* local_A=*local_A_p;
   int local_n=*local_n_p;

   sample = (int*) malloc(num*sizeof(int));
   spliters= malloc(p*sizeof(double));
   GetSample(local_A, local_n, sample, p, global_s, my_rank);
    

   /* Find partners:  negative rank => do nothing during phase */
   if (my_rank % 2 != 0) {
      even_partner = my_rank - 1;
      odd_partner = my_rank + 1;
      if (odd_partner == p) odd_partner = -1;  // Idle during odd phase
   } else {
      even_partner = my_rank + 1;
      if (even_partner == p) even_partner = -1;  // Idle during even phase
      odd_partner = my_rank-1;  
   }

   /* Sort local list using built-in quick sort */
   qsort(sample, num, sizeof(int), Compare);
   
   
   /* Temporary storage used in merge-split */
   temp_B = (int*) malloc(num*sizeof(int));
   temp_C = (int*) malloc(num*sizeof(int));
   for (phase = 0; phase < p; phase++)
      Odd_even_iter(sample, temp_B, temp_C, num, phase, 
             even_partner, odd_partner, my_rank, p, comm);
   free(temp_B);
   free(temp_C);
   
   if(my_rank==0) leftSpliter=sample[0];
   for (phase = 0; phase < 2; phase++)
       GetSpliter(sample,num,&leftSpliter,my_rank,p,phase,even_partner,
       odd_partner,comm);
   
   int *counts, *displs,t;
   counts = malloc(p*sizeof(int));
   displs = malloc(p*sizeof(int));
   for(t=0;t<p;t++){
        counts[t]=1;
        displs[t]=t;     
   }
   MPI_Allgatherv(&leftSpliter, 1, MPI_DOUBLE, 
               spliters, counts, displs, MPI_DOUBLE, comm);
   if(my_rank==0) printf("Sample in each process:\n");
   Print_local_lists(sample, num, my_rank, p, comm);
   if(my_rank==0){
       int ii;
       for(ii=0;ii<p;ii++) printf("spliter in process %d: %lf \n", ii, spliters[ii]);
   }
   qsort(local_A, local_n, sizeof(int), Compare);
   MPI_Barrier(comm);
   Swap(local_A_p, local_n_p, my_rank, p, spliters, comm);
   free(sample);
   free(spliters);
}  /* Sort */

/*-------------------------------------------------------------------
 * Function:    Swap
 * Purpose:     Swap numbers according to leftSpliter and rightSpliter 
 * In args:     sample[], num, leftSpliter_p, leftSpliter_p, my_rank, p, phase,         
 *              comm
 * In/out args: leftSpliter, rightSpliter
 */
void Swap(int** local_A_p, int* local_n_p, int my_rank,int p, double spliters[], 
          MPI_Comm comm) {
   MPI_Status status;
   int partner;
   unsigned bitmask = (unsigned) (p/2);
   double spliter;
   int  *rec;
   int r,m,o;
   int ai, bi, ci;
   int tempSize;
   int* local_A=*local_A_p;   
   
   o=p;
   
   while (bitmask >0) {
        
        partner = my_rank ^ bitmask;  
        MPI_Send(local_A, *local_n_p, MPI_INT, partner, 0, comm);
        MPI_Probe(partner, 0, comm, &status);
        MPI_Get_count(&status, MPI_INT, &r);
        rec=(int*) malloc(r*sizeof(int));
        MPI_Recv(rec,r,MPI_INT,partner,0,comm,&status);
        ai = 0;
        bi = 0;
        ci = 0;
        m=my_rank/o;
        spliter=spliters[m*o+o/2];
        int* temp;
        temp=(int*) malloc((r+*local_n_p)*sizeof(int));
        tempSize=0;
        if(my_rank<partner){
            while (ci < r+*local_n_p) {
               if ((local_A[ai] <= rec[bi]||bi>=r) &&ai<*local_n_p) {
                  if(local_A[ai]<=spliter){
                    temp[tempSize] = local_A[ai];
                    tempSize++;
                   }
                  ci++; ai++;
               } else if((local_A[ai] > rec[bi]||ai>=*local_n_p)&&bi<r){
                  if(rec[bi]<=spliter){
                    temp[tempSize] = rec[bi];
                    tempSize++;
                   }
                  ci++; bi++;
               }
            }                    
        }else{
            while (ci < r+*local_n_p) {
               
               if ((local_A[ai] <= rec[bi]||bi>=r) &&ai<*local_n_p) {
                  if(local_A[ai]>spliter){
                    temp[tempSize] = local_A[ai];
                    tempSize++;
                   }
                  ci++; ai++;
               } else if((local_A[ai] > rec[bi]||ai>=*local_n_p)&&bi<r) {
                  if(rec[bi]>spliter){
                    temp[tempSize] = rec[bi];
                    tempSize++;
                   }
                  ci++; bi++;
               }
            }     
        }
        
        if(*local_n_p<tempSize){  
            local_A=realloc(local_A,*local_n_p*sizeof(int));      
        }
        if(tempSize>0){
            *local_n_p=tempSize;
            memcpy(local_A, temp, tempSize*sizeof(int));
        }else{
            *local_n_p=0;
        }
        bitmask >>= 1;
        o/=2;
        free(temp);
    }
    local_A_p=&local_A;  
    free(rec);
}  /* Swap */

/*-------------------------------------------------------------------
 * Function:    GetSpliter
 * Purpose:     Get leftSpliter and rightSpliter 
 * In args:     sample[], num, leftSpliter_p, leftSpliter_p, my_rank, p, phase,         
 *              comm
 * In/out args: leftSpliter, rightSpliter

 */
void GetSpliter(int sample[], int num, double* leftSpliter_p, int my_rank, int p, 
                int phase, int even_partner, int odd_partner, MPI_Comm comm) {
   MPI_Status status;
   int temp;

   if (phase % 2 == 0) {  /* Even phase, odd process <-> rank-1 */
      if (even_partner >= 0) {
         if(my_rank<even_partner){
              MPI_Send(&sample[num-1], 1, MPI_INT, even_partner, 0, comm); 
          }else{
              MPI_Recv(&temp, 1, MPI_INT, even_partner, 0, comm, &status);
              *leftSpliter_p=(sample[0]+temp)/2.0;
          }
      }
   } else { /* Odd phase, odd process <-> rank+1 */
      if (odd_partner >= 0) {
         if(my_rank<odd_partner){
              MPI_Send(&sample[num-1], 1, MPI_INT, odd_partner, 0, comm); 
          }else{
              MPI_Recv(&temp, 1, MPI_INT, odd_partner, 0, comm, &status);
              *leftSpliter_p=(sample[0]+temp)/2.0;
          }
      }
   }
}  /* GetSpliter */

/*-------------------------------------------------------------------
 * Function:    GetSample
 * Purpose:     Get sample numbers randomly for each process
 * In args:     local_A, local_n, p, global_s, my_rank
 * In/out args: sample
 */
void GetSample(int local_A[], int local_n, int sample[], int p, int global_s,
                int my_rank) {
    int num=global_s/p;
    int ran;
    int *get;
    int i=0;
    int j;
    int c;
    get = (int*) malloc((num)*sizeof(int));
    srandom(my_rank+1);
    do{
        ran = random() % (local_n);
        c=1;  
            
        for(j=0;j<i;j++){
            if(get[j]==ran){
               c=0;         
               break;
            }
        }
        if(c==1){
            sample[i]=local_A[ran];
            get[i]=ran;
            i++;
        }       
    }while(i!=num);
    free(get);
}  /* GetSample */

/*-------------------------------------------------------------------
 * Function:    Odd_even_iter
 * Purpose:     One iteration of Odd-even transposition sort
 * In args:     local_n, phase, my_rank, p, comm
 * In/out args: local_A
 * Scratch:     temp_B, temp_C
 */
void Odd_even_iter(int local_A[], int temp_B[], int temp_C[],
        int local_n, int phase, int even_partner, int odd_partner,
        int my_rank, int p, MPI_Comm comm) {
   MPI_Status status;

   if (phase % 2 == 0) {  /* Even phase, odd process <-> rank-1 */
      if (even_partner >= 0) {
         MPI_Sendrecv(local_A, local_n, MPI_INT, even_partner, 0, 
            temp_B, local_n, MPI_INT, even_partner, 0, comm,
            &status);
         if (my_rank % 2 != 0)
            Merge_split_high(local_A, temp_B, temp_C, local_n);
         else
            Merge_split_low(local_A, temp_B, temp_C, local_n);
      }
   } else { /* Odd phase, odd process <-> rank+1 */
      if (odd_partner >= 0) {
         MPI_Sendrecv(local_A, local_n, MPI_INT, odd_partner, 0, 
            temp_B, local_n, MPI_INT, odd_partner, 0, comm,
            &status);
         if (my_rank % 2 != 0)
            Merge_split_low(local_A, temp_B, temp_C, local_n);
         else
            Merge_split_high(local_A, temp_B, temp_C, local_n);
      }
   }
}  /* Odd_even_iter */

/*-------------------------------------------------------------------
 * Function:    Merge_split_low
 * Purpose:     Merge the smallest local_n elements in local_A 
 *              and temp_B into temp_C.  Then copy temp_C
 *              back into local_A.
 * In args:     local_n, temp_B
 * In/out args: local_A
 * Scratch:     temp_C
 */
void Merge_split_low(int local_A[], int temp_B[], int temp_C[], 
        int local_n) {
   int ai, bi, ci;
   
   ai = 0;
   bi = 0;
   ci = 0;
   while (ci < local_n) {
      if (local_A[ai] <= temp_B[bi]) {
         temp_C[ci] = local_A[ai];
         ci++; ai++;
      } else {
         temp_C[ci] = temp_B[bi];
         ci++; bi++;
      }
   }

   memcpy(local_A, temp_C, local_n*sizeof(int));
}  /* Merge_split_low */

/*-------------------------------------------------------------------
 * Function:    Merge_split_high
 * Purpose:     Merge the largest local_n elements in local_A 
 *              and temp_B into temp_C.  Then copy temp_C
 *              back into local_A.
 * In args:     local_n, temp_B
 * In/out args: local_A
 * Scratch:     temp_C
 */
void Merge_split_high(int local_A[], int temp_B[], int temp_C[], 
        int local_n) {
   int ai, bi, ci;
   
   ai = local_n-1;
   bi = local_n-1;
   ci = local_n-1;
   while (ci >= 0) {
      if (local_A[ai] >= temp_B[bi]) {
         temp_C[ci] = local_A[ai];
         ci--; ai--;
      } else {
         temp_C[ci] = temp_B[bi];
         ci--; bi--;
      }
   }

   memcpy(local_A, temp_C, local_n*sizeof(int));
}  /* Merge_split_low */


/*-------------------------------------------------------------------
 * Only called by process 0
 */
void Print_list(int local_A[], int local_n, int rank) {
   int i;
   printf("%d: ", rank);
   for (i = 0; i < local_n; i++)
      printf("%d ", local_A[i]);
   printf("\n");
}  /* Print_list */

/*-------------------------------------------------------------------
 * Function:   Print_local_lists
 * Purpose:    Print each process' current list contents
 * Input args: all
 * Notes:
 * 1.  Assumes all participating processes are contributing local_n 
 *     elements
 */
void Print_local_lists(int local_A[], int local_n, 
         int my_rank, int p, MPI_Comm comm) {
   int*       A;
   int        q;
   MPI_Status status;
    
    
   if (my_rank == 0) {
      A = (int*) malloc(local_n*sizeof(int));
      Print_list(local_A, local_n, my_rank);
      for (q = 1; q < p; q++) {
         MPI_Recv(A, local_n, MPI_INT, q, 0, comm, &status);
         Print_list(A, local_n, q);
      }
      free(A);
   } else {
      MPI_Send(local_A, local_n, MPI_INT, 0, 0, comm);
   }
}  /* Print_local_lists */
