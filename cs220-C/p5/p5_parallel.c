/* File:     p5_parallel.c 
 * Purpose:  Get the longest common Subsequence.
 * Author:   Jinpeng Bi(Koby)
 * Input:    m: size of the A sequence
 *           A: numbers in A
 *           n: size of the B sequence
 *           B: numbers in B
 *
 * Output:   the longest common Subsequence
 *
 * Compile:  gcc -g -Wall -o p5_parallel p5_parallel.c
 * Run:      ./p5_parallel <numberOfThreads>
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>

typedef struct{
        int* list;
        int len;
        int li;
        int lj;
}seq_t;

int* A;
int* B;
seq_t* L;
int m,n;
int thread_count;
int count;
sem_t* barrier_sems;
sem_t count_sem;



void Read_vector(char* prompt, int v[], int n);
void Print_vector(char* title, int y[], int m);
void *Get_Max(void* rank);
void Compute_L(int i,int j);
int Compute_row(int diag,int diag_entry);
int Compute_col(int diag,int diag_entry);
void Usage(char* prog_name);
int Get_diag_len(int diag);
    
int main(int argc, char* argv[]) {
    int  min;
    int  i;
    long thread;
    pthread_t* thread_handles; 
    
    if (argc != 2) Usage(argv[0]);
    thread_count = strtol(argv[1], NULL, 10);
    
    thread_handles = malloc (thread_count*sizeof(pthread_t));
    
    
    printf("Enter the length of A sequence\n");
    scanf("%d", &m);
    A = malloc(m*sizeof(int));
    Read_vector("the A sequence", A, m);
    
    printf("Enter the length of B sequence\n");
    scanf("%d", &n);   
    B = malloc(n*sizeof(int));
    Read_vector("the B sequence", B, n);
    
    barrier_sems=malloc((m+n-1)*sizeof(sem_t));
    for (i = 0; i < m+n-1; i++)
        sem_init(&barrier_sems[i], 0, 0);  // Init barrier_sems to 0
    sem_init(&count_sem, 0, 1);  // Initialize count_sem to 1
    

    Print_vector("the A sequence: ",A,m);
    Print_vector("the B sequence: ",B,n);
    
    
    if(m>n){
        min=n;
    }else{
        min=m;
    }
    
    L=malloc(m*n*sizeof(seq_t));
    for(i=0;i<m*n;i++){
        L[i].list=malloc(min*sizeof(int));
        L[i].len=0; 
        L[i].li=-1;
        L[i].lj=-1;
    }
    
    for (thread = 0; thread < thread_count; thread++)
        pthread_create(&thread_handles[thread], (pthread_attr_t*) NULL,
          Get_Max, (void*) thread);
    for (thread = 0; thread < thread_count; thread++) 
        pthread_join(thread_handles[thread], NULL); 
        
        
    seq_t max=L[m*n-1];
    Print_vector("the final sequence: ",max.list,max.len);

    free(L);
    free(A);
    free(B);
    sem_destroy(&count_sem);
    for (i = 0; i < m+n-1; i++)
       sem_destroy(&barrier_sems[i]);
    free(thread_handles);
    return 0;
}  /* main */
  
/*---------------------------------------------------------------
 * Function:  Get_Max
 * Purpose:   Get the Longest Common Subsequence
 * In arg:    A list of seq_t, two sizes m,n of sequences
 * Out arg:   A list of seq_ts which are filled in of common 
 *            subsequence depending on the index.
 */
void *Get_Max(void* rank) {
    long my_rank = (long) rank;
    int diag,diag_len,diag_entry,row,col;
    int diag_count = m + n - 1;
    int j;
    for (diag = 0; diag < diag_count; diag++) {
        diag_len = Get_diag_len(diag);
        for (diag_entry = my_rank; diag_entry < diag_len;
            diag_entry += thread_count) {
           //printf("rank %ld:%d,%d\n",my_rank,
             //       diag,diag_len);
            row = Compute_row(diag, diag_entry);
            col = Compute_col(diag, diag_entry);
            //printf("rank %ld:r %d,c %d,l %d, d %d\n",my_rank,
                 //   row,col,diag_len,diag);
            //printf("********************\n");
            Compute_L(row, col);
        }
        //Barrier
        sem_wait(&count_sem);
        if (count == thread_count - 1) {
           count = 0;
           sem_post(&count_sem);
           for (j = 0; j < thread_count-1; j++)
              sem_post(&barrier_sems[diag]);
        } else {
           count++;
           sem_post(&count_sem);
           sem_wait(&barrier_sems[diag]);
        }
    }
    return NULL;
}  /* Get_Max */

