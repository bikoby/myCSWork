#
# Read in an int, caculate nth fibonacci number
 	.text
	.globl	main
main:
	addi	$sp, $sp, -12		# Make additional stack space.
	sw	$ra, 12($sp)		# Save the return address
	sw      $t0,  8($sp)            # the f old
	sw      $t1,  4($sp)            # the f older
	sw      $t2,  0($sp)            # the f new
	
	# Ask the OS to read value
        la      $a0, i1_msg              # String prompt
        jal     rd_int                  # Jump to function that prints a msg
                                        #   and reads an int
	move	$t3, $v0		# Copy return value to safe location
	
	# initialize t old, t older and i
	li      $t0, 1
	li      $t1, 0
	li	$t4, 2
	
	
f_loop: bgt     $t4, $t3, done          # If $t4 > $t3 (i >  n), 
                                        #    branch out of loop.
                                        #    Otherwise continue.
        add	$t2, $t0, $t1		# t new = t old + t older
        addi    $t1, $t0, 0
        addi    $t0, $t2, 0
        addi    $t4, $t4, 1             # Increment $t2 (i++)
        j       f_loop                  # Go to the loop test
	
done:	# Print input value with message
        la      $a0, r_msg              # Message to appear with int
        move    $a1, $t2                # The int to print
        jal     pr_int                  # Go to function	

	# Restore the values from the stack, and release the stack space.
	lw	$ra	, 0($sp)		# Retrieve return address
	addu	$sp, $sp, 4		# Free stack space.

	# Return -- go to the address left by the caller.  OK for SPIM
	# jr	$ra

        # Exit system call:  SPIM or MARS
        li    $v0, 10
        syscall


        # Function to print a message and read an int
        # String to print is in $a0
rd_int: 
        # Put return address on stack
	addi	$sp, $sp, -4		# Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address

        li      $v0, 4                  # Print string code.
        syscall                         # Print prompt
	li	$v0, 5			# Code for read int.
	syscall				# Read int, int is in $v0.

	# Restore the values from the stack, and release the stack space.
	lw	$ra, 0($sp)		# Retrieve return address
	addu	$sp, $sp, 4		# Free stack space.

	# Return -- go to the address left by the caller.
	jr      $ra		        # Return.  Int is in $v0.


        # Function to print a message and an int
        # String to print is in $a0, int is in $a1
pr_int: 
        # Put return address on stack
	addi	$sp, $sp, -4		# Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address

        li      $v0, 4                  # Print string code.
        syscall                         # Print message
	li	$v0, 1			# Code for print int.
        move    $a0, $a1                # Make int available to syscall
	syscall				# Print int
        li      $v0, 4                  # Print string code.
        la      $a0, newln              # Make newln avail to syscall
        syscall                         # Print newln

	# Restore the values from the stack, and release the stack space.
	lw	$ra, 0($sp)		# Retrieve return address
	addu	$sp, $sp, 4		# Free stack space.

	# Return -- go to the address left by the caller.
	jr      $ra		        # Return.    


        .data
i1_msg: .asciiz "Enter int an 'n'\n"
r_msg: .asciiz "The result is "     
newln: .asciiz "\n"
