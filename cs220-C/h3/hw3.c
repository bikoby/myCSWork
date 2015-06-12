/* File:     search_word.c
 * Author:   Jinpeng Bi (Koby)
 * Purpose:  search input text for the first occurrence of a word.  
 *           This version gets the word from the command line and
 *           the input text from stdin.
 *
 * Compile:  gcc -g -Wall -o hw3 hw3.c -std=c99
 *
 * Usage:    ./search_word <word to be searched for> <word to be searched for> 
 *              (or more words) 
 * Input:    text
 * Output:   Message if search word not found
 *           Number of first word of input that matched if
 *              found
 * Notes:
 * 1.  Words searched should be separated by space
 * 2.  Words in input text consist of strings separated by white space
 * 3.  No error check for strings exceeding MAX_STRING_LEN
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>  /* For exit */

const int MAX_STRING_LEN = 100;

void Usage(char prog_name[]);

int main(int argc, char* argv[]) {
   char search_for[MAX_STRING_LEN][argc];
   char current_word[MAX_STRING_LEN];
   int  scanf_rv=0;
   int  word_count = 0;
   int  number=0;  
   int  index[argc];
   
   /* Check that the user-specified words are on the command line */
   if (argc < 2) Usage(argv[0]);
   for(int i=1;i<argc;i++){
        index[i]=0;
        strcpy(search_for[i], argv[i]);
   }
   printf("Enter the text to be searched\n");
   /*compare each word with argv*/
   while ( scanf_rv != EOF && number!=(argc-1) )  {
      scanf_rv = scanf("%s", current_word);
      word_count++; 
      for(int i=1;i<argc;i++){
        if(strcmp(current_word,search_for[i])==0){
            number++;
            index[i]=word_count;
            strcpy(search_for[i], "");
        }
      }
   }
   for(int i=0;i<argc;i++){
    if(index[i]>0){
        printf("\n%s was word number %d\n",
         argv[i], index[i]);
    }else if(index[i]==0){
        printf("\n%s was not found in the %d words of input\n",
           argv[i], word_count);
      }
    }
    
   return 0;
}  /* main */

/* If user-specified words are not on the command line, 
 * print a message and quit
 */
void Usage(char prog_name[]) {
   fprintf(stderr, "usage: %s <string to search for>\n",
      prog_name);
   exit(0);
}  /* Usage */

