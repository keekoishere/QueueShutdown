/**
 *
 * @author keekoishere
 */

package shutdowner;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;

import java.io.IOException;
import java.net.URI;

public class Shutdowner {

    private static final String clientId = "9eebbd0704784cf597ee0f2048bfed83";
    private static final String clientSecret = "4fba634045214040bbaf490adf36e91d";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("YOUR APP REDIRECT URI HERE");
    private static String code = "";
    private static String[] tokens = new String[2]; // 0 is access token, 1 is refresh
    private static String FilePath = "filepath where you'll be saving your tokens for the auth";

    // creating SpotifyApi object for the access
    private static SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setClientId(clientId)
          .setClientSecret(clientSecret)
          .setRedirectUri(redirectUri)
          .build();    
    
    public static void main(String[] args) {
        // try reading rtoken and access token;
        try {
             tokens = APIhelpers.readFile(FilePath);
            spotifyApi.setRefreshToken(tokens[1]);
        
        // if there's no error, keep going and get access from files
        APIhelpers.refreshTokens(spotifyApi);   
        System.out.println("Got tokens, starting shutdowner.");
        
        // else ask for code through URI redirect
        } catch ( IOException e){
            APIhelpers.authorizationCodeUri(code, spotifyApi);
        
        // since we have no files created, we need to write new files with the tokens
        try {
            APIhelpers.writeFile(FilePath, spotifyApi.getAccessToken(), spotifyApi.getRefreshToken());
        } catch (IOException a){
            System.out.println("Error - " + a);
        }
        
    }
    
    // after getting access, look for info on playback    
    GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = spotifyApi.getInformationAboutUsersCurrentPlayback()
                  .market(CountryCode.SE)
                  .build();
    try{
        CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
        // I use a do loop so I have a 10s interval to play the song and setup the queue on spotify
        do {
            try
            {
                Thread.sleep(20000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
        } while (currentlyPlayingContext.getIs_playing() == true);
        
        String commandExec = "shutdown -s";
        System.out.println("Stopped playing. Shutting Down.");
        Runtime.getRuntime().exec(commandExec);
        
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}
    

