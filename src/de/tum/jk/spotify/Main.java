package de.tum.jk.spotify;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.gson.Gson;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.AbstractRequest;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;

import model.album.AlbumItem;
import model.album.AlbumResult;
import model.artist.ArtistItem;
import model.artist.ArtistResult;

/**
 * 
 * @author jan Little Sample on how to use a given Library to query the
 *         SpotifyAPI
 */
public class Main {

	public static final String CLIENT_ID = "";
	public static final String CLIENT_SECRET = "";

	public static void main(String[] args) throws SpotifyWebApiException, IOException {

		// Pass ClientSecret and ClientID to receive our AccessToken
		SpotifyApi spotifyApi = generateAccessTokenForApi();

		// Ask for Artist Name
		Scanner sc = new Scanner(System.in);
		System.out.println("Please give me an Artist name to look for:");
		String artistSearchName = sc.nextLine();

		// Query the API for results
		SearchArtistsRequest artistResult = spotifyApi.searchArtists(artistSearchName).build();
		// Transform JSON received Object into an ArtistResult Class Instance
		ArtistResult artRes = new Gson().fromJson(artistResult.getJson(), ArtistResult.class);
		printArtists(artRes);

		System.out.println("Please provide an ArtistID to search for Albums: ");
		String albumSearchId = sc.nextLine();

		// Query the API for results
		GetArtistsAlbumsRequest albumResult = spotifyApi.getArtistsAlbums(albumSearchId).market(CountryCode.DE).build();
		AlbumResult albRes = new Gson().fromJson(albumResult.getJson(), AlbumResult.class);

		System.out.println("Do you want me to display all Album Covers? yes/no");
		String showCover = sc.nextLine();
		// Print Albums either with or without Cover depending on yes/no
		printAlbums(albRes, showCover);

		// Close Scanner for System Input
		sc.close();
	}

	/**
	 * Iterates over the given AlbumResult Objects and prints them
	 * 
	 * @param albRes
	 * @param showCover
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void printAlbums(AlbumResult albRes, String showCover) throws IOException, MalformedURLException {
		System.out.println("I found the following Albums: ");
		System.out.println("-------------");
		for (AlbumItem alb : albRes.getItems()) {
			System.out.println(alb.getName() + "\t uniqueID: " + alb.getId());
			if (showCover.toLowerCase().equals("yes"))
				createCoverPreviewForAlbum(alb);
		}
		System.out.println("-------------");
	}

	/**
	 * Iterates over the given ArtistResult Objects and prints them
	 * 
	 * @param artRes
	 */
	private static void printArtists(ArtistResult artRes) {
		System.out.println("I found the following Artists: ");
		System.out.println("-------------");
		for (ArtistItem a : artRes.getArtists().getItems()) {
			System.out.println(a.getName() + " \t with ID:" + a.getId());
		}
		System.out.println("-------------");
	}

	/**
	 * Given a static CLIENT_ID and CLIENT_SECRET a request is send to generate a
	 * authToken for further requests. Note this type of authentication does not
	 * allow to retrieve personal information using the API. (see other auth
	 * methods)
	 * 
	 * @return
	 * @throws IOException
	 * @throws SpotifyWebApiException
	 */
	private static SpotifyApi generateAccessTokenForApi() throws IOException, SpotifyWebApiException {
		// 1. Create the SpotifyAPI

		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();

		// 2. Authenticate at the SpotifyAPI and set the accessToken for requests

		ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
		ClientCredentials clientCredentials = clientCredentialsRequest.execute();
		spotifyApi.setAccessToken(clientCredentials.getAccessToken());
		return spotifyApi;
	}

	/**
	 * Creates JFrame including a bufferedImage of the AlbumItem parameter
	 * 
	 * @param alb
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void createCoverPreviewForAlbum(AlbumItem alb) throws IOException, MalformedURLException {
		if (alb.getImages().size() > 0) {
			System.out.println("image: " + alb.getImages().get(0).getUrl());
			BufferedImage image = ImageIO.read(new URL(alb.getImages().get(0).getUrl()));
			JFrame f = new JFrame();
			JLabel l = new JLabel(new ImageIcon(image));
			f.add(l);
			f.pack();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
		}
	}
}
