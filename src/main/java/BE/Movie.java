package BE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String name;
    private int imdbRating;
    private int personalRating;
    private int duration;
    private String directors;
    private String filePath;
    private LocalDate lastViewed;
    private List<Category> categories = new ArrayList<>();

    public Movie(int id, String name, int imdbRating, int personalRating,
                 int duration, String directors, String filePath, LocalDate lastViewed) {
        this.id = id;
        this.name = name;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.duration = duration;
        this.directors = directors;
        this.filePath = filePath;
        this.lastViewed = lastViewed;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getImdbRating() { return imdbRating; }
    public int getPersonalRating() { return personalRating; }
    public int getDuration() { return duration; }
    public String getDirectors() { return directors; }
    public String getFilePath() { return filePath; }
    public LocalDate getLastViewed() { return lastViewed; }
    public List<Category> getCategories() { return categories; }

    public void setPersonalRating(int rating) { this.personalRating = rating; }
    public void setLastViewed(LocalDate date) { this.lastViewed = date; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
}