
      PROGRAM CALLNEC

      INCLUDE 'NECPAR.INC'
      PARAMETER (IRESRV=MAXMAT**2)
      IMPLICIT REAL*8 (A-H,O-Z)
      EXTERNAL SOMSET
      CHARACTER AIN*2,FILNAM*40,PLFNAM*60
      complex*16 zin
      COMPLEX*16 CM,ZARRAY,CEINS,ZPEDA,AX,BX,CX,ZPED,AIX,BIX,CIX,CUR
	INTEGER*8 X_VALUE
	CHARACTER*20 X_ARG
    	INTEGER*8 Y_VALUE
	CHARACTER*20 Y_ARG
	
	REAL POSIC,posz(100),pi,d
	INTEGER NDIP


	pi=2*asin(1D0)
	
	CALL GETARG(1, X_ARG)
	Read(X_ARG,'(I8)')  X_VALUE
	NDIP=X_VALUE
    
    	CALL GETARG(2, Y_ARG)
    	Read(Y_ARG,'(I8)')  Y_VALUE
	J=Y_VALUE
     


	!NDIP=10


	!DO J=1,100


!	 -(N-1)*(d+L)/2 hasta (N-1)*(d+L)/2
      d=0.01+0.01*real(j-1)
	posic=-(ndip-1)*(d+0.5)/2
	
	DO k=1,NDIP
      	posz(k)=posic+real(k-1)*(d+0.5)

	END DO

	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	REWIND 15
	WRITE(15,*)
	CLOSE(15) 

	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	WRITE(15,153)'CM Dipolo para benchmark 5 Collinear de lambda/2'
	WRITE(15,153)'CE'


       DO JJ=1,NDIP
	 WRITE(15,154) JJ,49,0.,0.,posz(jj)-0.25,0.,0.,posz(jj)+0.25,0.0001

	 END DO

	WRITE(15,153)'GE 0'

	DO jj=1,NDIP
	WRITE(15,155) jj,0.
	END DO

	WRITE(15,153)'RP 0,1,1,1010, 90., 0., 0., 0.'
	WRITE(15,153)'EN'

	CLOSE(15) 

       CALL system('../Debug/NEC -i NEC.INP -o NEC.OUT')
	! Extract directivity from NEC.OUT and place the output in GAINS.OUT
	CALL system("cat  NEC.OUT | head -n $(expr $(cat NEC.OUT " &
	// " | grep -n 'RADIATION PATTERNS' | awk -F: '{print $1}') + 5) " &
	// " | tail -n 1 | awk '{print $1, $5}' > GAIN.OUT")


151   FORMAT('GW ', I3, ',1,', 6(F8.4, ','), F8.4)
152   FORMAT('FR ', '0,', I4 , ',0,', '0,' , 2(F9.4, ',') , '0')
153   FORMAT(A)
154   FORMAT('GW ', I3, ',' , I3, ',' , 6(F8.4, ','), F8.4)
155	FORMAT('EX 0,', I3, ',25,0,1.0,', F8.4)
      END
