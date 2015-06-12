#include <stdio.h>

void ArrayReverse(int array[],int array_s);

int main(void){
    int array_size=0;
    //get the array size from the user
    while(array_size<=0){
        printf("please input the size of an array(n>0):");
        scanf("%d",&array_size);
    }
    //create an array in the array size
    int array[array_size];
    printf("please input %d integers:",array_size);
    for (int i=0;i<array_size;i++){
        scanf("%d",&array[i]);
    }
    //create an array to get the value in the old array
    int newArray[array_size];
    for (int i=0;i<array_size;i++){
        newArray[i]=array[i];
    }
    //using ArrayReverse to reverse the new array
    ArrayReverse(newArray,array_size);
    //print out the old array
    printf("old array:");
    for (int i=0;i<array_size;i++){
        printf("%d ", array[i]);
    }
    printf(" \n");
    //print out the new array
    printf("new reversed array:");
    for (int i=0;i<array_size;i++){
        printf("%d ", newArray[i]);
    }   
    printf(" \n");
}

void ArrayReverse(int array[],int array_size){
    int temp;
    //swith values in array
    for(int i=0;i<array_size/2;i++){
        temp=array[i];
        array[i]=array[array_size-1-i];
        array[array_size-1-i]=temp;
    }
}
