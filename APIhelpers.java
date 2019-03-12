/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Kiko
 */
public class APIhelpers {
    
    // methods to read and write the tokens in separate files
    public static void writeFile(String filepath, String atoken, String rtoken) 
    throws IOException
    {
        File file = new File (filepath);
        BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
        out.write(atoken+"\n"+rtoken);
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
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
            .build();
        
        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .state("x4xkmn9pu3j6ukrs8n")
            .scope("user-read-playback-state")
            .show_dialog(true)
            .build();
            
        final URI uri = authorizationCodeUriRequest.execute();
            
        System.out.println("Copy this URL:");
        System.out.println(uri.toString());
        
        Scanner reader = new Scanner(System.in);
        System.out.println("Paste the code from the URL after the redirect: ");
        code = reader.nextLine();

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
