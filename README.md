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

If the file can be read, any recognized fields in the JSON will override the built-in defaults from `initInputVals()`.

Supported JSON keys (all optional):

- `popSize`
- `fitGenAA`
- `fitGenAa`
- `fitGenaa`
- `mutAa`
- `mutaA`
- `migRate`
- `initFreq`
- `genRun`
- `numPop`
- `genSeed`
- `randSeed`

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
