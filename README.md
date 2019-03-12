# QueueShutdown

-- Simple Java project to shutdown your PC after your Spotify Queue stops playing --
Uses https://github.com/thelinmichael/spotify-web-api-java - a Java wrapper for the SpotifyAPI - to check every 20 seconds whether your Spotify Player is playing a track or not. If it is not, it shutsdown your computer. You will have to turn off Autoplay in your Spotify Preferences (CTRL+P) so that when your queue reaches its end, this script works.

How to:
1) Create an App in your SpotifyDashboard;
2) Copy Client ID, Secret client ID and Redirect URI to Shutdowner.java;
3) Change the filepath variable to a text file where you can save your access tokens for the User Authentication;
4) Run the project in your IDE or create a .jar executable;
5) Run first time and follow steps for the authorization flow;
6) Now you have your tokens in a file and from now on you just need to doubleclick the .jar to start;
