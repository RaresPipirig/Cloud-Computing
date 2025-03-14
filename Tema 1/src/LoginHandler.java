import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("POST".equals(exchange.getRequestMethod())){
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }

            try{
                JSONObject requestJson = new JSONObject(requestBody.toString());
                String username = requestJson.getString("username");
                String password = requestJson.getString("password");

                int result = DBController.logIn(username, password);

                if(result == 0){
                    Controller.sendResponse(exchange, 204,
                            "{\n\"message\": \"User does not exist.\"\n}");
                    return;
                }

                if(result == -1){
                    Controller.sendResponse(exchange, 200,
                            "{\n\"message\": \"Wrong password.\"\n}");
                    return;
                }

                String token = SessionManager.generateSessionToken();
                SessionManager.createSession(result, token);

                Controller.sendResponse(exchange, 200,
                        "{" +
                                "\"message\": \"Login successful.\"," +
                                "\"token\": \"" + token + "\"" +
                                "}");


            }catch(JSONException e){
                Controller.sendResponse(exchange, 400,
                        "{\n\"message\": \"Invalid data.\"\n}");
            }
            catch(Exception e){
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
