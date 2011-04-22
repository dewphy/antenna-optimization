	PROGRAM CALLNEC

	INCLUDE 'NECPAR.INC'
	PARAMETER (IRESRV=MAXMAT**2)
	IMPLICIT REAL*8 (A-H,O-Z)
	EXTERNAL SOMSET

	CHARACTER AIN*2,FILNAM*40,PLFNAM*60
	CHARACTER*20 N_ITER, X_ARG
	COMPLEX*16 zin,CM,ZARRAY,CEINS,ZPEDA,AX,BX,CX,ZPED,AIX,BIX,CIX,CUR,XKS,XKU,XKL,ETAU,ETAL,CEPSU,CEPSL,FRATI,ETAL2,TLYT1,TLYT2,SVOLTS
	REAL POSIC,DIST, X_VALUE

	! Read distance from the first argument of the command line.
	CALL GETARG(1, X_ARG)
	Read(X_ARG, '(F4.4)' )  X_VALUE
	DIST=X_VALUE

	! Delete previous input file for NEC "NEC.INP"
	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	REWIND 15
	WRITE(15,*)
	CLOSE(15)

	! Create new input file for NEC "NEC.INP"
	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')

	WRITE(15,153)'CM Benchmark #2'
	WRITE(15,153)'CE'

	DO JJ=1,10
		POSIC=-9*(DIST)/2+(JJ-1)*DIST
		WRITE(15,154) JJ,49,POSIC,0.,-0.25,POSIC,0.,0.25,0.001
	END DO

	WRITE(15,153)'GE 0'
	WRITE(15,153)'EX 0,1,25,0,1.0,0'
	WRITE(15,153)'EX 0,2,25,0,1.0,0'
	WRITE(15,153)'EX 0,3,25,0,1.0,0'
	WRITE(15,153)'EX 0,4,25,0,1.0,0'
	WRITE(15,153)'EX 0,5,25,0,1.0,0'
	WRITE(15,153)'EX 0,6,25,0,1.0,0'
	WRITE(15,153)'EX 0,7,25,0,1.0,0'
	WRITE(15,153)'EX 0,8,25,0,1.0,0'
	WRITE(15,153)'EX 0,9,25,0,1.0,0'
	WRITE(15,153)'EX 0,10,25,0,1.0,0'
	WRITE(15,153)'RP 0,361,1,1010, 0., 90., 0.5, 0.'
	WRITE(15,153)'EN'

	CLOSE(15)

	! Run NEC on NEC.INP and place the output in NEC.OUT
	CALL system('../../../AntennaOptimization/PSO/nec2c/nec2c -i NEC.INP -o NEC.OUT')

	! Extract directivity from NEC.OUT and place the output in GAINS.OUT
	CALL system("cat  NEC.OUT | head -n $(expr $(cat NEC.OUT " &
	// " | grep -n 'RADIATION PATTERNS' | awk -F: '{print $1}') + 365) " &
	// " | tail -n 361 | awk '{print $1, $5}' > GAIN.OUT")

151   FORMAT('GW ', I3, ',1,', 6(F8.4, ','), F8.4)
152   FORMAT('FR ', '0,', I4 , ',0,', '0,' , 2(F9.4, ',') , '0')
153   FORMAT(A)
154   FORMAT('GW ', I3, ',' , I3, ',' , 6(F8.4, ','), F8.4)
      END
