/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.game.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameLoader;
import net.galaxygaming.dispenser.game.GameLogger;
import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.game.GameType;

/**
 * @author t7seven7t
 */
public abstract class JavaGame implements Game {

    /** The current game state */
    protected GameState state = GameState.INACTIVE;
    
    /** Name of this game instance */
    private String name;
    
    private GameType type;
    private GameLoader loader;
    private FileConfiguration config;
    private File configFile;
    private ClassLoader classLoader;
    private Logger logger;
    
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }
    
    @Override
    public final FileConfiguration getConfig() {
        return this.config;
    }
    
    @Override
    public final void saveConfig() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Error ocurred while saving config", e);
        }
    }
    
    /**
     * Gives the file associated with this game's config
     * @return config file
     */
    protected final File getConfigFile() {
        return this.configFile;
    }
    
    @Override
    public final GameLoader getGameLoader() {
        return this.loader;
    }
    
    @Override
    public final Logger getLogger() {
        return this.logger;
    }
    
    @Override
    public final GameState getState() {
        return this.state;
    }
    
    @Override
    public final GameType getType() {
        return this.type;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    @Override
    public final void setName(String name) {
        this.name = name;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (getClass() != o.getClass()) {
            return false;
        }
        
        if (getClass() == o.getClass()) {            
            if (this.name.equalsIgnoreCase(((JavaGame) o).name)) {
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
    public final int hashCode() {
        return name.hashCode();
    }
    
    public final InputStream getResource(String fileName) {
        Validate.notNull(fileName, "Filename cannot be null");
        
        try {
            URL url = getClassLoader().getResource(fileName);
            
            if (url == null) {
                return null;
            }
            
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
    
    JavaGame() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof GameClassLoader)) {
            throw new IllegalStateException("JavaGame requires " + GameClassLoader.class.getName());
        }
    }
    
    final void initialize(String name, FileConfiguration config, GameLoader loader, File file, ClassLoader classLoader) {
        this.name = name;
        this.config = config;
        this.loader = loader;
        this.configFile = file;
        this.classLoader = classLoader;
        this.logger = new GameLogger(this, GameDispenser.getInstance());
        
        onLoad();
    }
}
