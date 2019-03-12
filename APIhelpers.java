package shutdowner;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

/**
 *
 * @author Keeko
 */
public class APIhelpers {
    
    // methods to read and write the tokens in separate files
    public static void writeFile(String filepath, String atoken, String rtoken) 
    throws IOException
    {
        File file = new File (filepath);
        BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
        out.write(atoken+ "\n" +rtoken);
        out.close();
    }
     
    // method to refresh tokens if I have the latest on files    
    public static String[] readFile(String filepath)
    throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(filepath));
        String atoken = in.readLine();
        String rtoken = in.readLine();
        return new String[]{atoken, rtoken};
    }
    
    public static void refreshTokens(SpotifyApi spotifyApi){
        
        // using a SpotifyApi object, call the refresh method with your latest tokens saved 
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                .build();
            
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();            
            
            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            
        } catch ( IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // method to get tokens from code and URI
    public static void authorizationCodeUri(String code, SpotifyApi spotifyApi){
        
        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .state("x4xkmn9pu3j6ukrs8n")
            .scope("user-read-playback-state")
            .show_dialog(true)
            .build();
        
        final URI uri = authorizationCodeUriRequest.execute();
        
        // print out the URI given with the authorizationCodeUriRequest so you can allow access to this app;
        System.out.println("Copy this URL to your browser:");
        System.out.println(uri.toString());
        
        Scanner reader = new Scanner(System.in);
        System.out.println("Paste the code on the new URL (after code= and before the &): ");
        code = reader.nextLine();

        // use the code read by the user after the redirect to finally get the tokens
        final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
            .build();
                
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
              
            // Set access and refresh token for further "spotifyApi" object usage
            String aToken = authorizationCodeCredentials.getAccessToken();
            String rToken = authorizationCodeCredentials.getRefreshToken();
            spotifyApi.setAccessToken(aToken);
            spotifyApi.setRefreshToken(rToken);
            
            
        }   catch (IOException | SpotifyWebApiException i) {
            System.out.println("Error:" + i.getMessage());
            
        }
    }
    
    
}
