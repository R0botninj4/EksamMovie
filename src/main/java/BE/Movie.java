package BE;

import java.time.LocalDate;
import java.util.List;

public class Movie {

    private int id;
    private String name;
    private Integer imdbRating;
    private Integer personalRating;
    private int duration;
    private String directors;
    private int imageId;
    private String filePath;
    private LocalDate lastView;
    private List<Category> categories;

    public Movie(int id, String name, Integer imdbRating, Integer personalRating,
                 int duration, String directors, int imageId,
                 String filePath, LocalDate lastView) {
        this.id = id;
        this.name = name;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.duration = duration;
        this.directors = directors;
        this.imageId = imageId;
        this.filePath = filePath;
        this.lastView = lastView;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public Integer getImdbRating() { return imdbRating; }
    public Integer getPersonalRating() { return personalRating; }
    public int getDuration() { return duration; }
    public String getDirectors() { return directors; }
    public int getImageId() { return imageId; }
    public String getFilePath() { return filePath; }
    public LocalDate getLastView() { return lastView; }
    public List<Category> getCategories() { return categories; }

    // Setters
    public void setCategories(List<Category> categories) { this.categories = categories; }
    public void setPersonalRating(Integer rating) { this.personalRating = rating; }
}
