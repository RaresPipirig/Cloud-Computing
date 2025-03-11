import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("DELETE".equals(exchange.getRequestMethod())){
            
        }
        else{
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
