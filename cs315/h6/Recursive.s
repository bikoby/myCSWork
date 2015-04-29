# Function:     Recursive
# Purpose:  Print values of the function R(i,j) defined by
#           the formulas
#         
#              R(i,j) = i - j, if i or j is < 0
#              R(i,j) = R(i-1, j) + R(i, j-1), otherwise
#
# C Prototype:  long Recursive(long i, long j)
# Args:         i = rdi, j = rsi
# Return val:   Recursive(i,j) = rax

        .section .text
        .global Recursive

Recursive:
        push %rbp
        mov  %rsp, %rbp
        sub  $24, %rsp          # We may need to store n and a return 
                                #    val from a recursive call

        # Is i = 0?
        cmp  $0, %rdi           # Is n = rdi == 0?  Note that the immediate
                                #    must come first here
        jne  i_gt_0             # Look at the flags register to see whether
                                #    the previous comparison result is != 0
        mov  %rdi, %rax         # rax = i
        sub  %rsi, %rax         # rax = i-j
        jmp  done               # Go to done

i_gt_0:
        # Is j = 0?
        cmp  $0, %rsi           # Is j = rsi == 0?
        jne  j_gt_0             # Look at the flags register to see whether
                                #    the previous comparison result is != 1
        mov  %rdi, %rax         # rax = i
        sub  %rsi, %rax         # rax = i-j
        jmp  done               # Go to done
        
j_gt_0:
        # i>0 or j>0
        mov  %rdi, 8(%rsp)      # Save i = rdi on the stack
        mov  %rsi, 16(%rsp)     # Save j = rsi on the stack  
        sub  $1, %rdi           # i = i-1
        call Recursive
        mov  %rax, 0(%rsp)      # Save Recursive(i-1,j) on the stack
        mov  8(%rsp), %rdi      # Retrieve i
        mov  16(%rsp), %rsi     # Retrieve j
        sub  $1, %rsi           # j = j-1
        call Recursive
        add  0(%rsp), %rax      # return Recursive(i-1,j) + Recursive(i,j-1)

done:
        leave                   # Assigns rbp to rsp:  no need to
                                #    add 16 to rsp
        ret
