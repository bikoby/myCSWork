/* File:     p2.c
 *
 * Author:   Jinpeng Bi (Koby)
 *
 * Purpose:  Implement an binary-tree with ops insert (in a right place),
 *           print, print_sort(print from the smallest to the greatest),
 *           member, free_tree, delete,.
 * 
 * Input:    Single character lower case letters to indicate operators, 
 *           followed by arguments needed by operators.
 * Output:   Results of operations.
 *
 * Compile:  gcc -g -Wall -o p2 p2.c
 * Run:      ./p2
 *
 * Notes:
 *    1.  Repeated values are not allowed in the binary-tree
 *    2.  Program assumes an int will be entered when prompted
 *        for one.
 */
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

const int MAX_INDENT = 80;

struct tree_node_s {
   int key;
   struct tree_node_s* lc;
   struct tree_node_s* rc;
}; 

typedef struct tree_node_s tree_node_t;
void Print_tree_wrapper(tree_node_t* root_p);
void Print_tree(tree_node_t* curr_p, char indent[]);
void Print_sort(tree_node_t* curr_p);
int Member(tree_node_t* curr_p, int val);
tree_node_t* Insert(tree_node_t* curr_p, int val);
tree_node_t* Free_tree(tree_node_t* curr_p);
tree_node_t* Delete(tree_node_t* curr_p, int val);
tree_node_t* Delete_successor(tree_node_t* curr_p);

char Get_command(void);
int  Get_value(void);

/*-----------------------------------------------------------------*/
int main(void) {
   char command;
   int  value;
   tree_node_t* root_p = NULL;  
      /* start with empty list */

   command = Get_command();
   while (command != 'q' && command != 'Q') {
      switch (command) {
         case 'i': 
         case 'I': 
            value = Get_value();    
            root_p = Insert(root_p, value);
            break;
         case 'p':
         case 'P':
            Print_tree_wrapper(root_p);
            break;
         case 's':
         case 'S':
            Print_sort(root_p);
            printf("\n");
            break;
          case 'm':
          case 'M':
            value = Get_value();
            if(Member(root_p,value))
                printf("The value %d is in the tree!\n", value);
            else
                printf("The value %d is not in the tree!\n",value);
            break;
          case 'f':
          case 'F':
            root_p=Free_tree(root_p);
            break;
           case 'd':
           case 'D':
            value = Get_value();
            root_p=Delete(root_p,value);
            break;
         default:
            printf("There is no %c command\n", command);
            printf("Please try again\n");
      }
      command = Get_command();
   }
   return 0;
}  /* main */


/*---------------------------------------------------------------------
 * Function:  Print_tree_wrapper
 * Purpose:   Set up for call to Print_tree function by creating
 *              storage for a string that will be used to control
 *              indentation.
 * In arg:    root_p:  pointer to the root of the tree.
 */
void Print_tree_wrapper(tree_node_t* root_p) {
   char indent[MAX_INDENT];

   indent[0] = '\0';
   Print_tree(root_p, indent);
}  /* Print_tree_wrapper */


/*---------------------------------------------------------------------
 * Function:    Print_tree
 * Purpose:     Print the keys in the tree showing the structure of
 *                 the tree.  (Preorder traversal)
 * In args:     curr_p:  pointer to the current node
 * In/out arg:  indent:  array of char specifying the indentation
 *              for the node.
 *
 * Note:
 * Each new level of the tree is indented 3 spaces to the right
 *    on the screen
 */
void Print_tree(tree_node_t* curr_p, char indent[]) {

   if (curr_p != NULL) {
      printf("%s %d\n", indent, curr_p->key);
      strcat(indent, "   ");
      Print_tree(curr_p->lc, indent);
      Print_tree(curr_p->rc, indent); 
      indent[strlen(indent) - 3] = '\0';
   }
}  /* Print_tree */


/*---------------------------------------------------------------------
 * Function:    Print_sort
 * Purpose:     Print the keys in sequence
 * In args:     curr_p:  pointer to the current node
 *
 * Note:
 *  Sequencs is from the smallest to the greatest.
 */
void Print_sort(tree_node_t* curr_p) {
   if (curr_p != NULL) {
      Print_sort(curr_p->lc);
      printf("%d ",curr_p->key);
      Print_sort(curr_p->rc); 
   }
}  /* Print_sort */


/*-----------------------------------------------------------------
 * Function:   Insert
 * Purpose:    Insert val at right place
 * Input args: curr_p:  pointer to the current node
 *             val:  new value to be inserted
 * Return val: Pointer to root node
 */
