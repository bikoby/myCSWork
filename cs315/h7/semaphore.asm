# File:           semaphore.asm
# Purpose:        Implement a semaphore using MIPS
# Functions:      sem_post and sem_wait

# main function is used for testing the semaphore in single thread, 
# set lock to 0 (locked), and sempost change it into 1, then semwait
# can go through
        .text	
        .globl	main
main:
	addi	$sp, $sp, -8 	        # Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address
	sw	$s2, 4($sp)
	move	$s2, $sp
	
	sw	$zero, 0($s2)
	move	$a0, $s2
	jal	sem_post
	
	move	$a0, $s2
	jal	sem_wait
	
	lw	$ra, 0($sp)
	lw	$s2, 4($sp)
	addi	$sp, $sp, 8
	
	li      $v0, 10
        syscall
	

#######################################################################
# Function:       sem_wait
# Purpose:        Wait for semaphore to be unlocked
# C Prototype:    void sem_wait(sem_t* semaphore)
#
#  in/out:        semaphore

	
        .globl sem_wait
sem_wait: addi	$sp, $sp, -4 	        # Make additional stack space.
	  sw	$ra, 0($sp)		# Save the return address
	  
	  move	$s1, $a0


wait:   move	$t0, $zero
        ll	$t1, 0($s1)
        sc	$t0, 0($s1)
        beq	$t0, $zero, wait
        
        beq	$t1,$zero, wait

        lw	$ra, 0($sp)
        addi	$sp, $sp, 4
        
      	jr	$ra


#######################################################################
# Function:       sem_post
# Purpose:        Unlock the semaphores
# C Prototype:    void sem_post(sem_t* semaphore)
#
# Args:           semaphore
# in/out:         semaphore
        .globl sem_post
sem_post:
        addi	$sp, $sp, -4 	        # Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address
	
	move	$s1, $a0
	li	$t2, 1

post:   move	$t0, $t2
        ll	$t1, 0($s1)
        sc	$t0, 0($s1)
        beq	$t0, $zero, post
        
        move	$t2, $t1
        
        lw	$ra, 0($sp)
        addi	$sp, $sp, 4
        
      	jr	$ra
