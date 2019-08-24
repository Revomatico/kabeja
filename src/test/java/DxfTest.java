

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.kabeja.batik.tools.SAXPDFSerializer;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;
import org.xml.sax.SAXException;

public class DxfTest {
	public static void main(String[] args) {
		try {
			DxfTest.dxfToPdf();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
	}

	public static void dxfToPdf() throws FileNotFoundException, ParseException, SAXException {
		String path = "samples/dxf/Sample3";
		File inputWord = new File(path);
	    File outputFile = new File(FilenameUtils.removeExtension(path) + ".PDF");
		Parser dxfParser = ParserBuilder.createDefaultParser();
		dxfParser.parse(new FileInputStream(inputWord+".DXF"), "UTF-8");
		DXFDocument doc = dxfParser.getDocument();
		SAXGenerator generator = new SVGGenerator();
		SAXSerializer out = new SAXPDFSerializer();
		OutputStream fileo = new FileOutputStream( outputFile);
		out.setOutput(fileo);
		generator.generate(doc, out, new HashMap());

	}
	

}
