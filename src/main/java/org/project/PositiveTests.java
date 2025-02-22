package org.project;

import com.jcraft.jsch.SftpException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.testng.Assert.assertEquals;

@Test(dependsOnMethods = "testLoadJsonFromFile")
public class PositiveTests extends BasicTest{

    @Test(description = "Тестируем парсинг файла типа, заданного примером", dataProvider = "manyDomains")
    public void testParseJson(String[][] domainsAndIps) {
        // Формируем JSON-строку динамически
        StringBuilder jsonStringBuilder = new StringBuilder("{ \"addresses\": [ ");
        for (int i = 0; i < domainsAndIps.length; i++) {
            String[] domainAndIp = domainsAndIps[i];
            jsonStringBuilder.append("{ \"domain\": \"").append(domainAndIp[0]).append("\", \"ip\": \"")
                    .append(domainAndIp[1]).append("\" }");
            if (i < domainsAndIps.length - 1) {
                jsonStringBuilder.append(", ");
            }
        }
        jsonStringBuilder.append("]}");
        String jsonString = jsonStringBuilder.toString();

        // Ожидаемое значение с использованием TreeMap
        TreeMap<String, String> expected = new TreeMap<>();
        for (String[] domainAndIp : domainsAndIps) {
            expected.put(domainAndIp[0], domainAndIp[1]);
        }

        // Вызов метода парсинга
        Map<String, String> actual = client.parseJson(jsonString);
        TreeMap<String, String> actualSorted = new TreeMap<>(actual);

        // Сравнение ожидаемого результата с фактическим
        Assert.assertEquals(actualSorted, expected, "Полученный JSON не соответствует ожидаемому выводу");
    }

    @Test(description = "Тестируем форматирование JSON для загрузки в тестовый файл")
    public void testFormatJson_SingleEntry() {
        // Подготовка
        Map<String, String> data = new HashMap<>();
        data.put("example.com", "192.168.1.1");
        // Действие
        String result = client.formatJson(data);

        // Проверка
        String expected = "{\n" +
                "    \"addresses\": [\n" +
                "        {\n" +
                "            \"domain\": \"example.com\",\n" +
                "            \"ip\": \"192.168.1.1\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        assertEquals(result, expected, "Неверный формат JSON для карты с одной парой.");
    }

    @Test(description = "Тестирует получение IP-адреса по домену.", dataProvider = "domainIpPairs")
    public void testGetIpByDomain(String ipTest, String domainTest) {
        Map<String, String> data = client.loadJsonFromFile();
        String ip = client.getIpByDomain(data, domainTest);
        assertEquals(ip, ipTest, "IP-адрес не совпадает.");
    }

    @Test(description = "Тестирует получение домена по IP-адресу.", dataProvider = "domainIpPairs")
    public void testGetDomainByIp(String ipTest, String domainTest) {
        Map<String, String> data = client.loadJsonFromFile();
        String domain = client.getDomainByIp(data, ipTest);
        assertEquals(domain, domainTest, "Домен не совпадает.");
    }

    @Test(description = "Тестирует добавление пары домен-IP.", dataProvider = "newDomainIpPairs")
    public void testAddDomainIpPair(String ipTest, String domainTest) throws IOException, SftpException {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.addDomainIpPair(data, domainTest, ipTest);
        assertEquals(result, "Пара добавлена", "Пара не была добавлена.");
        client.saveJsonToFile(data);
    }

    @Test(description = "Тестирует удаление пары домен-IP по домену.", dependsOnMethods = "testAddDomainIpPair",
            dataProvider = "newDomainIpPairs")
    public void testRemoveDomainIpPairByDomain(String ipTest, String domainTest) throws IOException, SftpException {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.removeDomainIpPair(data, domainTest);
        assertEquals(result, "Пара удалена по домену", "Пара не была удалена.");
        client.saveJsonToFile(data);
    }

    @Test(description = "Тестирует удаление пары домен-IP по IP-адресу.", dependsOnMethods = "testAddDomainIpPair",
            priority = 1, dataProvider = "newDomainIpPairs")
    public void testRemoveDomainIpPairByIp(String ipTest, String domainTest) throws IOException, SftpException {
        Map<String, String> data = client.loadJsonFromFile();
        client.addDomainIpPair(data, domainTest, ipTest);

        String result = client.removeDomainIpPair(data, ipTest);
        assertEquals(result, "Пара удалена по IP-адресу", "Пара не была удалена.");
        client.saveJsonToFile(data);
    }

    @Test(description = "Тестирует валидность IPv4-адреса.")
    public void testIsValidIpv4() {
        Assert.assertTrue(client.isValidIpv4("192.168.0.1"), "IP-адрес должен быть валидным.");
        Assert.assertFalse(client.isValidIpv4("256.255.100.2"), "IP-адрес должен быть невалидным.");
    }

    @Test(description = "Тестирует вывод пар 'домен – IP' в алфавитном порядке.", priority = 2)
    public void testPrintDomainIpPairs() {
        Map<String, String> data = client.loadJsonFromFile();
        // Перехват вывода в консоль
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Вызов тестируемого метода
        client.printDomainIpPairs(data);

        // Восстановление стандартного вывода
        System.setOut(originalOut);

        //возвращает строку, представляющую разделитель строк, используемый в текущей операционной системе
        String lineSeparator = System.lineSeparator();

        // Ожидаемый результат (отсортированный в алфавитном порядке)
        String expectedOutput =
                "first.domain - 192.168.0.1" + lineSeparator +
                "second.domain - 192.168.0.2" + lineSeparator +
                "third.domain - 192.168.0.3";

        // Сравнение фактического и ожидаемого вывода
        assertEquals(outputStream.toString().trim(), expectedOutput, "Пары 'домен – IP' не отсортированы в алфавитном порядке.");
    }
}
