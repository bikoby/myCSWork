# File:           ovflow_det.s
# Purpose:        Determine whether the sum of the two arguments has
#                 overflowed.
# Functions:      Overflow
# C Prototype:    long Overflow(long n, long m)
#
#  in:             n, m
#  out:            0 if the sum doesn't overflow, nonzero otherwise

        .section .text

        .global Overflow

Overflow:
        push    %rbp            # The usual setup: push old base ptr
        mov     %rsp, %rbp      # Set new base ptr to stack ptr

        add     %rdi, %rsi
        jo      ovflow
        mov     $0, %rax
        jmp     done

ovflow:
        mov     $1, %rax

done:
        leave                   # The usual cleanup: set stack ptr
                                # to base ptr, pop old base ptr
        ret                     # Pop ret addr and jump to it
