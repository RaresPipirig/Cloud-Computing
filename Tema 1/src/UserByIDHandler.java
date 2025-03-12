import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class UserByIDHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("GET".equals(exchange.getRequestMethod())){
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            String id = parts[2];

            if(!Controller.isNumeric(id)){
                Controller.sendResponse(exchange, 400,
                        "{\n\"message\": \"Invalid user id.\"\n}");
                return;
            }
            int userId = Integer.parseInt(id);

            try {
                String user = DBController.getUserByID(userId);

                if(user.isEmpty()){
                    Controller.sendResponse(exchange, 404,
                            "{\n\"message\": \"User not found.\"\n}");
                    return;
                }

                Controller.sendResponse(exchange, 200,user);

            } catch (Exception e) {
                e.printStackTrace();
                Controller.sendResponse(exchange, 500,
                        "{\n\"message\": \"Internal server error.\"\n}");
            }
        }else if("DELETE".equals(exchange.getRequestMethod())){
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            String id = parts[2];

            if(!Controller.isNumeric(id)){
                Controller.sendResponse(exchange, 400,
                        "{\n\"message\": \"Invalid user id.\"\n}");
                return;
            }
            int userId = Integer.parseInt(id);

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

                int verify = SessionManager.getID(token);

                if(verify == -1){
                    Controller.sendResponse(exchange, 400,
                            "{\n\"message\": \"Invalid session token.\"\n}");
                    return;
                }

                if(verify == userId){
                    if(DBController.deleteUser(userId)){
                        Controller.sendResponse(exchange, 200,
                                "{\n\"message\": \"Account deleted successfully.\"\n}");

                        SessionManager.endSession(token);
                    }
                    else{
                        Controller.sendResponse(exchange, 404,
                                "{\n\"message\": \"User not found.\"\n}");
                    }
                }else{
                    Controller.sendResponse(exchange, 401,
                            "{\n\"message\": \"You are trying to delete another user's account.\"\n}");
                }

            }catch(JSONException e){
                Controller.sendResponse(exchange, 400,
                        "{\n\"message\": \"Invalid data.\"\n}");
            }
            catch(Exception e){
                e.printStackTrace();
                Controller.sendResponse(exchange, 500,
                        "{\n\"message\": \"Internal server error.\"\n}");
            }



        }else if("PUT".equals(exchange.getRequestMethod())){
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            String operation = parts[2];

            switch(operation){
                case "password":
                    changePassword(exchange);
                    break;
                case "username":
                    changeUsername(exchange);
                    break;
                default:
                    Controller.sendResponse(exchange, 400,
                            "{\n\"message\": \"Invalid attribute.\"\n}");
            }
        }else{
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private static void changePassword(HttpExchange exchange) throws IOException {
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
            String password = requestJson.getString("password");

            int id = SessionManager.getID(token);

            if(id == -1){
                Controller.sendResponse(exchange, 400,
                        "{\n\"message\": \"Invalid session token.\"\n}");
                return;
            }

            if(DBController.updatePasswordById(id, password)){
                Controller.sendResponse(exchange, 200,
                        "{\n\"message\": \"Password updated successfully.\"\n}");
            }
            else{
                Controller.sendResponse(exchange, 404,
                        "{\n\"message\": \"User not found.\"\n}");
            }

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

    private static void changeUsername(HttpExchange exchange) throws IOException {
        Controller.sendResponse(exchange, 501,
                "{\n\"message\": \"Not implemented.\"\n}");
    }
}
