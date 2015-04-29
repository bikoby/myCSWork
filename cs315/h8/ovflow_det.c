/* File:      ovflow_det.c
 * Purpose:   Read signed long ints, add them, and determine whether
 *            there's been overflow.
 *
 * Compile:   gcc -g -Wall -DALL_IN_C -o ovflow_det ovflow_det.c
 *      or:   gcc -g -Wall -o ovflow_det ovflow_det.c ovflow_det.s
 * Run:       ./ovflow_det
 *
 * Input:     A series of pairs of hexadecimal, long ints.  A pair
 *            of zeroes to quit.
 * Output:    The C sum of each input pair and a message stating
 *            whether the addition overflowed.
 *
 * Notes:
 *
 * 1.  The sequence of hex digits "fedcba9876543210" is printed
 *     so that the user can see how many digits he or she has
 *     entered.
 */
#include <stdio.h>

long Overflow(long m, long n);

int main(void) {
    long m, n, sum;
    
    printf("Enter two ints (both 0 to quit)\n");
    printf("fedcba9876543210 fedcba9876543210\n");
    scanf("%lx%lx", &m, &n);
    
    while (m != 0 || n != 0) {
        sum = m + n;
        printf("The C sum of 0x%lx and 0x%lx is 0x%lx\n", m, n, sum);
        if (Overflow(m,n))
            printf("The sum overflowed\n\n");
        else
            printf("The sum didn't overflow\n\n");
        
        printf("Enter two ints (both 0 to quit)\n");
        printf("fedcba9876543210 fedcba9876543210\n");
        scanf("%lx%lx", &m, &n);
    }
    
    return 0;
}  /* main */

#ifdef ALL_IN_C
/*-------------------------------------------------------------------
 * Function:    Overflow
 * Purpose:     Determine whether the sum of the two arguments has
 *              overflowed.
 * Input args:  m, n
 * Ret val:     0 if the sum doesn't overflow, nonzero otherwise
 *
 */
long Overflow(long m, long n) {
    if((m >= 0 && n <= 0)||(m <= 0 && n >= 0)){
        return 0;
    }else{
        long sum =  m + n;
        if(m>0){
            if(sum<0)
                return 1;
            else
                return 0;
        }else{
            if(sum>0)
                return 1;
            else
                return 0;
        }
        
    }
    return 0;
}  /* Overflow */
#endif