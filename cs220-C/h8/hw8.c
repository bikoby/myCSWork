/* File:    hw8.c
 *
 * Purpose:  Convert global row numbers to local row numbers and vice-versa
 *
 * Input:   p, total_r, temp_r, c, (local_r) 
 * Output:  The proceess that the temp_r on or the index of local_r in the whole
 *          array. 
 *
 * Compile: gcc -g -Wall -o hw8 hw8.c
 * Run:     ./hw8
 *
 * Note:  1.The total number of rows is evenly divisible by the number of 
 *          processes.
 *        2.The input format has to be "int int int g" or "int int int l int"
 */

#include <stdio.h>
#include <string.h>

int main(void){
    int p, total_r, temp_r, local_r;
    char c[1];
    printf("Please type in one of these format:\n");
    printf("'int int int g' or 'int int int l int'\n");
    int temp = scanf("%d %d %d %s", &p, &total_r, &temp_r, c);   
    if(strcmp("g",c)==0){
        printf("%d\n",temp_r/p);
    }else if(strcmp("l",c)==0){
        temp = scanf("%d", &local_r);
        printf("%d\n", temp_r*p+local_r-1);
    }

    return 0;
}