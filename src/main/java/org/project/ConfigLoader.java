package org.project;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties = new Properties(); // Объект для хранения свойств конфигурации

    public ConfigLoader() {
        // Получаем путь к файлу конфигурации из переменной окружения
        String configPath = System.getenv("TEST_CONFIG_PATH");
        if (configPath != null) {
            try (FileInputStream input = new FileInputStream(configPath)) {
                properties.load(input); // Загружаем свойства из файла
            } catch (IOException e) {
                e.printStackTrace(); // Обработка ошибок ввода-вывода
            }
        } else {
            // Вывод сообщения об ошибке, если переменная окружения не установлена
            System.err.println("Переменная окружения TEST_CONFIG_PATH не установлена.");
        }
    }

    // Метод для получения хоста из конфигурации
    public String getHost() {
        return properties.getProperty("host", "127.0.0.1");
    }

    // Метод для получения порта из конфигурации
    public int getPort() {
        return Integer.parseInt(properties.getProperty("port", "22"));
    }

    // Метод для получения имени пользователя из конфигурации
    public String getUsername() {
        return properties.getProperty("username", "defaultUser");
    }

    // Метод для получения пароля из конфигурации
    public String getPassword() {
        return properties.getProperty("password", "defaultPassword");
    }

    // Метод для получения пути к файлу из конфигурации
    public String getFilePath() {
        return properties.getProperty("file_path", "json.txt");
    }

    // Метод для получения пути к папке с Expect-скриптами из конфигурации
    public String getPathToExpectScriptsFolder() {
        return properties.getProperty("pathToExpectFolder", "expects_scripts");
    }

    // Метод для получения пути к исполняемому файлу Expect из конфигурации
    public String getPathToExpectBinFile() {
        return properties.getProperty("pathToExpectBinFile", "usr/bin/expect");
    }

    // Метод для получения пути к файлу known_hosts из переменной окружения
    public String getKnownHostsPath() {
        String knownHostsPath = System.getenv("KNOWN_HOSTS_PATH"); // Получаем значение переменной окружения
        if (knownHostsPath == null) {
            System.out.println("Переменная окружения KNOWN_HOSTS_PATH не задана."); // Вывод сообщения, если переменная не задана
        }
        return knownHostsPath;
    }
}