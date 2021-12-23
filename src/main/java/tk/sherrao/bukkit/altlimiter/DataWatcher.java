package tk.sherrao.bukkit.altlimiter;

import com.google.common.collect.Lists;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import tk.sherrao.bukkit.utils.config.SherConfiguration;
import tk.sherrao.bukkit.utils.logging.ColoredLogger;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;

public class DataWatcher {
    
    protected SherPlugin pl;
    protected SherConfiguration config;
    protected ColoredLogger log;
    
    protected File file;
    protected YamlConfiguration alts;
    protected int maxAltConnectionPerIP;
    
    public DataWatcher( SherPlugin pl ) {  
        this.pl = pl;
        this.config = pl.getCustomConfig();
        this.log = pl.getColoredLogger();
        
        this.file = pl.createFile( "alts.yml" );
        this.alts = YamlConfiguration.loadConfiguration( file );
        this.maxAltConnectionPerIP = config.getInt( "max-alts-per-ip" );
    
    }
    
    public boolean allowedAccess( InetAddress address, UUID uuid ) {
        String ip = address.getHostAddress();
        String id = uuid.toString();

        try { 
            /** If player joins for the first time */
            if( !alts.contains(ip) ) { 
                alts.set( ip + ".allowed", Lists.newArrayList(id) );
                alts.set( ip + ".amount", 1 );
                alts.set( ip + ".denied", Lists.newArrayList() );
                alts.save( file );
                return true;

            } else {
                List<String> allowed = alts.getStringList( ip + ".allowed" );
                List<String> denied = alts.getStringList( ip + ".denied" );

                /** If they've already logged in with this account and are allowed */
                if( allowed.contains(id) ) 
                    return true;
            
                /** If they've already logged in with this account and are denied */
                else if( denied.contains(id) )
                    return false;
                
                /** If they've joined with another account as well but they haven't passed the cap */
                else if( alts.getInt( ip + ".amount" ) < maxAltConnectionPerIP ) { 
                    allowed.add(id);
                    alts.set( ip + ".allowed", allowed );
                    alts.set( ip + ".amount", alts.getInt( ip + ".amount" ) + 1  );
                    alts.save( file );
                    return true;
                
                /** If they've passed the cap */
                } else {
                    denied.add( id );
                    alts.set( ip + ".denied", denied );
                    alts.set( ip + ".amount", alts.getInt( ip + ".amount" ) + 1  );
                    alts.save( file );
                    return false;
                        
                }
            }

        } catch( IOException e ) { e.printStackTrace(); }
        return false;
        
    }
    
    public int getMaxAltsConnectionPerIP() { return maxAltConnectionPerIP; }
    
    public int getAltAmount( InetAddress address ) {
        return alts.getInt( address.getHostAddress() + ".amount" );
        
    }
    
    public List<String> getAllAllowedAccountsConnectedTo( InetAddress address ) {
        return ( List<String> ) alts.getStringList( address.getHostAddress() + ".allowed" );
        
    }
    
    public List<String> getAllDeniedAccountsConnectedTo( InetAddress address ) {
        return alts.getStringList( address.getHostAddress() + ".denied" );
        
    }
    
    public List<String> getAllAccountsConnectedTo( InetAddress address ) {
        List<String> accounts = getAllAllowedAccountsConnectedTo( address );
        accounts.addAll( getAllDeniedAccountsConnectedTo( address ) );
      
        return accounts;
    
    }
    
}