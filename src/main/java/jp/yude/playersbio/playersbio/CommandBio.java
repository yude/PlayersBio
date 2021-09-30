package jp.yude.playersbio.playersbio;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandBio implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            // MySQL connection
            Connection connection = PlayersBio.instance.connection;
            // Command sender
            Player player = (Player) sender;
            // SQL to check whether the player's data is already exists
            String sql_search = "SELECT * FROM `players` WHERE uuid = '" + player.getUniqueId() + "';";

            // If nothing is specified on "/bio"
            if (args.length == 0) {
                // Search player's data to db
                try {
                    PreparedStatement stmt_search = connection.prepareStatement(sql_search);
                    ResultSet results_search = stmt_search.executeQuery();
                    // Update data
                    if (results_search.next()) {
                        String sql = "DELETE FROM `players` WHERE `players`.`uuid` = ?";
                        PreparedStatement stmt = connection.prepareStatement(sql);
                        stmt.setString(1, player.getUniqueId().toString());
                        stmt.executeUpdate();
                        player.sendMessage("§7[PlayersBio] §fバイオグラフィーを削除しました。");
                    } else {
                        player.sendMessage("§7[PlayersBio] §fバイオグラフィーには既に何も設定されていませんでした。");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
            // Player's bio is specified on "/bio"
                // Search player's data to db
                try {
                    PreparedStatement stmt_search = connection.prepareStatement(sql_search);
                    ResultSet results_search = stmt_search.executeQuery();
                    // Get all args and combine them into single args / string
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String allArgs = sb.toString().trim();

                    // Update data
                    if (results_search.next()) {
                        String sql = "UPDATE `players` SET `bio` = ?;";
                        PreparedStatement stmt = connection.prepareStatement(sql);
                        stmt.setString(1, allArgs);
                        stmt.executeUpdate();
                        player.sendMessage("§7[PlayersBio] §fあなたのバイオグラフィーを「" + allArgs + "」に更新しました。");
                    } else {
                        String sql = "INSERT INTO `players` (uuid, bio) VALUES (?, ?);";
                        PreparedStatement stmt = connection.prepareStatement(sql);
                        stmt.setString(1, player.getUniqueId().toString());
                        stmt.setString(2, allArgs);
                        stmt.executeUpdate();
                        player.sendMessage("§7[PlayersBio] §fあなたのバイオグラフィーを「" + allArgs + "」に更新しました。");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}