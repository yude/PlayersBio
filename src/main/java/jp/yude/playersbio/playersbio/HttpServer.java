package jp.yude.playersbio.playersbio;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static spark.Spark.*;

public class HttpServer {
    public HttpServer(Connection connection) {
        // Specify port to expose for web API server.
        Integer web_port = PlayersBio.instance.getConfig().getInt("web_port");
        port(web_port);

        // Enable Cross-Origin Resource Sharing (CORS) policy.
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }
                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }
                    return "OK";
                });
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        // Provide players biography
        get("/:uuid", (req, res) -> {
            // Check if query is truly UUID in order to avoid SQL injection
            if (req.params(":uuid").matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
                String sql = "SELECT * FROM `players` WHERE uuid = '" + req.params(":uuid") +"';";
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    return results.getString("bio");
                } else {
                    return "";
                }
            } else {
                return "invalid_uuid";
            }
        });

    }
}