tree_node_t* Insert(tree_node_t* curr_p, int val) {
    if(curr_p==NULL){
        tree_node_t* temp_p;
        temp_p = malloc(sizeof(tree_node_t*));
        temp_p->key=val;
        temp_p->lc=NULL;
        temp_p->rc=NULL;
        curr_p=temp_p;
    }else if(val>curr_p->key){
        curr_p->rc=Insert(curr_p->rc,val);
    }else if(val<curr_p->key){
        curr_p->lc=Insert(curr_p->lc,val);
    }else{
        printf("the value:%d is already in the tree\n",val);
    }       
    return curr_p;
}   /* Insert */


/*-----------------------------------------------------------------
 * Function:   Delete
 * Purpose:    Delete the value given the user, if it is NULL return massage to 
 *             the user; if it has no child, just delete it and return NULL to 
 *             its parent; if it has one child, delete it and return its child 
 *             to its parent; if it has two children, use the smallest child in 
 *             it right child to replace it and delete it and link its children 
 *             to its parent.
 * Input args: curr_p:  pointer to the current node
 *             val:  new value to be deleted
 * Return val: Pointer to root node
 */
tree_node_t* Delete(tree_node_t* curr_p, int val) {
   int find=0;
   if(curr_p!=NULL){
        if(val==curr_p->key){
            if(curr_p->lc==NULL && curr_p->rc==NULL){
                find=1;
                free(curr_p);
                return NULL;  
             }else if(curr_p->lc==NULL || curr_p->rc==NULL){
                tree_node_t* temp;
                if(curr_p->lc==NULL)
                    temp=curr_p->rc;
                else if(curr_p->rc==NULL)
                    temp=curr_p->lc;
                free(curr_p);
                return temp;
             }else{
                curr_p=Delete_successor(curr_p);
                return curr_p;
             }
        }else if(val>curr_p->key){
            curr_p->rc=Delete(curr_p->rc,val);
            return curr_p;
        }else if(val<curr_p->key){
            curr_p->lc=Delete(curr_p->lc,val);
            return curr_p;
        }
    }
    if(find==0){
        printf("There is no %d in this tree\n", val);
    }
    return curr_p;
}   /* Delete */


/*-----------------------------------------------------------------
 * Function:   Delete_successor
 * Purpose:    Find the smallest node in the right sub tree and repalce the key
 *             of the root key and free the node and return it to its parent
 * Input args: curr_p:  pointer to the current node
 * Return val: Pointer to root node
 */
tree_node_t* Delete_successor(tree_node_t* curr_p) {
    tree_node_t* Min_p=curr_p->rc;
    int Min;
    while(Min_p->lc!=NULL){
        Min_p=Min_p->lc;
    }
    Min=Min_p->key;
    curr_p=Delete(curr_p,Min);
    curr_p->key=Min;
    return curr_p;
}   /* Delete_successor */


/*---------------------------------------------------------------------
 * Function:    Member
 * Purpose:     check whether the value is in the tree
 * In args:     curr_p:  pointer to the current node
 *              val: the value that needs to be checked
 */
int Member(tree_node_t* curr_p, int val) {
    while(curr_p!=NULL){
        if(val>curr_p->key)
            return Member(curr_p->rc,val);
        else if(val<curr_p->key){
            return Member(curr_p->lc,val);
        }
        else if(val==curr_p->key){
            return 1;
        }
    }
    return 0;
}  /* Member */


/*-----------------------------------------------------------------
 * Function:   Free_tree
 * Purpose:    Free all nodes in tree and return NULL to root_p
 * Input args: curr_p:  pointer to the current node
 *            
 * Return val: Pointer to root node which points to NULL
 */
tree_node_t* Free_tree(tree_node_t* curr_p) {
   while(curr_p!=NULL){
        curr_p->lc=Free_tree(curr_p->lc);
        curr_p->rc=Free_tree(curr_p->rc);
        free(curr_p);
        return NULL;
   }
   return curr_p;
}   /* Free_tree */


/*-----------------------------------------------------------------
 * Function:      Get_command
 * Purpose:       Get a single character command from stdin
 * Return value:  the first non-whitespace character from stdin
 */
char Get_command(void) {
   char c;

   printf("Please enter a command (i, p, s, m, d, f, q):  ");
   /* Put the space before the %c so scanf will skip white space */
   scanf(" %c", &c);
   return c;
}  /* Get_command */

/*-----------------------------------------------------------------
 * Function:   Get_value
 * Purpose:    Get an int from stdin
 * Return value:  the next int in stdin
 * Note:       Behavior unpredictable if an int isn't entered
 */
int  Get_value(void) {
   int val;

   printf("Please enter a value:  ");
   scanf("%d", &val);
   return val;
}  /* Get_value */