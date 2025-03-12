import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class SessionManager {
    private static final String FILE_PATH = "sessions.xml";

    public static File getDB() throws IOException {
        File xmlFile = new File(FILE_PATH);
        if (!xmlFile.exists()) {
            xmlFile.createNewFile();

            FileWriter writer = new FileWriter(xmlFile);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<database>\n</database>");
            writer.close();
        }

        return xmlFile;
    }

    private static void saveXML(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(FILE_PATH));
        transformer.transform(source, result);
    }

    public static String generateSessionToken(){
        return UUID.randomUUID().toString();
    }

    public static void createSession(int UID, String token) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        Element root = doc.getDocumentElement();

        Element record = doc.createElement("session");

        record.setAttribute("token", String.valueOf(token));

        Element userID = doc.createElement("userID");
        userID.appendChild(doc.createTextNode(String.valueOf(UID)));

        record.appendChild(userID);
        root.appendChild(record);

        saveXML(doc);
    }

    public static boolean endSession(String token) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("session");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            if (record.getAttribute("token").equals(token)) {
                record.getParentNode().removeChild(record);
                saveXML(doc);
                return true;
            }
        }

        return false;
    }

    public static int getID(String token) throws Exception{
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("session");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            if (record.getAttribute("token").equals(token)) {
                String id = record.getElementsByTagName("userID").item(0).getTextContent();
                return Integer.parseInt(id);
            }
        }

        return -1;
    }

}
