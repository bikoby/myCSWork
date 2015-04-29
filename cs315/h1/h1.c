hw1.c
Jinpeng Bi(Koby)

1.5

a. performance(p1) = (3*(10^9) cycles/sec) / (1.5 cycles/instructions)

                   = 2*(10^9) instructions/sec

 performance(p2) = (2.5*(10^9) cycles/sec) / (1 cycles/instructions)

                 = 2.5*(10^9) instructions/sec

 performance(p3) = (4*(10^9) cycles/sec) / (2.2 cycles/instructions)

 	 	 	 	 = 1.82*(10^9) instructions/sec

 So p2 has the highest performance.

b. p1

#cycles = (3*(10^9) cycles/sec) * 10sec = 3*(10^10) cycles

#instructions = (2*(10^9) instructions/sec) * 10sec = 2*(10^10) instructions

P2

#cycles = (2.5*(10^9) cycles/sec) * 10sec = 2.5*(10^10) cycles

#instructions = (2.5*(10^9) instructions/sec) * 10sec
				= 2.5*(10^10) instructions

P3

#cycles = (4*(10^9) cycles/sec) * 10sec = 4*(10^10) cycles

#instructions = (1.82*(10^9) instructions/sec) * 10sec
				= 1.82*(10^10) instructions

c. p1

 clock rate = (2*(10^10) instructions / 7sec) * (1.5 cycles/instructions * 1.2)

 	 	 	 = 5.14 * (10^9) GHz

 p2

 clock rate = (2.5*(10^10) instructions / 7sec) * (1 cycles/instructions * 1.2)

 	 	 	 = 4.29 * (10^9) GHz

 p3

 clock rate = (1.82*(10^10) instructions / 7sec) * (2.2 cycles/instructions * 1.2)

 	 	 	 = 6.86 * (10^9) GHz

1.6

a. p1

 CPI = (1*(10^6)*10%+2*(10^6)*20%+3*(10^6)*50%+3*(10^6)*20%)/(10^6)

 	 = 2.6

 p2

 CPI = (2*(10^6)*10%+2*(10^6)*20%+2*(10^6)*50%+2*(10^6)*20%)/(10^6)

 	 = 2

b. p1

#cycles = (1*(10^6)*10%+2*(10^6)*20%+3*(10^6)*50%+3*(10^6)*20%)

 p2

#cycles = (2*(10^6)*10%+2*(10^6)*20%+2*(10^6)*50%+2*(10^6)*20%)

 	 	 = 2.6 * (10^6)

 	 	 = 2 * (10^6)

1.7

a. compiler A

 CPI = (1.1s / 1 * (10^-9) s/cycles) / (10^9 instructions) = 1.1

 compiler B

 CPI = (1.5s / 1 * (10^-9) s/cycles) / (1.2*10^9 instructions) = 1.25

b. clock(A) = CPI(A) * (10^9) / t

 clock(B) = CPI(B) * (1.2*10^9)/t

 clock(A)/clock(B) = CPI(A)/CPI(B) * (1/1.2)= (1.1/1.25) * (1/1.2) = 1.056

 So clock B should be 5.6% faster than clock A

c. compiler A

 newt = (6 * 10^8) * 1.1 / clock(A)

 oldt = (10^9)*1.1/clock(A)

 speedup = oldt/newt = 1.67

 compiler B

 newt = (6 * 10^8) * 1.1 / clock(B)

 oldt = (1.2*10^9)*1.25/clock(B)

 speedup = oldt/newt = 2.5

1.9

1. 1 processors

 T1 = (1*2.16*10^9+12*1.28*10^9+5*2.56*10^8)/(2*10^9)

 	 = 2.16+15.36+1.28

 	 = 18.8s



2 processors

 T2 = (1*2.16*10^9/(0.7*2)+12*1.28*10^9/(0.7*2)+5*2.56*10^8)/(2*10^9)

 	 = 1.,54+10.97+1.28

 	 = 13.79s

 Speedup = T1/T2= 1.36



 4 processors

 T4 = (1*2.16*10^9/(0.7*4)+12*1.28*10^9/(0.7*4)+5*2.56*10^8)/(2*10^9)

 	 = 0.77+5.49+1.28

 	 = 7.54s

 	 Speedup = T1/T4 = 2.49

 8 processors

T8 = (1*2.16*10^9/(0.7*8)+12*1.28*10^9/(0.7*8)+
		5*2.56*10^8)/(2*10^9)

		= 0.39+2.74+1.28

		= 4.41s

		Speedup = T1/T8= 4.26

2. 1 processors

 T1 = (2*2.16*10^9+12*1.28*10^9+5*2.56*10^8)/(2*10^9)

 	 = 4.32+15.36+1.28
 	 = 20.96s

 	 Impact: 20.96/18.8=1.13



2 processors

 T2 = (2*2.16*10^9/(0.7*2)+12*1.28*10^9/(0.7*2)+5*2.56*10^8)/(2*10^9)

 	 = 13.09+10.97+1.28

 	 = 15.34s

 	 Impact: 15.34/13.79=1.11



 4 processors

 T4 = (2*2.16*10^9/(0.7*4)+12*1.28*10^9/(0.7*4)+5*2.56*10^8)/(2*10^9)

 	 = 1.,54+5.49+1.28

 	 = 8.31s

 	 Impact: 8.31/7.54= 1.10

 8 processors

T8 = (2*2.16*10^9/(0.7*8)+12*1.28*10^9/(0.7*8)+
		5*2.56*10^8)/(2*10^9)

		= 0.77+2.74+1.28
		= 4.79s

		Impact: 4.79/4.41=1.09

 The impact becomes less as the number of processors increase.

3. (1*2.16*10^9+12*X+5*2.56*10^8)/(2*10^9)=7.54s

	12*X = 7.54*2*(10^9)-2.16*(10^9)-5*2.56*(10^8)

	X = 11.64 x 10^9 / 12

	X = 9.7*(10^8)

Reduced=1.28*(10^9) – 9.7*(10^8)=3.1*(10^8)

1.13

1. (250+70+85+40)/(250+70*80%+85+40)= 445/428=1.04

 So it reduced 4%.

3. (250+70+85+40)/(250+70+85+40*X)=1.2

 X = (445/1.2-250-70-85)/40

 X = -34.17 < 0

 So it’s impossible.

1.15

T2 = t/2+4=54s

Speedup= 100/54=1.85

Ratio=1.85/2=0.93

T4 = t/4+4=29s

Speedup= 100/29=3.45

Ratio=3.45/4=0.86

T8 = t/8+4= 16.5s

Speedup= 100/16.5=6.06

Ratio=6.06/8=0.76

T16 = t/16+4=10.25s

Speedup= 100/10.25=9.76

Ratio=9.76/16=0.61

T32 = t/42+4= 7.13s

Speedup= 100/7.13=14.03

Ratio=14.03/32=0.44

T64 = t/64+4= 5.56s

Speedup= 100/5.56=17.99

Ratio=17.99/64=0.28

T128 = t/128+4= 4.78s

Speedup= 100/4.78=20.92

Ratio=20.92/128=0.16
