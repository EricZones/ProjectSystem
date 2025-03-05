package de.ericzones.projectsystem.collectives.data;

import de.ericzones.projectsystem.ProjectSystem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class FileConfig {

    protected final FileConfiguration fileConfiguration;
    protected final File file;

    public FileConfig(ProjectSystem instance, String fileName) {
        file = new File(instance.getDataFolder(), fileName);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println("Could not create file " + fileName);
            }
        }
        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException ex) {
            System.out.println("Could not load file " + fileName);
        } catch (InvalidConfigurationException ex) {
            System.out.println("Could not load configuration in " + fileName);
        }
    }

    protected void saveFile() {
        try {
            fileConfiguration.save(file);
        } catch (IOException ex) {
            System.out.println("Could not save file " + file.getName());
        }
    }

    public boolean isAvailable() {
        return file.exists() && fileConfiguration != null;
    }

    public void setEntry(String entryPath, String value) {
        fileConfiguration.set(entryPath, value);
        saveFile();
    }

    public String getValue(String entryPath) {
        Object value = fileConfiguration.get(entryPath);
        if(value instanceof String)
            return (String) value;
        else if(value instanceof MemorySection)
            return fileConfiguration.getString(entryPath);
        else
            return null;
    }

    public HashMap<String, String> getContent() {
        HashMap<String, String> content = new HashMap<>();
        fileConfiguration.getKeys(true).forEach(current -> content.put(current, getValue(current)));
        return content;
    }

    public boolean containsEntry(String entryPath) {
        return fileConfiguration.contains(entryPath);
    }

    public Set<String> getMainKeys() {
        return fileConfiguration.getKeys(false);
    }

}
