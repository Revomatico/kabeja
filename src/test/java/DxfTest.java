
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.kabeja.batik.tools.SAXJPEGSerializer;
import org.kabeja.batik.tools.SAXPDFSerializer;
import org.kabeja.batik.tools.SAXPNGSerializer;
import org.kabeja.batik.tools.SAXTIFFSerializer;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;
import org.xml.sax.SAXException;

public class DxfTest {
  private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DxfTest.class);

  public static void main(String[] args) {
    Exporter format = Exporter.JPG;
    export("samples/dxf/Sample3.dxf", format);
    export("samples/dxf/draft1.dxf", format);
    export("samples/dxf/draft2.dxf", format);
    export("samples/dxf/draft3.dxf", format);
    export("samples/dxf/draft4.dxf", format);
    export("samples/dxf/hatch_1.dxf", format);
    export("samples/dxf/hatch_16.dxf", format);
  }

  public static enum Exporter {
    PNG("png", new SAXPNGSerializer()),
    PDF("pdf", new SAXPDFSerializer()),
    JPG("jpg", new SAXJPEGSerializer()),
    TIF("tif", new SAXTIFFSerializer());
    public final String format;
    public final SAXSerializer serializer;

    Exporter(String format, SAXSerializer serializer) {
      this.format = format;
      this.serializer = serializer;
    }
  }

  private static void export(String path, Exporter exporter) {
    try {
      File inputWord = new File(path);
      File outputFile = new File("target/" + FilenameUtils.removeExtension(path) + "." + exporter.format);
      logger.info("export {} to {}", inputWord, outputFile);
      FileUtils.forceMkdir(outputFile.getParentFile());
      Parser dxfParser = ParserBuilder.createDefaultParser();
      dxfParser.parse(new FileInputStream(inputWord), "UTF-8");
      DXFDocument doc = dxfParser.getDocument();
      SAXGenerator generator = new SVGGenerator();
      SAXSerializer out = exporter.serializer;
      OutputStream fileo = new FileOutputStream(outputFile);
      out.setOutput(fileo);
      generator.generate(doc, out, new HashMap());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }
}
