package com.example.tnttag.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Interface for sub-commands
 */
public interface SubCommand {
    
    /**
     * Execute the command
     * 
     * @param sender The command sender
     * @param args Command arguments (without the subcommand name)
     * @return true if command was handled
     */
    boolean execute(CommandSender sender, String[] args);
    
    /**
     * Tab completion for the command
     * 
     * @param sender The command sender
     * @param args Current arguments
     * @return List of completions
     */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
