package tk.sherrao.bukkit.altlimiter;

import java.io.File;
import java.io.IOException;

import tk.sherrao.bukkit.altlimiter.listener.PlayerJoinListener;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;

public class AltLimiter extends SherPlugin {
    
    protected DataWatcher data;
    protected SherEventListener listener;
    
    public AltLimiter() {
        super();
    
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        this.data = new DataWatcher( this );
        this.listener = new PlayerJoinListener( this );
        super.registerEventListener( listener );
        
        try {
            new Placeholders( new File( super.getDataFolder(), "placeholders.txt" ) ).write();
       
        } catch ( IOException e ) { e.printStackTrace(); }
        super.complete();
        
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        
    }
 
    public DataWatcher getDataWatcher() { return data; }
    
}