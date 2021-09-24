package jp.yude.playersbio.playersbio;


import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandBio implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Connection connection = PlayersBio.instance.connection;
            // Save player's data to db
            String sql_search = "SELECT * FROM `players` WHERE uuid = '" + player.getUniqueId() +"';";
            try {
                PreparedStatement stmt_search = connection.prepareStatement(sql_search);
                ResultSet results_search = stmt_search.executeQuery();
                if (results_search.next()) {
                    String sql = "UPDATE `players` SET `bio` = ?;";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, args[0]);
                    stmt.executeUpdate();
                    player.sendMessage("§7[PlayersBio] §fあなたのバイオグラフィーを「" + args[0] + "」に更新しました。");
                } else {
                    String sql = "INSERT INTO `players` (uuid, bio) VALUES (?, ?);";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, args[0]);
                    stmt.executeUpdate();
                    player.sendMessage("§7[PlayersBio] §fあなたのバイオグラフィーを「" + args[0] + "」に更新しました。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}