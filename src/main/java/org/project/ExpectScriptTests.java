package org.project;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ExpectScriptTests {

    String pathToExpectFolder = new ConfigLoader().getPathToExpectScriptsFolder();

    @Test(priority = 2)
    public void testWithExpectScript() {

        // Создаём объект File для указанной директории
        File directory = new File(pathToExpectFolder);

        // Проверяем, что это директория и что она существует
        if (directory.exists() && directory.isDirectory()) {
            // Получаем массив файлов, находящихся в директории
            File[] files = directory.listFiles();

            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    // Извлекаем числа из имен файлов
                    String name1 = f1.getName();
                    String name2 = f2.getName();

                    // Предполагается, что имена файлов имеют формат "exceptX", где X - число
                    int number1 = Integer.parseInt(name1.replaceAll("\\D+", ""));
                    int number2 = Integer.parseInt(name2.replaceAll("\\D+", ""));

                    return Integer.compare(number1, number2);
                }
            });

            int countOfMistakes = 0;
            for (File expectFile : files) {
                try {
                    // Запуск Expect-скрипта с параметрами
                    System.out.println("\nЗапуск Expect-скрипта: " + expectFile.getName());
                    ExpectRunner.runExpectScript(
                            expectFile.getAbsolutePath(),
                            BasicTest.HOST, String.valueOf(BasicTest.PORT), BasicTest.USERNAME, BasicTest.PASSWORD, BasicTest.FILE_PATH
                    );
                } catch (Exception e) {
                    System.err.println("Ошибка при запуске Expect-скрипта: " + e.getMessage());
                    countOfMistakes ++;
                }
            }
            if (countOfMistakes > 0)
                Assert.fail("Ошибки при запуске Expect-скриптов");
        } else {
            System.out.println("Указанный путь не является директориеё или директория не существует.");
            Assert.fail("Не было выполненио ни одного expect скрипта");
        }

    }
}

