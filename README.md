# PopG

This project includes a helper script to compile and run the Java application from the command line.

## Requirements

- Java JDK installed (`javac` and `java` available in PATH)

## Run the program

From the project root:

```bash
./run.sh
```

To start a new run immediately at startup (without opening the New Run settings form), use:

```bash
./run.sh -n
```

To auto-save a plot screenshot after a run finishes and then exit the app, use:

```bash
./run.sh -n -p=out/plot.png
```

The script will:

1. Compile `src/PopGUserInterface.java` into the `out/` directory.
2. Run the main class `popg.PopGUserInterface`.

## Run with a JSON defaults file

You can pass a JSON file path to preload default input values.

```bash
./run.sh path/to/defaults.json
```

You can combine it with `-n` to load defaults and immediately execute the New Run flow:

```bash
./run.sh path/to/defaults.json -n
```

You can also combine defaults, auto-run, and screenshot capture:

```bash
./run.sh path/to/defaults.json -n -p=out/plot.png
```

If the file can be read, any recognized fields in the JSON will override the built-in defaults from `initInputVals()`.

## Build an executable JAR

From the project root:

```bash
./build.sh
```

This generates `PopG.jar` in the project root.

## Run the JAR from the command line

```bash
java -jar PopG.jar
```

You can pass the same optional arguments as `run.sh`:

```bash
java -jar PopG.jar -n
java -jar PopG.jar path/to/defaults.json
java -jar PopG.jar path/to/defaults.json -n
java -jar PopG.jar path/to/defaults.json -n -p=out/plot.png
```

CLI options:

- `-n`: start a New Run immediately at startup.
- `-p=path/to/file.png`: after a run finishes, save a plot screenshot to the given path and close the application. Requires `-n`.

Supported JSON keys (all optional):

- `popSize`: initial population size.
- `fitGenAA`: fitness value for genotype `AA`.
- `fitGenAa`: fitness value for genotype `Aa`.
- `fitGenaa`: fitness value for genotype `aa`.
- `mutAa`: mutation rate from allele `A` to `a`.
- `mutaA`: mutation rate from allele `a` to `A`.
- `migRate`: migration rate between populations.
- `initFreq`: initial frequency of allele `A`.
- `genRun`: number of generations to simulate.
- `numPop`: number of populations in the simulation.
- `genSeed`: whether to generate a random seed automatically.
- `randSeed`: explicit random seed value (used when `genSeed` is `false`).

## Example JSON

```json
{
  "popSize": 250,
  "fitGenAA": 0.95,
  "fitGenAa": 1.0,
  "fitGenaa": 1.05,
  "mutAa": 0.001,
  "mutaA": 0.002,
  "migRate": 0.01,
  "initFreq": 0.4,
  "genRun": 200,
  "numPop": 15,
  "genSeed": false,
  "randSeed": 42
}
```
