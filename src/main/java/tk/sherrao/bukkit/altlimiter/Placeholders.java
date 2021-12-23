package tk.sherrao.bukkit.altlimiter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Placeholders {
    
    protected File file;
    protected BufferedWriter writer;
    
    public Placeholders( File file ) 
            throws IOException {
        this.file = file;
    
    }
    
    public void write() 
            throws IOException {
        if( file.exists() )
            return;
        
        file.createNewFile();
        writer = new BufferedWriter( new FileWriter( file ) );
        
            w( "=========================" )
            .w( "  File for placeholders  " )
            .w( "=========================" )
            .w( "" )
            .w( "Placeholders for 'kick-message' and 'announce-alt-being-banned-msg': " )
            .w( " - '[allowed-accounts-uuid]' -> A list of allowed accounts by UUID " )
            .w( " - '[denied-accounts-uuid]' -> A list of banned accounts by UUID " )
            .w( " - '[allowed-accounts-names]' -> A list of allowed accounts by username" )
            .w( " - '[danied-accounts-names]' -> A list of denied accounts by username" )
            .w( " - '[last-played]' -> The time a player last played" )
            .w( " - '[uuid]' -> The UUID of the player" )
            .w( " - '[username]' -> The username of the player" )
            .w( " - '[ip]' -> The IPv4 Address of the player" )
            .w( " - '[username]' -> The username of the player" )
            .w( " - '[max-alt-amount]' -> The max accounts per IP specified in the config" );
            writer.close();
    
    }
    
    private Placeholders w( String str ) 
            throws IOException {
        writer.write( str );
        return this;
        
    }
    
}