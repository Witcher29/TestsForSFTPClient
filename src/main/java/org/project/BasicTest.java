package org.project;

import com.jcraft.jsch.JSchException;
import org.project.testingCode.SFTPClient;

import org.testng.Assert;
import org.testng.annotations.*;

import java.util.Map;

public class BasicTest {

    protected SFTPClient client; // Клиент для работы с SFTP
    protected static final String HOST; // Хост SFTP-сервера
    protected static final int PORT; // Порт SFTP-сервера
    protected static final String USERNAME; // Имя пользователя для подключения
    protected static final String PASSWORD; // Пароль для подключения
    protected static final String FILE_PATH; // Путь к файлу на SFTP-сервере
    protected static final String KNOWN_HOSTS_PATH; // Путь к файлу known_hosts

    // Статический блок для инициализации конфигурации
    static {
        ConfigLoader configLoader = new ConfigLoader(); // Загрузчик конфигурации
        HOST = configLoader.getHost(); // Получаем хост
        PORT = configLoader.getPort(); // Получаем порт
        USERNAME = configLoader.getUsername(); // Получаем имя пользователя
        PASSWORD = configLoader.getPassword(); // Получаем пароль
        FILE_PATH = configLoader.getFilePath(); // Получаем путь к файлу
        KNOWN_HOSTS_PATH = configLoader.getKnownHostsPath(); // Получаем путь к known_hosts
    }

    // Метод, выполняемый перед каждым тестом
    @BeforeMethod
    public void setUp() throws JSchException {
        client = new SFTPClient(HOST, PORT, USERNAME, PASSWORD, FILE_PATH, KNOWN_HOSTS_PATH); // Инициализация клиента
    }

    // Метод, выполняемый после каждого теста
    @AfterMethod
    public void tearDown() {
        client.disconnect(); // Отключение клиента
    }

    // Тест для проверки загрузки JSON-файла
    @Test(description = "Тестирует загрузку JSON-файла.")
    public void testLoadJsonFromFile() {
        Map<String, String> data = client.loadJsonFromFile(); // Загрузка данных из файла
        Assert.assertNotNull(data, "Данные не загружены из файла."); // Проверка, что данные не null
    }

    @DataProvider(name = "manyDomains")
    public Object[][] domainDataProvider() {
        return new Object[][]{
                {
                        new String[][]{
                                {"first.domain", "192.168.0.1"},
                                {"second.domain", "192.168.0.2"},
                                {"third.domain", "192.168.0.3"},
                                {"forth.domain", "192.168.0.10"},
                                {"fifth.domain", "192.168.0.11"}
                        }
                }
        };
    }

    @DataProvider(name = "domainIpPairs")
    public static Object[][] provideDomainIpPairs() {
        return new Object[][]{
                // {IP-адрес, Ожидаемый домен}
                {"192.168.0.1", "first.domain"},
                {"192.168.0.2", "second.domain"},
                {"192.168.0.3", "third.domain"}
        };
    }

    @DataProvider(name = "newDomainIpPairs")
    public static Object[][] provideNewDomainIpPairs() {
        return new Object[][]{
                {"192.123.27.2", "newDom1"},
                {"2.4.5.245", "superNewDom"},
                {"56.34.23.2", "newDom2"}
        };
    }

    @DataProvider(name = "domainIpPairsWithEqualsDomain")
    public static Object[][] provideEqualsDomainAndIpPairs() {
        return new Object[][]{
                // {IP-адрес, Ожидаемый домен}
                {"122.168.0.1", "first.domain"},
                {"122.168.0.2", "second.domain"},
                {"122.168.0.3", "third.domain"}
        };
    }

    @DataProvider(name = "domainIpPairsWithEqualsIp")
    public static Object[][] provideDomainAndEqualsIpPairs() {
        return new Object[][]{
                // {IP-адрес, Ожидаемый домен}
                {"192.168.0.1", "sdsst.domain"},
                {"192.168.0.2", "sjhgnd.domain"},
                {"192.168.0.3", "iuytd.domain"}
        };
    }

    @DataProvider(name = "WrongIpWithDomain")
    public static Object[][] provideWrong() {
        return new Object[][]{
                {"255.255.255.256", "newDom1"},
                {"0.0.0.0", "superNewDom"},
                {"2.3.4", "newDom2"},
                {"2.3.4.0", "somDom"}
        };
    }
}