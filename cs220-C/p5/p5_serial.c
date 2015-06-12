/* File:     p5_serial.c
 * Purpose:  Get the longest common Subsequence.
 * Author:   Jinpeng Bi(Koby)
 * Input:    m: size of the A sequence
 *           A: numbers in A
 *           n: size of the B sequence
 *           B: numbers in B
 *
 * Output:   the longest common Subsequence
 *
 * Compile:  gcc -g -Wall -o p5_serial p5_serial.c
 * Run:      ./p5_serial
 *
 */
#include <stdio.h>
#include <stdlib.h>


typedef struct{
        int* list;
        int len;
        int li;
        int lj;
}seq_t;

int* A;
int* B;

void Read_vector(char* prompt, int v[], int n);
void Print_vector(char* title, int y[], int m);
void Get_Max(seq_t* L,int m,int n);

    
int main(void) {
    int min;
    seq_t* L;
    int m,n,i;
    
    printf("Enter the length of A sequence\n");
    scanf("%d", &m);
    A = malloc(m*sizeof(int));
    Read_vector("the A sequence", A, m);
    
    printf("Enter the length of B sequence\n");
    scanf("%d", &n);   
    B = malloc(n*sizeof(int));
    Read_vector("the B sequence", B, n);


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
    
    Get_Max(L,m,n);
    seq_t max=L[m*n-1];
    Print_vector("the final sequence: ",max.list,max.len);
    for(i=0;i<m*n;i++){
        free(L[i].list);
    }
    free(L);
    free(A);
    free(B);
    return 0;
}  /* main */
   
/*---------------------------------------------------------------
 * Function:  Get_Max
 * Purpose:   Get the Longest Common Subsequence
 * In arg:    A list of seq_t, two sizes m,n of sequences
 * Out arg:   A list of seq_ts which are filled in of common
 *            subsequence depending on the index.
 */
void Get_Max(seq_t* L, int m, int n) {

        /* Careful of i-1, j-1 < 0! */
    if(m>0&&n>0){
        int i,j;
        int index,lj,li;
        int len1,len2;
        for (i = 0; i < m; i++){
            for (j = 0; j < n; j++){
                index=i*n+j;
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
             }
        }
    }
}  /* Get_Max */


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
