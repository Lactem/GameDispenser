package net.galaxygaming.dispenser.command;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.InvalidGameException;

class LoadCommand extends Command {

    public LoadCommand() {
        this.prefix = "gd";
        this.name = "load";
        this.requiredArgs.add("type/game");
        this.requiredArgs.add("name");
        this.description = "Load a game or game type from file";
        this.permission = new Permission("gamedispenser.command.load");
    }
    
    @Override
    public void perform() {
        try {
            if (argMatchesAlias(args[0], "type", "t")) {
                GameManager.getGameManager().loadGameType(args[1]);
            } else if (argMatchesAlias(args[0], "game", "g")) {
                GameManager.getGameManager().loadGame(args[1]);
            } else {
                error("Please specify either 'game' or 'type'.");
                return;
            }
            sendMessage(messages.getMessage(CommandMessage.GAME_LOADED), args[1]);
        } catch (InvalidGameException e) {
            error(e.getMessage(), e);
        }
    }
}