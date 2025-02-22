package org.project;

import com.jcraft.jsch.JSchException;
import org.project.testingCode.SFTPClient;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

@Test(dependsOnMethods = "testLoadJsonFromFile")
public class NegativeTests extends BasicTest{

    @Test(description = "Неверный хост")
    public void testInvalidHost() {
        assertThrows(JSchException.class, () -> {
            new SFTPClient("invalid.host", PORT, USERNAME, PASSWORD, FILE_PATH, KNOWN_HOSTS_PATH);
        });
    }

    @Test(description = "Неверный порт")
    public void testInvalidPort() {
        assertThrows(JSchException.class, () -> {
            new SFTPClient(HOST, 9999, USERNAME, PASSWORD, FILE_PATH, KNOWN_HOSTS_PATH);
        });
    }

    @Test(description = "Неверный логин")
    public void testInvalidUsername() {
        assertThrows(JSchException.class, () -> {
            new SFTPClient(HOST, PORT, "invalid_user", PASSWORD, FILE_PATH, KNOWN_HOSTS_PATH);
        });
    }

    @Test(description = "Неверный пароль")
    public void testInvalidPassword() {
        assertThrows(JSchException.class, () -> {
            new SFTPClient(HOST, PORT, USERNAME, "wrong_password", FILE_PATH, KNOWN_HOSTS_PATH);
        });
    }

    @Test(description = "Неверный путь к файлу")
    public void testInvalidFilePath() {
        assertThrows(RuntimeException.class, () -> {
            SFTPClient sftpClient = new SFTPClient(HOST, PORT, USERNAME, PASSWORD, "invalid_file.txt", KNOWN_HOSTS_PATH);
            sftpClient.loadJsonFromFile();
        });
    }

    @Test(description = "Неверный путь к known_hosts")
    public void testInvalidKnownHostsPath() {
        assertThrows(JSchException.class, () -> {
            new SFTPClient(HOST, PORT, USERNAME, PASSWORD, FILE_PATH, "invalid_known_hosts.txt");
        });
    }

    @Test(description = "Добавление новой пары домен – IP с уже существующим доменом",
            dataProvider = "domainIpPairsWithEqualsDomain")
    public void addEqualDomain(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.addDomainIpPair(data, domain, ip);
        assertEquals(result, "Домен уже существует", "Добавились повторяющиеся домены");
    }

    @Test(description = "Добавление новой пары домен – IP с уже существующим IP",
            dataProvider = "domainIpPairsWithEqualsIp")
    public void addEqualIp(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.addDomainIpPair(data, domain, ip);
        assertEquals(result, "IP-адрес уже существует", "Добавились повторяющиеся IP-адреса");
    }

    @Test(description = "Добавление новой пары домен – IP с некорректным IP",
            dataProvider = "WrongIpWithDomain")
    public void addWrongIp(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.addDomainIpPair(data, domain, ip);
        assertEquals(result, "Некорректный IP-адрес", "Некорректный IP-адрес добавился");
    }

    @Test(description = "Удаляем по несуществующему имени домена",
            dataProvider = "domainIpPairsWithEqualsIp")
    public void deleteWrongDomain(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.removeDomainIpPair(data, domain);
        assertEquals(result, "Домен или IP-адрес не найдены");
    }

    @Test(description = "Удаляем по несуществующему IP-адресу",
            dataProvider = "WrongIpWithDomain")
    public void deleteWrongIp(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.removeDomainIpPair(data, ip);
        assertEquals(result, "Домен или IP-адрес не найдены");
    }

    @Test(description = "Получаем IP по несуществующему имени домена",
            dataProvider = "domainIpPairsWithEqualsIp")
    public void testGettingIpByDomain(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.getIpByDomain(data, domain);
        assertEquals(result, "Домен не найден", "Нашёлся какой-то IP");
    }

    @Test(description = "Получаем домен по несуществующему IP-адресу",
            dataProvider = "domainIpPairsWithEqualsDomain")
    public void testGettingDomainByIp(String ip, String domain) {
        Map<String, String> data = client.loadJsonFromFile();
        String result = client.getDomainByIp(data, ip);
        assertEquals(result, "IP-адрес не найден", "Нашёлся какой-то домен");
    }
}
