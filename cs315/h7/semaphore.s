# File:           semaphore.s
# Purpose:        Implement a semaphore using x86 assembly
# Functions:      sem_post and sem_wait

#######################################################################
# Function:       sem_wait
# Purpose:        Wait for semaphore to be unlocked
# C Prototype:    void sem_wait(sem_t* semaphore)
#
#  in/out:        semaphore

        .section .text

        .global sem_wait
sem_wait:
        push    %rbp            # The usual setup: push old base ptr
        mov     %rsp, %rbp      # Set new base ptr to stack ptr


busy_wait:
        mov     $0, %r8         # Set r8 to 0 (locked)
        mov     %rdi, %r9
        xchg    %r8, 0(%r9)       # Atomically exchange contents of r9
                                #   (in memory) and contents of r8
        cmp     $1, %r8         # If we succeeded, r8 = 0
        jne     busy_wait       # If we failed, r8 = 0.  Try again

        leave                   # The usual cleanup: set stack ptr
                                # to base ptr, pop old base ptr
        ret                     # Pop ret addr and jump to it


#######################################################################
# Function:       sem_post
# Purpose:        Unlock the semaphores
# C Prototype:    void sem_post(sem_t* semaphore)
#
# Args:           semaphore
# in/out:         semaphore
        .global sem_post
sem_post:
        push    %rbp            # The usual setup: push old base ptr
        mov     %rsp, %rbp      # Set new base ptr to stack ptr

        mov     $1, %r8         # Set r8 to 1 (unlocked)]
        mov     %rdi, %r9
        xchg    %r8, 0(%r9)       # Atomically exchange contents of rdi
                                #   and contents of r8, setting semaphore
                                #   to 1 (unlocked)

        leave                   # The usual cleanup: set stack ptr
                                # to base ptr, pop old base ptr
        ret                     # Pop ret addr and jump to it
