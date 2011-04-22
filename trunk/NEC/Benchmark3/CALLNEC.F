      PROGRAM CALLNEC

      INCLUDE 'NECPAR.INC'
      PARAMETER (IRESRV=MAXMAT**2)
      IMPLICIT REAL*8 (A-H,O-Z)
      EXTERNAL SOMSET
	INTEGER*8 X_VALUE
	CHARACTER*20 X_ARG
      CHARACTER AIN*2,FILNAM*40,PLFNAM*60
      complex*16 zin
      COMPLEX*16 CM,ZARRAY,CEINS,ZPEDA,AX,BX,CX,ZPED,AIX,BIX,CIX,CUR,
     &XKS,XKU,XKL,ETAU,ETAL,CEPSU,CEPSL,FRATI,ETAL2,TLYT1,TLYT2,YN11,
     &YN12,YN22,SVOLTS
	REAL POSIC,beta,a,posx(100),posy(100),pi
	INTEGER NDIP
C***
      COMMON /DATA/ X(MAXSEG),Y(MAXSEG),Z(MAXSEG),SI(MAXSEG),BI(MAXSEG),
     &ALP(MAXSEG),BET(MAXSEG),SALP(MAXSEG),T2X(MAXSEG),T2Y(MAXSEG),
     &T2Z(MAXSEG),ICON1(MAXSEG),ICON2(MAXSEG),ITAG(MAXSEG),
     &ICONX(MAXSEG),IPSYM,LD,N1,N2,N,NP,M1,M2,M,MP
      COMMON/FRQDAT/IFRQ,NFRQ,FMHZS,DELFRQ,FMHZ

      COMMON/CRNT/AIX(MAXSEG),BIX(MAXSEG),CIX(MAXSEG),CUR(3*MAXSEG),
     &XKS(MAXSEG)

C***
	
	
	

	pi=2*asin(1D0)

      


C     CONSTANTES NECESARIAS PARA GENERAR EL FICHERO DE ENTRADA 

C	El barrido será para valores beta del desfase.
C	Conservare el angulo theta como segunda variable.

C	Pongo de inicio 10 antenas circulares y radio de a=1 m. (a=lambda)
	NDIP=8
	a=1
C	La posicion de las 8 antenas es:
	DO J=1,NDIP
	posx(j)=a*cos(2*pi*real(j-1)/real(NDIP))
	posy(j)=a*sin(2*pi*real(j-1)/real(NDIP))
	END DO

	CALL GETARG(1, X_ARG)
	Read(X_ARG, '(I8)' )  X_VALUE
	j=X_VALUE

C	Para la variable beta, quiero dirigir el haz a cero, cero (pag. 325 Balanis).
c	esto me deja la variable alfa=-cos(-2*pi*n/N). Esto es, lo que hago es variar linealmente
c	los valores de la exictacion enl a forma: -cos(2*pi*beta*n) con beta comprendido entre 0
C	y un entero mayor que 1 para que la funcion sea multivaluada.

C	Entre 0 y 4 nos da 4 maximos identicos.
C	DO J=1,401
	beta=0.+REAL(j-1)*0.01

C	Lo primero sera escribir el fichero NEC.INP correspondiente a este cromosoma
C	Abro el fichero para borrar el antiguo
	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	REWIND 15
	WRITE(15,*)
	CLOSE(15) 
C	Y ahora comienza la escritura del fichero
	OPEN(15,FILE='NEC.INP',STATUS='UNKNOWN')
	WRITE(15,153)'CM Dipolo para benchmark 3?? Circular array.'
    WRITE(15,153)'CE'


C	Los elementos seran dipolos de lambda/2 orientados en el eje Z
       DO JJ=1,NDIP
	 WRITE(15,154) JJ,49,posx(jj),posy(jj),-0.25,posx(jj),posy(jj)
     # 	           ,0.25,0.001
	 END DO

C	Ahora existencia de plano de tierra
	WRITE(15,153)'GE 0'
C	Lo siguiente es la excitacion. CUIDADO TODAVIA NO ESTA CLARO EL EFECTO DE BETA!!!
	DO jj=1,NDIP
	WRITE(15,155) jj,-cos(2*pi*beta*(jj-1))
	END DO

	WRITE(15,153)'RP 0,361,1,1010, 0., 0., 0.5, 0.'
	WRITE(15,153)'EN'

C	Fin de escritura. Ahora puede NEC comenzar el analisis
	CLOSE(15) 

      CALL system('../AntennaOptimization/PSO/nec2c/nec2c -i /Users/Irina/Benchmark3/NEC.INP -o /Users/Irina/Benchmark3/NEC.OUT')
! Extract directivity from NEC.OUT and place the output in GAINS.OUT
	CALL system("cat  NEC.OUT | head -n $(expr $(cat NEC.OUT " &
	// " | grep -n 'RADIATION PATTERNS' | awk -F: '{print $1}') + 365) " &
	// " | tail -n 361 | awk '{print $1, $5}' > GAIN.OUT")


	! END DO

C*** Imprime corrientes
C        open(33,file='CORRI.OUT',STATUS='UNKNOWN')
C        DO II=1,N
C          WRITE(33,*) (II-0.5)*SI(II),abs(CUR(II)),cang(cur(ii))
C        END DO
C        close(33)
C***
	CLOSE(2000)
151   FORMAT('GW ', I3, ',1,', 6(F8.4, ','), F8.4)
152   FORMAT('FR ', '0,', I4 , ',0,', '0,' , 2(F9.4, ',') , '0')
153   FORMAT(A)
154   FORMAT('GW ', I3, ',' , I3, ',' , 6(F8.4, ','), F8.4)
155	FORMAT('EX 0,', I3, ',25,0,1.0,', F8.4)
      END
