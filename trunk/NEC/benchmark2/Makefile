# Paremeters.
CALLNEC_SRC=CALLNEC.f90
CALLNEC_EXE=outputs/CALLNEC.exe
GFORTRAN=gfortran-mp-4.5

all: $(CALLNEC_EXE)

$(CALLNEC_EXE): $(CALLNEC_SRC)
	mkdir outputs
	$(GFORTRAN) $(CALLNEC_SRC) -o $(CALLNEC_EXE)

clean:
	rm -drf outputs
