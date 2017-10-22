package a3osoft.jiratracker.validations;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class SAXXMLParser {
    public static List<Validation> parse(InputStream is) {
        List<Validation> employees = null;
        try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            XMLHandler saxHandler = new XMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            // get the `Employee list`
            employees = saxHandler.getItemsList();

        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed");
        }

        // return Employee list
        return employees;
    }
}