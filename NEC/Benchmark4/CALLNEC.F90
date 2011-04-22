
      PROGRAM CALLNEC

      INCLUDE 'NECPAR.INC'
      PARAMETER (IRESRV=MAXMAT**2)
      IMPLICIT REAL*8 (A-H,O-Z)
      EXTERNAL SOMSET
      CHARACTER AIN*2,FILNAM*40,PLFNAM*60
	INTEGER*8 X_VALUE
	CHARACTER*20 X_ARG
    INTEGER*8 Y_VALUE
	CHARACTER*20 Y_ARG
      complex*16 zin
      COMPLEX*16 CM,ZARRAY,CEINS,ZPEDA,AX,BX,CX,ZPED,AIX,BIX,CIX,CUR
     

	INTEGER K,J
	REAL posxfin,poszfin,length,alfa,pi

	
	CALL GETARG(1, X_ARG)
	Read(X_ARG,'(I8)')  X_VALUE
	K=X_VALUE
    
    CALL GETARG(2, Y_ARG)
    Read(Y_ARG,'(I8)')  Y_VALUE
	J=Y_VALUE

	pi=2*asin(1.D0)


	!DO K=1,81
	ALFA=10.+REAL(K)

	!DO J=1,51

	length=0.5+0.05*(J-1)

	posxfin=(length-0.01)*cos(pi*alfa/180)
	poszfin=(length-0.01)*sin(pi*alfa/180)

	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	REWIND 15
	WRITE(15,*)
	CLOSE(15) 
   
	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	WRITE(15,153)'CM Dipolo para benchmark 4'
    WRITE(15,153)'CE'
	WRITE(15,154) 1,5*(J-1)+49,posxfin,0.,-poszfin-0.01,0.,0.,-.01,0.001
	WRITE(15,154) 2,2,0.,0.,-.01,0.,0.,0.01,0.001
	WRITE(15,154) 3,5*(J-1)+49,0.,0.,0.01,posxfin,0.,poszfin+0.01,0.001

	WRITE(15,153)'GE 0'

	WRITE(15,153)'EX 0,2,1,0,0.5,0'
	WRITE(15,153)'EX 0,2,2,0,0.5,0'
	WRITE(15,153)'RP 0,1,1,1010, 90., 0., 0., 0.'
	WRITE(15,153)'EN'


	CLOSE(15) 
	CALL system('../Debug/NEC -i NEC.INP -o NEC.OUT')
	! Extract directivity from NEC.OUT and place the output in GAINS.OUT
	CALL system("cat  NEC.OUT | head -n $(expr $(cat NEC.OUT " &
	// " | grep -n 'RADIATION PATTERNS' | awk -F: '{print $1}') + 5) " &
	// " | tail -n 1 | awk '{print $1, $5}' > GAIN.OUT")



	!END DO

151   FORMAT('GW ', I3, ',1,', 6(F7.4, ','), F7.4)
152   FORMAT('FR ', '0,', I4 , ',0,', '0,' , 2(F9.4, ',') , '0')
153   FORMAT(A)
154   FORMAT('GW ', I3, ',' , I3, ',' , 6(F7.4, ','), F7.4)
      END
