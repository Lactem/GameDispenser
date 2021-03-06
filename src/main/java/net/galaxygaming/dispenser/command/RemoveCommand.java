package net.galaxygaming.dispenser.command;

import java.util.logging.Level;

import org.bukkit.permissions.Permission;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;

class RemoveCommand extends Command {

    public RemoveCommand() {
        this.name = "remove";
        this.prefix = "gd";
        this.requiredArgs.add("name");
        this.permission = new Permission("gamedispenser.command.remove");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getGameManager().getGame(args[0]);
        if (game == null) {
            error(messages.getMessage(CommandMessage.UNKNOWN_GAME), args[0]);
            return;
        }
        
        GameManager.getGameManager().deleteGame(game);
        sendMessage(messages.getMessage(CommandMessage.GAME_DELETED), args[0]);
        GameDispenser.getInstance().getLogger().log(Level.INFO, player.getName() + " has deleted the game " + args[0]);
    }
}