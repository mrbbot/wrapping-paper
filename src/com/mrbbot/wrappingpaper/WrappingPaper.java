package com.mrbbot.wrappingpaper;

import java.io.*;
import java.util.function.Consumer;

public class WrappingPaper {
    private ProcessBuilder builder;
    private Process process;

    private Consumer<String> outputConsumer;
    private Consumer<Integer> exitConsumer;

    private Thread outputConsumeThread;
    private Thread errorConsumeThread;
    private PrintWriter stdInWriter;

    public WrappingPaper(String... command) {
        builder = new ProcessBuilder(command);
    }

    public void output(Consumer<String> consumer) {
        this.outputConsumer = consumer;
    }

    public void input(String input) {
        stdInWriter.println(input);
        stdInWriter.flush();
    }

    public void exit(Consumer<Integer> consumer) {
        this.exitConsumer = consumer;
    }

    public void start() {
        if(process != null) {
            throw new IllegalStateException("The process is already running!");
        }

        try {
            process = builder.start();
            outputConsumeThread = consumeStream(process.getInputStream(), true);
            errorConsumeThread = consumeStream(process.getErrorStream(), false);
            stdInWriter = new PrintWriter(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        process.destroy();

        try {
            outputConsumeThread.join();
            errorConsumeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stdInWriter.close();

        outputConsumeThread = null;
        errorConsumeThread = null;
        stdInWriter = null;
        process = null;
    }

    private Thread consumeStream(InputStream inputStream, boolean stdOutput) {
        Thread thread = new Thread(() -> {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if(outputConsumer != null)
                        outputConsumer.accept(line);
                }
                reader.close();
                inputStreamReader.close();
                if(stdOutput && exitConsumer != null)
                    exitConsumer.accept(process.exitValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }
}
