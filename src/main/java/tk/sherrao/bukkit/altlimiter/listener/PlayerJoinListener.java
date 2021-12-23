package tk.sherrao.bukkit.altlimiter.listener;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import tk.sherrao.bukkit.altlimiter.AltLimiter;
import tk.sherrao.bukkit.altlimiter.DataWatcher;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;
import tk.sherrao.utils.strings.StringMultiJoiner;
import tk.sherrao.utils.strings.StringTime;

public class PlayerJoinListener extends SherEventListener {
    
    protected DataWatcher data;
    protected boolean announceKick;
    
    public PlayerJoinListener( SherPlugin pl ) {
        super( pl );
        
        this.data = ((AltLimiter) pl).getDataWatcher();
        this.announceKick = config.getBoolean( "announce-alt-being-banned" );
        
    }
    
    private String formatMessage( String str, InetAddress address, UUID id ) {        
        OfflinePlayer player = Bukkit.getOfflinePlayer( id );
        String ip = address.getHostAddress();
        
        List<String> allowed = data.getAllAllowedAccountsConnectedTo( address );
        List<String> denied = data.getAllDeniedAccountsConnectedTo( address );

        return str.replace( "[allowed-accounts-uuid]", new StringMultiJoiner( ", " ).add( allowed ).toString() )
                .replace( "[denied-accounts-uuid]", new StringMultiJoiner( ", " ).add( denied ).toString() )
                .replace( "[allowed-accounts-names]", new StringMultiJoiner( ", " ).add( allowed, 
                        (s) -> { return Bukkit.getOfflinePlayer( UUID.fromString(s) ).getName(); } ) )
                 
                .replace( "[denied-accounts-names]", new StringMultiJoiner( ", " ).add( denied, 
                        (s) -> { return Bukkit.getOfflinePlayer( UUID.fromString(s) ).getName(); } ) )
                
                .replace( "[last-played]", new StringTime( player.getLastPlayed() )
                        .setFormat( "[d] day(s), [h] hour(s), [m] minute(s), and [s] seconds ").toString()  )
                .replace( "[uuid]", id.toString() )
                .replace( "[ip]", ip )
                .replace( "[username]", player.getName() )
                .replace( "[max-alt-amount]", String.valueOf( data.getMaxAltsConnectionPerIP() ) );
        
    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onPlayerPreLogin( AsyncPlayerPreLoginEvent event ) {
        InetAddress ip = event.getAddress();
        UUID uuid = event.getUniqueId();
        if( !data.allowedAccess( ip, uuid ) ) {
            StringMultiJoiner sj = new StringMultiJoiner( "\n" );
            for( String s : config.getStringList( "kick-messages" ) ) 
                sj.add( formatMessage( s, ip, uuid ) );

            event.setLoginResult( Result.KICK_OTHER ); 
            event.setKickMessage( sj.toString() );
            if( announceKick )
                for( String s : config.getStringList( "announcements.announcement" ) )
                    Bukkit.broadcastMessage( formatMessage( s, ip, uuid ) );
            
        } else
            return;

    }
    
}