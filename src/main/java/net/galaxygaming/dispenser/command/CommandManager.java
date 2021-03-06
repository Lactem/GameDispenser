package net.galaxygaming.dispenser.command;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.GameType;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CommandManager {
    
    /** Singleton instance */
    private static final CommandManager instance = new CommandManager();
    
    final Map<GameType, Set<ReflectCommand>> commands;
    private CommandMap cmap;
    PrefixedReflectCommand root;
    
    private CommandManager() {
        commands = Maps.newHashMap();
    }
    
    public void setup(GameDispenser plugin) {
        root = new PrefixedReflectCommand("gd");
        plugin.getCommand("gd").setExecutor(root);
        root.addExecutor(new CreateCommand());
        root.addExecutor(new JoinCommand());
        root.addExecutor(new LeaveCommand());
        root.addExecutor(new LoadCommand());
        root.addExecutor(new UnloadCommand());
        root.addExecutor(new StartCommand());
        root.addExecutor(new EndCommand());
        root.addExecutor(new RemoveCommand());
        root.addExecutor(new WandCommand());
        root.addExecutor(new SetCommand());
        root.addExecutor(new ListCommand());
        root.addExecutor(new ComponentCommand());
        root.addExecutor(new TeamCommand());
        root.addExecutor(new KitCommand());
        root.addExecutor(new HelpCommand());
    }
    
    /**
     * Cloning is not supported.
     */
    @Override
    public CommandManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    /**
     * Registers a {@link Command} for bukkit to watch for.
     * Multiple commands with the same prefix can be set.
     * @param command
     * @param type
     */
    public void registerCommand(Command command, GameType type) {
        Set<ReflectCommand> commandSet = commands.get(type);
        if (commandSet == null) {
            commandSet = Sets.newHashSet();
            commands.put(type, commandSet);
        }
        
        ReflectCommand cmd;
        
        if (command.hasPrefix()) {
            if (command.getPrefix().equalsIgnoreCase("gd")) {
                throw new RuntimeException("Command " + command.getName() + " cannot use the 'gd' prefix");
            }
            
            PrefixedReflectCommand prefixedCommand = null;
            
            for (ReflectCommand reflectCommand : commandSet) {
                if (reflectCommand instanceof PrefixedReflectCommand 
                        && ((PrefixedReflectCommand) reflectCommand).getName()
                        .equalsIgnoreCase(command.getPrefix())) 
                {
                    prefixedCommand = (PrefixedReflectCommand) reflectCommand;
                    break;
                }
            }
            
            if (prefixedCommand == null) {
                prefixedCommand = new PrefixedReflectCommand(command.getPrefix());
                commandSet.add(prefixedCommand);
            }
            
            prefixedCommand.addExecutor(command);
            cmd = prefixedCommand;
        } else {
            cmd = new ReflectCommand(command, command);
            commandSet.add(cmd);
        }
        
        if (getCommandMap().getCommand(cmd.getName()) == null)
            getCommandMap().register("", cmd);
    }
    
    CommandMap getCommandMap() {
        if (cmap == null) {
            try {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return cmap;
    }
    
    /**
     * Unregisters all commands associated with this game type
     * @param type
     */
    public void unregisterCommands(GameType type) {
        Set<ReflectCommand> commandSet = commands.get(type);
        if (commandSet != null) {
            for (ReflectCommand command : commandSet) {
                command.unregister(getCommandMap());
            }
        }
        commands.remove(type);
    }
    
    /**
     * Unregisters all commands for every game except the commands
     * in-built into GameDispenser.
     */
    public void unregisterAll() {
        for (Set<ReflectCommand> commandSet : commands.values()) {
            for (ReflectCommand command : commandSet) {
                command.unregister(getCommandMap());
            }
        }
        commands.clear();
    }
    
    /**
     * Returns the singleton instance of CommandManager
     * @return CommandManager singleton
     */
    public static CommandManager getCommandManager() {
        return instance;
    }
}