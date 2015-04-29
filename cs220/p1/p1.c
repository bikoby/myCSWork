/* File:     p1.c
 * Author:   Jinpeng Bi (Koby)
 * Purpose:  using the Bisection Method to caculate the solution of f(x)=0 
 *            between a and b and check whether the function converged or not
 * Compile:  gcc -g -Wall -o p1 p1.c -lm
 * Run:      ./p1
 *
 * Input:    three doubles: a , b and tolerance, one int:max iterations 
 *           
 * Output:   whether the function f(x) converged or not, the iterations, 
 *           aproximate solusion of f(x), and the aproximate x.
 * Note:     the inputs have to be numbers.
 *           f(x)=x^2-1
 *           printResult(double middleNumber,int iters) is used for print out 
 *           the output
 */

#include <stdio.h>
#include <math.h>

double f(double x);
void printResult(double middleNumber,int iters);

int main(void){
    double a,b,tol;
    int max_iters;
    //get the values a,b,tolerance and max iterations
    printf("please input a:");
    scanf("%lf",&a);
    printf("please input b:");
    scanf("%lf",&b);
    printf("please input tolerance:");
    scanf("%lf",&tol);
    printf("please input max iterations:");
    scanf("%d",&max_iters);
    //check whether there is solution between a and b
    if (f(a)*f(b)>0){
        printf("There is no solution for f(x)=0 between a and b");
        return 0;
    //check whether a, b is the solution of f(x)=0
    }else if(f(a)==0){
        printResult(a,0);
        return 0;
    }else if(f(b)==0){
        printResult(b,0);
        return 0;
    }else{
        double middleNumber;
        int iters=0;
        //when the value of a-b is less than the tolerance or the iterations 
        //larger than the max iterations, end the while loop 
        while(fabs(a-b)>tol&&iters<max_iters){
            iters++;
            middleNumber=(a+b)/2;
            //check whether the middle number is the solution of f(x)=0
            if(f(middleNumber)==0){
                printf("The method converged.\n");
                printResult(middleNumber,iters);
                return 0;
            }
            //check which part does the solution belong
            if(f(middleNumber)*f(a)<0){
                b=middleNumber;
            }else if(f(middleNumber)*f(b)<0){
                a=middleNumber;
            }
        }
        //check whether the function converged or not
        if(fabs(a-b)<=tol){
            printf("The method converged.\n");
            printResult(middleNumber,iters);
            return 0;
        }else if(iters>=max_iters){
            printf("The method did not converge.\n");
            printResult(middleNumber,iters);
        }
                   
    }
    return 0;
}

double f(double x){
    return pow(x,2)-2;
}

void printResult(double middleNumber,int iters){
    printf("The aproximate solusion of f(x)=0 is x=%f.\n",middleNumber);
    printf("The function value of %f is %f.\n",middleNumber,
           f(middleNumber));
    printf("The iterations:%d\n",iters);
            
}
