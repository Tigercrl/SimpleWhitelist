package io.github.tigercrl.simplewhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleWhitelistCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendMessage(ChatColor.GREEN + "SimpleWhitelist" + ChatColor.RESET + " v" + ChatColor.GOLD + SimpleWhitelist.instance.getDescription().getVersion() + ChatColor.RESET + " by " + ChatColor.GOLD + "Tigercrl", sender);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                listWhitelistedPlayers(sender, 1);
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                SimpleWhitelist.instance.reloadConfig();
                SimpleWhitelist.whitelistConfig.reloadConfig();
                sendMessage(SimpleWhitelist.langConfig.getMessage("plugin-reloaded", sender), sender);
                return true;
            } else if (args[0].equalsIgnoreCase("import")) {
                sendMessage(SimpleWhitelist.langConfig.getMessage("whitelist-imported", sender).replace("{amount}", String.valueOf(SimpleWhitelist.whitelistConfig.importWhitelist())), sender);
                return true;
            } else if (args[0].equalsIgnoreCase("export")) {
                sendMessage(SimpleWhitelist.langConfig.getMessage("whitelist-exported", sender).replace("{amount}", String.valueOf(SimpleWhitelist.whitelistConfig.exportWhitelist())), sender);
                sendMessage(SimpleWhitelist.langConfig.getMessage("export-warning", sender), sender);
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                sendMessage("=== [ " + ChatColor.YELLOW + "Commands" + ChatColor.RESET + " ] ===", sender);
                sendCommandHelp(sender, "/swl add <player>", "Add a player to the plugin whitelist", "/swl add ");
                sendCommandHelp(sender, "/swl export <override>", "Export the plugin whitelist to the vanilla whitelist", "/swl export ");
                sendCommandHelp(sender, "/swl help", "View all commands and their functions");
                sendCommandHelp(sender, "/swl import <override>", "Import the vanilla whitelist to the plugin whitelist", "/swl import ");
                sendCommandHelp(sender, "/swl list", "List all players on the plugin whitelist");
                sendCommandHelp(sender, "/swl reload", "Reload the plugin config and whitelist");
                sendCommandHelp(sender, "/swl reload lang", "Reload the language config");
                sendCommandHelp(sender, "/swl reload whitelist", "Reload the plugin whitelist");
                sendCommandHelp(sender, "/swl remove <player>", "Remove a player from the plugin whitelist", "/swl remove ");
                sendMessage("Note: You can write \"/swl\" as \"/simplewhitelist\"", sender);
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (SimpleWhitelist.whitelistConfig.playerExists(args[1]))
                    sendMessage(SimpleWhitelist.langConfig.getMessage("player-already-whitelisted", sender).replace("{player}", args[1]), sender);
                else {
                    SimpleWhitelist.whitelistConfig.addPlayer(args[1]);
                    sendMessage(SimpleWhitelist.langConfig.getMessage("added-player", sender).replace("{player}", args[1]), sender);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (SimpleWhitelist.whitelistConfig.playerExists(args[1])) {
                    SimpleWhitelist.whitelistConfig.removePlayer(args[1]);
                    sendMessage(SimpleWhitelist.langConfig.getMessage("removed-player"), sender);
                } else {
                    sendMessage(SimpleWhitelist.langConfig.getMessage("player-not-whitelisted", sender).replace("{player}", args[1]), sender);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (args[1].equalsIgnoreCase("lang")) {
                    SimpleWhitelist.langConfig.reloadConfig();
                    sendMessage(SimpleWhitelist.langConfig.getMessage("plugin-reloaded-lang", sender), sender);
                    return true;
                } else if (args[1].equalsIgnoreCase("whitelist")) {
                    SimpleWhitelist.whitelistConfig.reloadConfig();
                    sendMessage(SimpleWhitelist.langConfig.getMessage("plugin-reloaded-whitelist", sender), sender);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                try {
                    listWhitelistedPlayers(sender, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sendMessage(SimpleWhitelist.langConfig.getMessage("invalid-number", sender).replace("{number}", args[1]), sender);
                }
                return true;
            }
        }
        sendMessage(SimpleWhitelist.langConfig.getMessage("invalid-command", sender), sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("add");
            list.add("import");
            list.add("export");
            list.add("help");
            list.add("list");
            list.add("reload");
            list.add("remove");
            return list;
        } else if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
            return SimpleWhitelist.whitelistConfig.getWhitelistedPlayers();
        } else if (args[0].equalsIgnoreCase("add") && args.length == 2) {
            return SimpleWhitelist.instance.getServer().getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(name -> !SimpleWhitelist.whitelistConfig.playerExists(name))
                    .collect(Collectors.toList());
        } else if (args[0].equalsIgnoreCase("reload") && args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("lang");
            list.add("whitelist");
            return list;
        }
        return Collections.emptyList();
    }

    private void listWhitelistedPlayers(CommandSender sender, int page) {
        List<String> whitelistedPlayers = SimpleWhitelist.whitelistConfig.getWhitelistedPlayers();
        int lastPage = whitelistedPlayers.size() / 10 + 1;

        if (page < 1 || page > lastPage) {
            sendMessage(SimpleWhitelist.langConfig.getMessage("invalid-page", sender), sender);
            return;
        } else if (whitelistedPlayers.isEmpty()) {
            sendMessage(SimpleWhitelist.langConfig.getMessage("empty-whitelist", sender), sender);
            return;
        }

        TextComponent prevPage = new TextComponent("←");
        prevPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(SimpleWhitelist.langConfig.getMessage("prev-page", sender))));
        prevPage.setBold(true);
        if (page > 1) {
            prevPage.setColor(ChatColor.AQUA);
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/simplewhitelist list " + (page - 1)));
        } else {
            prevPage.setColor(ChatColor.GRAY);
        }

        TextComponent nextPage = new TextComponent("→");
        nextPage.setBold(true);
        nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(SimpleWhitelist.langConfig.getMessage("next-page", sender))));
        if (page < lastPage) {
            nextPage.setColor(ChatColor.AQUA);
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/simplewhitelist list " + (page + 1)));
        } else {
            nextPage.setColor(ChatColor.GRAY);
        }
        BaseComponent[] pageNavComponent = new ComponentBuilder("").append(SimpleWhitelist.langConfig.getMessage("prefix", sender)).color(ChatColor.YELLOW).append("=== [ ").color(ChatColor.RESET).append(prevPage).append(" Page ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).append(String.valueOf(page)).color(ChatColor.GREEN).bold(true).append(" of ").color(ChatColor.YELLOW).append(lastPage + " ").color(ChatColor.GREEN).bold(true).append(nextPage).append(" ] ===", ComponentBuilder.FormatRetention.NONE).color(ChatColor.RESET).create();

        sendMessage(SimpleWhitelist.langConfig.getMessage("whitelist-list", sender), sender);
        sender.spigot().sendMessage(pageNavComponent);
        for (int i = (page - 1) * 10; i < whitelistedPlayers.size() && i < page * 10; i++) {
            sendMessage(" " + (i + 1) + ". " + whitelistedPlayers.get(i), sender);

        }
        sender.spigot().sendMessage(pageNavComponent);
    }

    public static void sendMessage(String message, CommandSender receiver) {
        receiver.sendMessage(SimpleWhitelist.langConfig.getMessage("prefix", receiver) + message);
    }

    public static void sendCommandHelp(CommandSender sender, String command, String description) {
        sendCommandHelp(sender, command, description, command);
    }

    public static void sendCommandHelp(CommandSender sender, String command, String description, String suggestion) {
        TextComponent commandComponent = new TextComponent(ChatColor.GOLD + command);
        commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion));
        BaseComponent[] component = new ComponentBuilder("").append(SimpleWhitelist.langConfig.getMessage("prefix", sender)).color(ChatColor.YELLOW).append(commandComponent).append(" - ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.RESET).append(description).color(ChatColor.YELLOW).create();
        sender.spigot().sendMessage(component);
    }
}
