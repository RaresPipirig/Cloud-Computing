import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class UsersHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("GET".equals(exchange.getRequestMethod())){
            try {
                String users = DBController.getAllUsers();
                if(users.isEmpty()){
                    Controller.sendResponse(exchange, 204,
                            "{\n\"message\": \"No users found.\"\n}");
                    return;
                }

                Controller.sendResponse(exchange, 200, users);
            } catch (Exception e) {
                e.printStackTrace();
                Controller.sendResponse(exchange, 500,
                        "{\n\"message\": \"Internal server error.\"\n}");
            }
        }
        else{
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
