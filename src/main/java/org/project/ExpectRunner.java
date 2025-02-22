package org.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExpectRunner {

    public static void runExpectScript(String scriptPath, String... args) throws IOException, InterruptedException {
        // Полный путь к expect (например, для Cygwin)
        String expectPath = new ConfigLoader().getPathToExpectBinFile();

        // Создаем команду для запуска Expect-скрипта
        String[] command = new String[args.length + 3];
        command[0] = expectPath;
        command[1] = scriptPath;
        System.arraycopy(args, 0, command, 2, args.length);

        List<String> listWithoutNull = Arrays.stream(command).filter(x -> x != null).collect(Collectors.toList());

        // Запускаем процесс
        Process process = new ProcessBuilder(listWithoutNull).start();

        // Чтение вывода процесса
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));

        // Вывод логов процесса
        String line;
        System.out.println("\nВывод процесса:\n");
        while ((line = outputReader.readLine()) != null) {
            System.out.println(line);
        }

        while ((line = errorReader.readLine()) != null) {
            System.err.println(line);
        }

        // Ожидаем завершения процесса
        process.waitFor();

        // Проверяем код завершения
        if (process.exitValue() != 0) {
            throw new RuntimeException("Expect-скрипт завершился с ошибкой. Код возврата: " + process.exitValue());
        }
    }
}