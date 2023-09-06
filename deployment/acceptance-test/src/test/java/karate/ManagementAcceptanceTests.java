package karate;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static karate.infrastructure.utils.ConstantManagement.BUILD;
import static karate.infrastructure.utils.ConstantManagement.CLASSPATHKARATE;
import static karate.infrastructure.utils.ConstantManagement.IGNORE;
import static karate.infrastructure.utils.ConstantManagement.JSON;
import static karate.infrastructure.utils.ConstantManagement.ONE;
import static karate.infrastructure.utils.ConstantManagement.PROJECTNAME;
import static karate.infrastructure.utils.ConstantManagement.TRUE;
import static karate.infrastructure.utils.ConstantManagement.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagementAcceptanceTests {

    @Test
    void testAll() {
        Results results = Runner.path(CLASSPATHKARATE).outputCucumberJson(TRUE).tags(IGNORE).parallel(ONE);
        generateReport(results.getReportDir());
        assertEquals(ZERO, results.getFailCount(), results.getErrorMessages());
    }

    public static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[]{JSON}, TRUE);
        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        Configuration config = new Configuration(new File(BUILD), PROJECTNAME);
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

}