import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DBController {
    private static final String FILE_PATH = "database.xml";

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

    public static int getHighestId() throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");
        int maxId = 0;

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            int id = Integer.parseInt(record.getAttribute("id"));

            if (id > maxId) {
                maxId = id;
            }
        }

        return maxId;
    }

    public static String getAllUsers() throws IOException, ParserConfigurationException, SAXException {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");

        StringBuilder result = new StringBuilder("{");

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            String name = record.getElementsByTagName("username").item(0).getTextContent();
            int id = Integer.parseInt(record.getAttribute("id"));

            result.append("{" + "\"id\":")
                    .append(id)
                    .append(",\"username\":\"")
                    .append(name)
                    .append("\"},");
        }

        result.deleteCharAt(result.length() - 1);
        result.append("}");
        return result.toString();
    }

    public static boolean doesUserExist(String username) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            String name = record.getElementsByTagName("username").item(0).getTextContent();

            if (name.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static String getUserByID(int uid) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);

            String name = record.getElementsByTagName("username").item(0).getTextContent();
            int id = Integer.parseInt(record.getAttribute("id"));

            if (uid == id) {
                return "{\"username\":\""+ name +"\"}";
            }
        }
        return "";
    }

    public static int logIn(String username, String password) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            String name = record.getElementsByTagName("username").item(0).getTextContent();
            String pass = record.getElementsByTagName("password").item(0).getTextContent();
            int id = Integer.parseInt(record.getAttribute("id"));

            if (name.equalsIgnoreCase(username)) {
                if(pass.equals(password)) {
                    return id;
                }
                else{
                    return -1;
                }
            }
        }
        return 0;
    }

    public static boolean updatePasswordById(int userId, String newPassword) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");
        boolean updated = false;

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            int id = Integer.parseInt(record.getAttribute("id"));

            if (id == userId) {
                record.getElementsByTagName("password").item(0).setTextContent(newPassword);
                updated = true;
                break;
            }
        }

        if (updated) {
            saveXML(doc);
            return true;
        } else {
            return false;
        }
    }

    public static boolean updateUsernameById(int userId, String newUsername) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");
        boolean updated = false;

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            int id = Integer.parseInt(record.getAttribute("id"));

            if (id == userId) {
                record.getElementsByTagName("username").item(0).setTextContent(newUsername);
                updated = true;
                break;
            }
        }

        if (updated) {
            saveXML(doc);
            return true;
        } else {
            return false;
        }
    }

    public static void addUser(String username, String password) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        Element root = doc.getDocumentElement();

        Element record = doc.createElement("user");

        int id = getHighestId() + 1;

        record.setAttribute("id", String.valueOf(id));

        Element usernameElement = doc.createElement("username");
        usernameElement.appendChild(doc.createTextNode(username));

        Element passwordElement = doc.createElement("password");
        passwordElement.appendChild(doc.createTextNode(password));

        record.appendChild(usernameElement);
        record.appendChild(passwordElement);
        root.appendChild(record);

        saveXML(doc);
    }

    public static boolean deleteUser(int id) throws Exception {
        File xmlFile = getDB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        NodeList records = doc.getElementsByTagName("user");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            int userID = Integer.parseInt(record.getAttribute("id"));
            if (userID == id) {
                record.getParentNode().removeChild(record);
                saveXML(doc);
                return true;
            }
        }

        return false;
    }


}
