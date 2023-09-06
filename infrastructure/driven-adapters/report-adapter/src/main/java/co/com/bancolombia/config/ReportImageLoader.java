package co.com.bancolombia.config;

import co.com.bancolombia.config.format.ActionsReportFormatConfig;
import co.com.bancolombia.config.format.ActionsReportPdfConfig;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.commons.io.FilenameUtils;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;

public class ReportImageLoader {
    private static final Integer MINIMUM_GAP = 10;
    private static final Integer DOUBLE_GAP = 20;
    private static final Integer REPORT_TOTAL_MARGIN = ActionsReportFormatConfig.DEFAULT_MARGIN * 2;
    private static final Integer BANK_LOGO_HEIGHT = 60;
    private static final Integer STROKE_IMAGE_WIDTH = 250;
    private static final Integer SUPERVISED_LOGO_HEIGHT = 15;
    private static final Float SCALATE_FACTOR = 8.0F;
    private static final Float IMAGE_QUALITY = 0.90F;
    private static final Dimension BANK_LOGO_DIMENSIONS = new Dimension(220, 60);
    private static final Dimension SUPERVISED_LOGO_DIMENSIONS = new Dimension(15, 120);
    private static final Dimension STROKE_IMAGE_DIMENSIONS = new Dimension(250, 60);
    private static final String BANK_LOGO_IMAGE = "bankLogo";
    private static final String SUPERVISED_LOGO_IMAGE = "supervisedLogo";
    private static final String STROKE_IMAGE = "strokeImage";
    private static final String IMAGE_FORMAT = ".jpeg";
    private static final String DATA_FOLDER = "data\\";

    private final Map<String, String> imageUrls;
    private final Map<String, String> imageLocations;
    private final String pageIndex;

    public final HorizontalListBuilder pageTitle;
    public final VerticalListBuilder pageFooterHistoryReport;
    public final VerticalListBuilder pageFooterDetailedReport;

    public ReportImageLoader(Map<String, String> imageUrls, String pageIndex) {
        this.pageIndex = pageIndex;
        this.imageUrls = imageUrls;
        imageLocations = new HashMap<>();
        imageLocations.put(BANK_LOGO_IMAGE,
                vsgToPngConverter(BANK_LOGO_IMAGE, BANK_LOGO_DIMENSIONS));
        imageLocations.put(SUPERVISED_LOGO_IMAGE,
                vsgToPngConverter(SUPERVISED_LOGO_IMAGE, SUPERVISED_LOGO_DIMENSIONS));
        imageLocations.put(STROKE_IMAGE,
                vsgToPngConverter(STROKE_IMAGE, STROKE_IMAGE_DIMENSIONS));
        pageTitle = buildPageTitle();
        pageFooterDetailedReport = buildPageFooter(PageOrientation.PORTRAIT);
        pageFooterHistoryReport = buildPageFooter(PageOrientation.LANDSCAPE);
    }

    private HorizontalListBuilder buildPageTitle() {
        return cmp.horizontalList().add(
                cmp.image(imageLocations.get(BANK_LOGO_IMAGE))
                        .setFixedHeight(BANK_LOGO_HEIGHT),
                cmp.image(imageLocations.get(STROKE_IMAGE))
                        .setFixedWidth(STROKE_IMAGE_WIDTH)
                        .setHorizontalImageAlignment(HorizontalImageAlignment.RIGHT)
        );
    }

    private VerticalListBuilder buildPageFooter(PageOrientation pageOrientation) {
        var lineWidth = 0;
        if (pageOrientation.equals(PageOrientation.PORTRAIT)) {
            lineWidth = PageType.LETTER.getWidth() - REPORT_TOTAL_MARGIN;
        } else {
            lineWidth = PageType.LETTER.getHeight() - REPORT_TOTAL_MARGIN;
        }
        return cmp.verticalList().add(
                cmp.verticalGap(DOUBLE_GAP),
                cmp.line().setFixedDimension(lineWidth, 1).setStyle(ActionsReportPdfConfig.getFooterLineStyle()),
                cmp.verticalGap(MINIMUM_GAP),
                cmp.horizontalList().add(
                        cmp.image(imageLocations.get(SUPERVISED_LOGO_IMAGE))
                                .setStyle(ActionsReportPdfConfig.getSupervisedLogoStyle())
                                .setHeight(SUPERVISED_LOGO_HEIGHT)
                ),
                cmp.pageXofY().setStyle(ActionsReportPdfConfig.getFooterDataStyle())
                        .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                        .setFormatExpression(pageIndex)
        );
    }

    @SuppressWarnings("fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    private String vsgToPngConverter(String imageName, Dimension dimension) {
        try {
            float scaledWidth = dimension.width * SCALATE_FACTOR;
            float scaledHeight = dimension.height * SCALATE_FACTOR;
            var file = new File(DATA_FOLDER, FilenameUtils.getName(imageName + IMAGE_FORMAT));

            var dataFolder = new File(DATA_FOLDER);
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            var transcoderInput = new TranscoderInput(imageUrls.get(imageName));
            var byteArrayOutputStream = new ByteArrayOutputStream();
            var transcoderOutput = new TranscoderOutput(byteArrayOutputStream);
            var jpegTranscoder = new JPEGTranscoder();
            jpegTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, scaledWidth);
            jpegTranscoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, scaledHeight);
            jpegTranscoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, IMAGE_QUALITY);
            jpegTranscoder.transcode(transcoderInput, transcoderOutput);
            Files.write(file.toPath(), byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            return file.toPath().toString();
        } catch (TranscoderException | IOException e) {
            throw new TechnicalException(e, TechnicalErrorMessage.UNEXPECTED_EXCEPTION);
        }
    }
}
