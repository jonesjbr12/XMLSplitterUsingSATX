import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class XMLSplitter {
    public static void main(String[] args) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileReader("src/input.xml"));

            int count = 0; // To track the number of XML files created
            boolean insideShipment = false;

            StringBuilder xmlContent = new StringBuilder();
            int contentLength = 0;

            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String elementName = reader.getLocalName();
                        if ("shipment".equals(elementName)) {
                            contentLength = xmlContent.length();
                            System.out.println(contentLength);
                            insideShipment = true;
                        }
                        if (!"shipment".equals(elementName) || !"invoice".equals(elementName)) {
                            insideShipment = false;
                        }
                        xmlContent.append("<" + elementName + ">");
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        xmlContent.append(reader.getText());
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        String endElementName = reader.getLocalName();
                        xmlContent.append("</" + endElementName + ">");

                        if ("invoice".equals(endElementName)) {
                            insideShipment = false;

                            // Create a new output XML file
                            count++;
                            String outputFileName = "src/" + "xml" + count + ".xml";
                            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));

                            // Write the XML header and content to the output file
                            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
                            writer.write(xmlContent.toString());
                            writer.write("\n\t</bd>\n</MSG>");
                            writer.close();

                            // Reset XML content for the next "shipment" element
                            xmlContent.setLength((contentLength - 1) - XMLStreamConstants.END_DOCUMENT);
                        }
                        break;
                }
            }
            reader.close();
            System.out.println("XML files split successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}