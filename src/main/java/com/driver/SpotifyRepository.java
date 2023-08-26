package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean isPresent = false;
        Artist artist = new Artist();
        for(Artist artist1 : artists){
            if(artist1.getName().equals(artistName)){
                isPresent = true;
                artist = artist1;
                break;
            }
        }
        if(!isPresent){
            createArtist(artistName);
        }
        Album album = new Album(title);
        albums.add(album);
        //public HashMap<Artist, List<Album>> artistAlbumMap;
        List<Album> albumList = artistAlbumMap.getOrDefault(artist, new ArrayList<>());
        albumList.add(album);
        artistAlbumMap.put(artist, albumList);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean isPresent = false;
        Album refAlbum = new Album();
        for(Album album : albums){
            if(album.getTitle().equals(albumName)){
                isPresent = true;
                refAlbum = album;
                break;
            }
        }
        if(!isPresent){
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title, length);
        songs.add(song);
        List<Song> list = albumSongMap.getOrDefault(refAlbum, new ArrayList<>());

        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> list = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength() == length){
                list.add(song);
            }
        }
        playlistSongMap.put(playlist, list);
        //searching for user
        boolean isPresent = false;
        User refUser = new User();
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                refUser = user;
                isPresent = true;
                break;
            }
        }
        if (!isPresent){
            throw new Exception("User does not exist");
        }
        List<User> newListUser = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        newListUser.add(refUser);
        playlistListenerMap.put(playlist, newListUser);

        //public HashMap<User, Playlist> creatorPlaylistMap;
        creatorPlaylistMap.put(refUser, playlist);

        //public HashMap<User, List<Playlist>> userPlaylistMap;
        List<Playlist> newPlayList = userPlaylistMap.getOrDefault(refUser, new ArrayList<>());
        newPlayList.add(playlist);
        userPlaylistMap.put(refUser, newPlayList);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> list = new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                list.add(song);
            }
        }
        playlistSongMap.put(playlist, list);
        //searching for user
        boolean isPresent = false;
        User refUser = new User();
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                refUser = user;
                isPresent = true;
                break;
            }
        }
        if (!isPresent){
            throw new Exception("User does not exist");
        }
        List<User> newListUser = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        newListUser.add(refUser);
        playlistListenerMap.put(playlist, newListUser);

        //public HashMap<User, Playlist> creatorPlaylistMap;
        creatorPlaylistMap.put(refUser, playlist);

        //public HashMap<User, List<Playlist>> userPlaylistMap;
        List<Playlist> newPlayList = userPlaylistMap.getOrDefault(refUser, new ArrayList<>());
        newPlayList.add(playlist);
        userPlaylistMap.put(refUser, newPlayList);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = new Playlist();
        boolean isPlayListPresent = false;
        for(Playlist playlist1 : playlists){
            if(playlist1.getTitle().equals(playlistTitle)){
                isPlayListPresent = true;
                playlist = playlist1;
                break;
            }
        }
        if(!isPlayListPresent){
            throw new Exception("Playlist does not exist");
        }
//        public HashMap<Playlist, List<User>> playlistListenerMap;
//        public HashMap<User, Playlist> creatorPlaylistMap;
//        public HashMap<User, List<Playlist>> userPlaylistMap;
        boolean isPresentUserr = false;
        User user = new User();
        for(User user1 : users){
            if(user1.getMobile().equals(mobile)){
                user = user1;
                isPresentUserr = true;
                break;
            }
        }
        if (!isPresentUserr){
            throw new Exception("User does not exist");
        }
        if(creatorPlaylistMap.getOrDefault(user, new Playlist()) == playlist){
            return playlist;
        }
        if(playlistListenerMap.getOrDefault(playlist, new ArrayList<>()).contains(user)){
            return playlist;
        }

        List<User> newListUser = playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
        newListUser.add(user);
        playlistListenerMap.put(playlist, newListUser);

        List<Playlist> newPlayList = userPlaylistMap.getOrDefault(user, new ArrayList<>());
        newPlayList.add(playlist);
        userPlaylistMap.put(user, newPlayList);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        boolean isPresentUserr = false;
        User user = new User();
        for(User user1 : users){
            if(user1.getMobile().equals(mobile)){
                user = user1;
                isPresentUserr = true;
                break;
            }
        }
        if (!isPresentUserr){
            throw new Exception("User does not exist");
        }

        boolean isSongPresent = false;
        Song song = new Song();
        for(Song song1 : songs){
            if(song1.getTitle().equals(songTitle)){
                song = song1;
                isSongPresent = true;
                break;
            }
        }
        if(!isSongPresent){
            throw new Exception("Song does not exist");
        }
        //public HashMap<Song, List<User>> songLikeMap;
        List<User> userList = songLikeMap.getOrDefault(song, new ArrayList<>());
        if(userList.contains(user)){
            return song;
        }
        userList.add(user);
        songLikeMap.put(song, userList);
        song.setLikes(song.getLikes()+1);
//        public HashMap<Artist, List<Album>> artistAlbumMap;
//        public HashMap<Album, List<Song>> albumSongMap;
        Album album = new Album();
        for(Album key : albumSongMap.keySet()){
            List<Song> list = albumSongMap.get(key);
            if(list.contains(song)){
                album = key;
                break;
            }
        }
        Artist artist = new Artist();
        for(Artist key : artistAlbumMap.keySet()){
            List<Album> list = artistAlbumMap.get(key);
            if(list.contains(album)){
                artist = key;
                break;
            }
        }
        artist.setLikes(artist.getLikes()+1);
        return song;
    }

    public String mostPopularArtist() {
        String ans = "";
        int count = -1;
        for(Artist artist : artists){
            if(artist.getLikes() > count){
                count = artist.getLikes();
                ans = artist.getName();
            }
        }
        return ans;
    }

    public String mostPopularSong() {
        String ans = "";
        int count = -1;
        for(Song song : songs){
            if(song.getLikes() > count){
                ans = song.getTitle();
                count = song.getLength();
            }
        }
        return ans;
    }
}
