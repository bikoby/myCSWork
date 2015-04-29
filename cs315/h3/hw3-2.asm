# File:      hw3-2.s
# Purpose:   Use count sort to sort a user-input list of ints
#
# Input:     Number of ints in list (n)
#            Elements of list (one element per line)
# Output:    Input list
# 
# Note:      list is statically allocated on the stack
#
	.text
	.globl	main
main:
	addi	$sp, $sp, -412          # Make additional stack space.
                                        #   3 words for $ra, $s0, $s1
                                        #   100 words for list
        sw      $ra, 408($sp)           # Put contents of $ra on stack
        sw      $s0, 404($sp)           # Put $s0 on stack
        sw      $s1, 400($sp)           # Put $s1 on stack
        move    $s0, $sp                # $s0 = stores start address of list
                                        #     = $sp

	# Ask the OS to read a number and put it in $s1 = n
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for input.
	move    $s1, $v0                # Put the input value (n) in a safe
                                        #    place

        # Now read in the list
        move    $a0, $s0                # First arg is address of list
        move    $a1, $s1                # Second arg is n
        jal     rd_lst

	# count sort
	move    $a0, $s0                # First arg is address of list
        move    $a1, $s1                # Second arg is n
        jal	c_sort

        # Now print the list
        move    $a0, $s0                # First arg is list
        move    $a1, $s1                # Second arg is n
        jal     pr_lst

        # Prepare for return
        lw      $ra, 408($sp)           # Retrieve return address
        lw      $s0, 404($sp)           # Retrieve $s0
        lw      $s1, 400($sp)           # Retrieve $s1
	addi	$sp, $sp, 412           # Make additional stack space.
#       jr      $ra                     # return
        li      $v0, 10
        syscall


        ###############################################################
        # Read_list function
        #    $a0 is the address of the beginning of list (In/out)
        #    $a1 is n
        #
        #    Note that the address in $a0 isn't modified.  The 
        #    contents of memory starting in the address in $a0
        #    are modified.  
rd_lst: 
        # Setup
        addi    $sp, $sp, -4            # Make space for return address
        sw      $ra, 0($sp)             # Save return address

        # Main for loop
        move    $t0, $zero              # $t0 = i = 0
rd_tst: bge     $t0, $a1, rddone        # If  i = $t0 >= $a1 = n 
                                        #    branch out of loop.
                                        #    Otherwise continue.
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
        sll     $t1, $t0, 2             # Words are 4 bytes:  use 4*i, not i
        add     $t1, $a0, $t1           # $t1 = list + 4*i
	sw      $v0, 0($t1)             # Put the input value in $v0 in
                                        #    list[i]
        addi    $t0, $t0, 1             # i++
        j       rd_tst                  # Go to the loop test
        
        # Prepare for return
rddone: lw      $ra, 0($sp)             # retrieve return address
        addi    $sp, $sp 4              # adjust stack pointer
        jr      $ra                     # return


        ###############################################################
        # Print_list function
        #    $a0 is the address of the beginning of list (In)
        #    $a1 is n
pr_lst: 
        # Setup
        addi    $sp, $sp, -4            # Make space for return address
        sw      $ra, 0($sp)             # Save return address

        # Main for loop
        move    $t2, $a0                # Need $a0 for syscall:  so
                                        #    copy list address to t2
        move    $t0, $zero              # $t0 = i = 0
pr_tst: bge     $t0, $a1, prdone        # If  i = $t0 >= $a1 = n 
                                        #    branch out of loop.
                                        #    Otherwise continue.
        sll     $t1, $t0, 2             # Words are 4 bytes:  use 4*i, not i
        add     $t1, $t2, $t1           # $t1 = list + 4*i
	lw      $a0, 0($t1)             # Put the value to print in $a0
	li	$v0, 1			# Code for print int.
	syscall

        # Print a space 
        la      $a0, space              # 
        li      $v0, 4                  # Code for print string
        syscall

        addi    $t0, $t0, 1             # i++
        j       pr_tst                  # Go to the loop test
        
        # print a newline
prdone: 
        la      $a0, newln
        li      $v0, 4                  # code for print string
        syscall

        # Prepare for return
        lw      $ra, 0($sp)             # retrieve return address
        addi    $sp, $sp 4              # adjust stack pointer
        jr      $ra                     # return

c_sort:	addi	$sp, $sp, -412         
        sw      $ra, 408($sp)          
        sw      $t2, 404($sp)		# loc
        sw      $t1, 400($sp)           # i
        move    $t0, $sp		# new list
        
        li 	$t1, 0
        move	$t3, $a1		# n
        move	$t4, $a0		# list
        
l_loop: bge	$t1, $t3, d_sort
	# get the content
	sll	$t5, $t1, 2
	add	$t6, $t4, $t5
	lw	$t7, 0($t6)		# list[i]

	move	$a0, $t7		# val
	move	$a1, $t1		# i
	move	$a2, $t4		# list
	move	$a3, $t3		# n
	jal	f_pos
	
	# get the absolute loc
	move	$t2, $v0
	sll	$t2, $t2, 2
	add	$t2, $t2, $t0
	
	# save content in loc
	sw	$t7, 0($t2)
	
	addi	$t1, $t1, 1
	j	l_loop
	
f_pos:	addi	$sp, $sp, -12
	sw	$ra, 0($sp)
	sw	$s0, 4($sp)		# j
	sw	$s1, 8($sp)		# count
	
	li	$s0, 0
	li	$s1, 0			
	
f_loop:	bge	$s0, $a3, d_f

	# get list[j]
	sll	$t8, $s0, 2
	add	$t9, $a2, $t8
	lw	$t8, 0($t9)
	
	ble	$t8, $a0, p_count
	
loop:	addi	$s0, $s0, 1
	j	f_loop
	
p_count:beq	$s0, $a1, loop
	addi	$s1, $s1, 1
	j	loop
	
d_f:	move	$v0, $s1
	lw	$ra, 0($sp)
	lw	$s0, 4($sp)		
	lw	$s1, 8($sp)
	addi	$sp, $sp, 12
	jr	$ra
		

d_sort: li	$t1, 0			# i
	
c_loop: bge	$t1, $t3, d_copy
	# get content
	sll	$t5, $t1, 2
	add	$t6, $t0, $t5		
	lw	$t8, 0($t6)		# new list[j]
	
	# get the absolute address
	add	$t7, $t4, $t5
	
	sw	$t8, 0($t7)
	addi	$t1, $t1, 1
	
	j	c_loop
	
	

d_copy: lw      $ra, 408($sp)          
        lw      $t2, 404($sp)		
        lw      $t1, 400($sp) 
        addi	$sp, $sp, 412
        jr	$ra

	

        .data
space:  .asciiz " "
newln:  .asciiz "\n"