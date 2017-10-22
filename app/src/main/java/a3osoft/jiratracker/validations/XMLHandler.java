package a3osoft.jiratracker.validations;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

class XMLHandler extends DefaultHandler {

    private Boolean currentElement = false;
    private String currentValue = "";
    private Validation item = null;
    private ArrayList<Validation> itemsList = new ArrayList<>();

    ArrayList<Validation> getItemsList() {
        return itemsList;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentElement = true;
        currentValue = "";
        if (localName.equals("Validation")) {
            item = new Validation();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentElement = false;

        if (localName.equalsIgnoreCase("ID"))
            item.setId(Integer.parseInt(currentValue));
        else if (localName.equalsIgnoreCase("WarningMessage"))
            item.setMessage(currentValue);
        else if (localName.equalsIgnoreCase("ValidationFrom"))
            item.setFrom(currentValue);
        else if (localName.equalsIgnoreCase("ValidationTo"))
            item.setTo(currentValue);
        else if (localName.equalsIgnoreCase("Validation"))
            itemsList.add(item);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentElement) {
            currentValue = currentValue +  new String(ch, start, length);
        }

    }

}
