package guru.qa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static com.codeborne.pdftest.assertj.Assertions.assertThat;


public class ZipParsingTests {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    @DisplayName("Проверка файлов из ZIP папки")

    void zipParsingTest () throws Exception {
        try (InputStream is = classLoader.getResourceAsStream("HT8.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) if (entry.getName().equals("HT.pdf")) {
                PDF pdf = new PDF(zis);
                assertThat(pdf.text).contains("Супер пресс!");
            } else if (entry.getName().equals("HTtabl.xlsx")) {
                XLS xls = new XLS(zis);
                String stringCellValue = xls.excel.getSheetAt(0).getRow(1).getCell(0)
                        .getStringCellValue();
                assertThat(stringCellValue).contains("Шампанское");
            } else if (entry.getName().equals("HTmarked.csv")) {
                CSVReader reader = new CSVReader(new InputStreamReader(zis));
                List<String[]> content = reader.readAll();
                assertThat(content).contains(
                        new String[]{"Шо как","Сумма",},
                        new String[]{"Шампанское","1750"});
            }
        }
    }
    @Test
void jsonTest () throws Exception {
        Gson gson = new Gson();
        try (InputStream is = classLoader.getResourceAsStream("simple.json")) {
            String json = new String (is.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = gson.fromJson(json,JsonObject.class);
            assertThat(jsonObject.get("from").getAsString()).isEqualTo("Saint-Petersburg");
        }
    }
}

