package net.galaxygaming.dispenser.game;

import java.io.File;
import java.util.Map;
import net.galaxygaming.dispenser.MessagesResource;

import com.google.common.collect.Maps;

/**
 * Represents a type of Game; as in each unique Game jar file
 */
public class GameType {
    private static final Map<String, GameType> lookup = Maps.newConcurrentMap();
    
    private final String name;
    private final GameDescriptionFile description;
    private final File dataFolder;
    private final MessagesResource messages;
    GameType(String name, GameDescriptionFile description, File dataFolder, ClassLoader classLoader) {
        if (lookup.containsKey(name)) {
            throw new IllegalStateException("A GameType with the name '" + name + "' already exists.");
        }
        
        this.name = name;
        this.description = description;
        this.dataFolder = dataFolder;
        this.messages = new MessagesResource(dataFolder, classLoader);
        lookup.put(name, this);
    }
    
    /**
     * Retrieves the {@link MessagesResource} for this GameType
     * @return message resource
     */
    public MessagesResource getMessages() {
        return messages;
    }
    
    /**
     * Retrieves the description file defining this game type
     * @return game description file
     */
    public GameDescriptionFile getDescription() {
        return this.description;
    }
    
    /**
     * Gives a folder for data from this game type. Not guaranteed to exist.
     * @return game type folder
     */
    public File getDataFolder() {
        return this.dataFolder;
    }
    
    /**
     * Gives the name of this game type
     * @return name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gives the name of this game type
     * @return name
     */
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        
        if (getClass() == o.getClass()) {            
            if (this.name.equalsIgnoreCase(((GameType) o).name)) {
                return true;
            }
        } else if (o.getClass() == String.class) {
            if (this.name.equalsIgnoreCase((String) o)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
    
    /**
     * Gets the game type with this name
     * @param name name of game type
     * @return game type
     */
    public static GameType get(String name) {
        GameType result = lookup.get(name);
        if (result == null) {
            for (GameType type : lookup.values()) {
                if (type.name.equalsIgnoreCase(name)) {
                    result = type;
                    break;
                }
            }
        }
        return result;
    }
    
    static void remove(GameType type) {
        if (type != null) {
            lookup.remove(type.toString());
        }
    }
}