/*---------------------------------------------------------------
 * Function:  Compute_L
 * Purpose:   Get the row number depending the number of diagnal
 *            and the diag_entry
 * In arg:    The nubmer of the diagnal and diag_entry 
 * Out arg:   Row number
 */
void Compute_L(int i,int j) {
        int lj,li,len1,len2;
        int index=i*n+j;
        if (A[i] == B[j]){
            /* L[i,j] = L(A_i, B_j) */
            //L[i,j] = L[i-1,j-1] + A[i];
            if(i-1>=0&&j-1>=0){
                L[index]=L[index-n-1];
                lj=L[index-n-1].lj;
                li=L[index-n-1].lj;
            }else{
                lj=-1;
                li=-1;
            }
            if(j!=lj||i!=li){
                L[index].list[L[index].len++]=A[i];
                L[index].li=i;
                L[index].lj=j;
             }
        }else {
            //len1 = Length(L[i,j-1]);
            if(j-1<0||index-1<0){
                len1=0;
            }else{
                len1=L[index-1].len;
            }
            //len2 = Length(L[i-1,j]);
            if(i-1<0||index-n<0){
                len2=0;
            }else{
                len2=L[index-n].len;
            }
            if (len1 > len2){
                //L[i,j] = L[i,j-1];
                L[index]=L[index-1];
            }else{
                //L[i,j] = L[i-1,j];
                if(len2>0)
                    L[index]=L[index-n];
            }
          }
}  /* Compute_L */

/*---------------------------------------------------------------
 * Function:  Compute_row
 * Purpose:   Get the row number depending the number of diagnal
 *            and the diag_entry
 * In arg:    The nubmer of the diagnal and diag_entry 
 * Out arg:   Row number
 */
int Compute_row(int diag,int diag_entry) {
    if(diag<m){
        return diag-diag_entry;
    }else{
        return m-1-diag_entry;
    }
    
}  /* Compute_row */

/*---------------------------------------------------------------
 * Function:  Compute_col
 * Purpose:   Get the row number depending the number of diagnal
 *            and the diag_entry
 * In arg:    The nubmer of the diagnal and diag_entry 
 * Out arg:   Row number
 */
int Compute_col(int diag,int diag_entry) {
    if(diag<m){
        return diag_entry;
    }else{
        return diag-m+1+diag_entry;
    }
}  /* Compute_col */

/*---------------------------------------------------------------
 * Function:  Get_diag_len
 * Purpose:   Get the length depending the number of diagnal
 * In arg:    The nubmer of the diagnal
 * Out arg:   The length of the diagnal
 */
int Get_diag_len(int diag) {
    if(diag<n){
        return diag+1;
    }else{
        return n+m-1-diag;
    }
    
}  /* Get_diag_len */


/*---------------------------------------------------------------
 * Function:  Read_vector
 * Purpose:   Read a vector from stdin
 * In arg:    n:  number of components
 * Out arg:   v:  the vector
 */
void Read_vector(
         char*  prompt  /* in  */,
         int  v[]     /* out */,
         int    n       /* in  */) {
    int i;

    printf("Enter %s\n", prompt);
    for (i = 0; i < n; i++)
        scanf("%d", &v[i]);
}  /* Read_vector */


/*------------------------------------------------------------------
 * Function:    Print_vector
 * Purpose:     Print a vector
 * In args:     title, y, m
 */
void Print_vector(char* title, int y[], int m) {
   int   i;

   printf("%s\n", title);
   for (i = 0; i < m; i++)
      printf("%4d ", y[i]);
   printf("\n");
}  /* Print_vector */

/*--------------------------------------------------------------------
 * Function:    Usage
 * Purpose:     Print command line for function and terminate
 * In arg:      prog_name
 */
void Usage(char* prog_name) {

   fprintf(stderr, "usage: %s <number of threads>\n", prog_name);
   exit(0);
}  /* Usage */