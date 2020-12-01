package entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Size;

@Entity
@NamedQuery(name = "Song.deleteAllRows", query = "DELETE from Song")
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Song() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String artist;
    @Size(max = 4)
    @Column(nullable = false)
    private int releaseYear;
    @Column(nullable = true)
    private String album;
    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "user_name")
    private Set<User> users = new HashSet();

    public Song(String name, String artist, int releaseYear, String album) {
        this.name = name;
        this.artist = artist;
        this.releaseYear = releaseYear;
        if(album != null) {
            this.album = album;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Set<User> getUsers() {
        return users;
    }
    
    public void addUser(User user) {
        this.users.add(user);
    }
}