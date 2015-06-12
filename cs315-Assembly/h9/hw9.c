/* File:     hw9.c
 *
 * Purpose:  determines whether the C-compiler on the lab machines generates code   
 *           that uses the "Mathematicians'" method or "Some Computer Scientists'"
 *           method for finding quotients and remainders
 *
 * Input:    none
 * Output:   none
 *
 * Compile:  gcc -g -Wall -o hw9 hw9.c
 * Usage:    hw9
 *
 * Conclusion: The result of -7 / 2 :
 *             Qutient: -3, Remainder: -1
 *             The result of 7 / -2 :
 *             Qutient: -3, Remainder: 1
 *             The result of -7 / -2 :
 *  		   Qutient: 3, Remainder: -1
 *             The remainder is not always greater than 0. 
 *			   So it is "Some Computer Scientists" method
 */

#include <stdio.h>
int main(int argc, char* argv[]) {
  
    printf("The result of -7 / 2 :\n");
    printf("Qutient: %d, Remainder: %d\n", -7/2, -7%2);
    printf("The result of 7 / -2 :\n");
    printf("Qutient: %d, Remainder: %d\n", 7/-2, 7%-2);
    printf("The result of -7 / -2 :\n");
    printf("Qutient: %d, Remainder: %d\n", -7/-2, -7%-2);
   return 0;
}  /* main */


