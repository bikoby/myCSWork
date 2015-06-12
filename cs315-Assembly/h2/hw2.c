1.12
    3. 
      P1
CPU1 = CPI(P1) * 5 * (10^9) / clock(P1)
       = 0.9 * 5 * (10^9) / 4 * (10^9)
       = 1.125s
MIPS(P1) = clock(P1) / (CPI(P1) * 10^6)
            = 4 * (10^9) / (0.9 * 10^6)
            = 4.44 * 10^3
      
      P2
CPU2 = CPI(P2) * 1 * (10^9) / clock(P2)
        = 0.75 * 1 * (10^9) / 3 * (10^9)
        = 0.25s
      MIPS(P2) = clock(P2) / (CPI(P2) * 10^6)
            = 3 * (10^9) / (0.75 * 10^6)
            = 4.00 * 10^3
      MIPS(P1) > MIPS(P2)
But CUP1 > CPU2
So using MIPS to compare the performances of two different processors is a 
common fallacy.

4. 
MFLOPS(P1) = #FP operation / (t * 10^6)
                = #instructions * 0.4 / (#instructions * CPI(P1) / clock(P1)) 
                 = clock(P1) *0.4 / CPI(P1)
                   = 0.4 * MIPS(P1)
      MFLOPS(P2) = 0.4 * MIPS(P2)
      MIPS(P1) > MIPS(P2)
      So MFLOPS(P1) > MILOPS(P2)
      So it is also a fallacy.

Show how the pseudoinstruction bgt can be implemented using ``core'' MIPS instructions
    slt $t1, $reg0, $reg1
beq $t1, $zero, gt_lab

 