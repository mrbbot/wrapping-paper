# wrapping-paper
A very simple wrapper for any program with text output.

## Usage

Imagine the following Python program. For reference purposes, this will be referred to as `name.py`.

```python
print("What is your name?")
name = input()
print("Hello", name)
```

### Initialising

To initialise the wrapper, a `Wrapper` object must be created.

```java
Wrapper wrapper = new Wrapper("python", "-u", "name.py");

wrapper.start();
```

> The `-u` flag forces Python's `stdout` and `stderr` streams to be unbuffered so the `print` statements don't have to be followed by a call to `sys.stdout.flush()` to flush them

### Output

Output can be captured from the program by using the `output(Consumer<String>)` function. Here is an example that prints all of the output from the program.

```java
wrapper.output(output -> {
    System.out.println(output);
});
```

### Exits

Program exits can be captured in a similar way using the `exit(Consumer<Integer>)` function. The integer value is the exit code.

```java
wrapper.exit(exitCode -> {
    System.out.println("Process finished with exit code " + exitCode);
});
```

### Input

To send input to the program the `input(Object)` function can be used. An `IllegalStateException` is thrown is there is no process running when this function is called.

```java
wrapper.input("MrBBot");
```

### Complete Example

```java
import com.mrbbot.wrappingpaper.Wrapper;

public class Name {
    public static void main(String[] args) {
        Wrapper wrapper = new Wrapper("python", "-u", "name.py");

        wrapper.output(output -> {
            System.out.println(output);
            if(output.equals("What is your name?")) {
                wrapper.input("MrBBot");
            }
        });

        wrapper.exit(exitCode -> {
            System.out.println("Process finished with exit code " + exitCode);
        });

        wrapper.start();
    }
}
```