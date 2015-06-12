# Function:     Bubble_sort
# Purpose:  Sort list using bubble sort
#         
#
# C Prototype:  void Bubble_sort(long a[], long n)
# Args:         a[] = rdi, n = rsi
        .section .text
        .global Bubble_sort
       

Bubble_sort:
        push %rbp
        mov  %rsp, %rbp      
        
        mov  %rdi, %r10        # A = r10 
        mov  %rsi, %r11        # lenth = r11 = n
        

o_loop: cmp  $2,%r11           # compare lenth and 2
        mov  $0,%r8            # i = r8 = 0
        jl   done

loop:   mov  %r11,%r9
        sub  $1,%r9            # r9 = lenth-1
        cmp  %r9,%r8          
        jge  e_loop
        mov  0(%r10,%r8,8), %rdi  # rdi = a[i] 
        mov  8(%r10,%r8,8), %rsi  # rsi = a[i+1]
        cmp  %rsi, %rdi
        jg   swap               # if rdi>rsi, j to swap
        add  $1, %r8
        jmp  loop  

e_loop: sub  $1, %r11
        jmp  o_loop        

swap:   mov  %rdi, 8(%r10,%r8,8) # a[i+1] = rdi = a[i]
        mov  %rsi, 0(%r10,%r8,8) # a[i] = rsi = a[i+1]
        add  $1, %r8   
        jmp  loop


done:
        leave                   # Assigns rbp to rsp:  no need to
                                #    add 16 to rsp
        ret
