JAVAC ?= javac
JAVA ?= java
PYTHON ?= python3
OUT_DIR := out
MAIN_CLASS := Main
INPUT ?= src/teste.minilang

SOURCES := $(wildcard src/*.java)

.PHONY: all compile run report clean

all: compile

compile:
	mkdir -p $(OUT_DIR)
	$(JAVAC) -d $(OUT_DIR) $(SOURCES)

run: compile
	$(JAVA) -cp $(OUT_DIR) $(MAIN_CLASS) $(INPUT)

report:
	$(PYTHON) Docs/gerar_relatorio.py

clean:
	rm -rf $(OUT_DIR)
