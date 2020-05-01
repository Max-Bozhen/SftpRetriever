package com.app;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"sftp.port = 10022", "sftp.remote.directory.download.filter=*.csv"})
public class SftpRetrieverTest {

    private static EmbeddedSftpServer server;

    private static Path sftpFolder;

    @Value("${sftp.local.directory.download}")
    private String localDirectoryDownload;


    @BeforeClass
    public static void startServer() throws Exception {
        server = new EmbeddedSftpServer();
        server.setPort(10022);
        sftpFolder = Files.createTempDirectory("SFTP_DOWNLOAD_TEST");
        server.afterPropertiesSet();
        server.setHomeFolder(sftpFolder);
        // Starting SFTP
        if (!server.isRunning()) {
            server.start();
        }
    }

    @Before
    @After
    public void clean() throws IOException {
        Files.walk(Paths.get(localDirectoryDownload))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testDownload() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Prepare phase
        Path tempFile = Files.createTempFile(sftpFolder, "TEST_DOWNLOAD_", ".csv");
        try (FileWriter fw = new FileWriter(tempFile.toFile());
             BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.get("remote").resolve("input.csv").toFile()))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(line);
                CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT.withDelimiter(';'));
                printer.print(line);
                printer.println();
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioExceptione) {
            ioExceptione.printStackTrace();
        }

        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Path expectedFile = Paths.get(localDirectoryDownload).resolve(tempFile.getFileName());
                while (!Files.exists(expectedFile)) {
                    Thread.sleep(200);
                }
                return true;
            }
        });

        assertTrue(future.get());
    }

    @AfterClass
    public static void stopServer() {
        if (server.isRunning()) {
            server.stop();
        }
    }

}
