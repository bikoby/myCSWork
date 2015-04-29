#
# Program to read a positive integer n, and compute the nth Fibonacci number
#   using recursion:
#
#     f_0 = 0
#     f_1 = 1
#     f_n = f_(n-1) + f_(n-2), n >= 2 
	.text
	.globl	main
main:
	addi	$sp, $sp, -4 	        # Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address
	
	# Ask the OS to read a number and put it in a temporary register
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
	move	$a0, $v0
	jal	f_rec
	j	done


f_rec:	addi	$sp, $sp, -12 	        # Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address
	sw	$s0, 4($sp)		# save for the arguement
	sw	$s1, 8($sp)		# save for the sum
	
	beq	$a0, 0, zero
	beq	$a0, 1,	one
	
	move	$s0, $a0
	addi	$a0, $s0, -1
	jal	f_rec
	move	$s1, $v0
	addi	$a0, $s0, -2
	jal	f_rec
	add	$v0, $s1, $v0
	j	return

zero:	li	$v0, 0
	j	return
	
one:	li	$v0, 1
	
return: lw	$ra, 0($sp)		
	lw	$s0, 4($sp)		
	lw	$s1, 8($sp)
	addi	$sp, $sp, 12
	jr	$ra
        # Done with the loop, print result
done:   move    $a0, $v0                # Put f_old in $a0
	li      $v0, 1                  # Code to print an int
        syscall                         # Print the string

	# Restore the values from the stack, and release the stack space.
	lw	$s0, 4($sp)
     	lw	$ra, 0($sp)		# Retrieve the return address
	addi	$sp, $sp, 8	        # Make additional stack space.

	# Return -- go to the address left by the caller.
	# jr	$ra
        li      $v0, 10
        syscall