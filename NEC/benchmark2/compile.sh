#!/bin/bash

# Paremeters.
CALLNEC_SRC=CALLNEC.f90
CALLNEC_EXE=CALLNEC.exe
GFORTRAN=gfortran-mp-4.5

# Clean.
rm CALLNEC.exe

# Compile.
$GFORTRAN $CALLNEC_SRC -o $CALLNEC_EXE
