import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class XmlUrl {
    @Test
    public void xmlUrl() throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        String xmlLink = "https://www.timesnownews.com/google-news-sitemap-en.xml";
        driver.get(xmlLink);


        String xmlContent = driver.getPageSource();


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new URL(xmlLink).openStream());


        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new DefaultNamespaceContext());


        XPathExpression expr = xpath.compile("//url/loc");


        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("URLs");
        int rowIndex = 0;


        for (int i = 0; i < nodeList.getLength(); i++) {
            String url = nodeList.item(i).getTextContent();
            Row row = sheet.createRow(rowIndex++);
            Cell cell = row.createCell(0);
            cell.setCellValue(url);
        }


        try (FileOutputStream outputStream = new FileOutputStream("Urls.xlsx")) {
            workbook.write(outputStream);
            System.out.println("Excel File Created Successfully!!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver.quit();
    }

    private static class DefaultNamespaceContext implements NamespaceContext {
        @Override
        public String getNamespaceURI(String prefix) {
            if ("xmlns".equals(prefix)) {
                return "http://www.sitemaps.org/schemas/sitemap/0.9";
            } else if ("xmlns:xhtml".equals(prefix)) {
                return "http://www.w3.org/1999/xhtml";
            } else if ("xmlns:news".equals(prefix)) {
                return "http://www.google.com/schemas/sitemap-news/0.9";
            } else if ("xmlns:image".equals(prefix)) {
                return "http://www.google.com/schemas/sitemap-image/1.1";
            }
            return null;
        }

        @Override
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Iterator<String> getPrefixes(String uri) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}

