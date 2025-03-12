import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("DELETE".equals(exchange.getRequestMethod())){
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }

            try{
                JSONObject requestJson = new JSONObject(requestBody.toString());
                String token = requestJson.getString("token");

                if(SessionManager.endSession(token)){
                    Controller.sendResponse(exchange, 200,
                            "{\n\"message\": \"Logout successful.\"\n}");
                    return;
                }

                Controller.sendResponse(exchange, 400,
                        "{\n\"message\": \"Invalid session token.\"\n}");

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
