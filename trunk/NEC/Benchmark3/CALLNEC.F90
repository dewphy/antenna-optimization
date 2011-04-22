      PROGRAM CALLNEC

      INCLUDE 'NECPAR.INC'
      PARAMETER (IRESRV=MAXMAT**2)
      IMPLICIT REAL*8 (A-H,O-Z)
      EXTERNAL SOMSET
	INTEGER*8 X_VALUE
	CHARACTER*20 X_ARG
      CHARACTER AIN*2,FILNAM*40,PLFNAM*60
      complex*16 zin
      COMPLEX*16 CM,ZARRAY,CEINS,ZPEDA,AX,BX,CX,ZPED,AIX,BIX,CIX,CUR
	REAL POSIC,beta,a,posx(100),posy(100),pi
	INTEGER NDIP
	
	
	

	pi=2*asin(1D0)

      
	NDIP=8
	a=1

	DO J=1,NDIP
	posx(j)=a*cos(2*pi*real(j-1)/real(NDIP))
	posy(j)=a*sin(2*pi*real(j-1)/real(NDIP))
	END DO

	CALL GETARG(1, X_ARG)
	Read(X_ARG,'(I8)')  X_VALUE
	j=X_VALUE


!	DO J=1,401
	beta=0.+REAL(j-1)*0.01


	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	REWIND 15
	WRITE(15,*)
	CLOSE(15) 

	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	WRITE(15,153)'CM Dipolo para benchmark 3'
    WRITE(15,153)'CE'



       DO JJ=1,NDIP
	 WRITE(15,154) JJ,49,posx(jj),posy(jj),-0.25,posx(jj),posy(jj),0.25,0.001
	 END DO


	WRITE(15,153)'GE 0'

	DO jj=1,NDIP
	WRITE(15,155) jj,-cos(2*pi*beta*(jj-1))
	END DO

	WRITE(15,153)'RP 0,361,1,1010, 0., 0., 0.5, 0.'
	WRITE(15,153)'EN'


	CLOSE(15) 

      CALL system('../Debug/NEC -i NEC.INP -o NEC.OUT')
! Extract directivity from NEC.OUT and place the output in GAINS.OUT
	CALL system("cat  NEC.OUT | head -n $(expr $(cat NEC.OUT " &
	// " | grep -n 'RADIATION PATTERNS' | awk -F: '{print $1}') + 365) " &
	// " | tail -n 361 | awk '{print $1, $5}' > GAIN.OUT")


	! END DO

	CLOSE(2000)
151   FORMAT('GW ', I3, ',1,', 6(F8.4, ','), F8.4)
152   FORMAT('FR ', '0,', I4 , ',0,', '0,' , 2(F9.4, ',') , '0')
153   FORMAT(A)
154   FORMAT('GW ', I3, ',' , I3, ',' , 6(F8.4, ','), F8.4)
155	FORMAT('EX 0,', I3, ',25,0,1.0,', F8.4)
      END
