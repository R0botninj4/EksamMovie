package BE;

import java.time.LocalDate;
import java.util.List;

public class Movie {

    private int id;
    private String name;
    private Double imdbRating;       // decimal
    private Double personalRating;   // decimal
    private int duration;
    private String directors;
    private int imageId;
    private String filePath;
    private LocalDate lastView;
    private List<Category> categories;

    // Constructor
    public Movie(int id, String name, Double imdbRating, Double personalRating,
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

    // ===================== GETTERS =====================
    public int getId() { return id; }
    public String getName() { return name; }
    public Double getImdbRating() { return imdbRating; }
    public Double getPersonalRating() { return personalRating; }
    public int getDuration() { return duration; }
    public String getDirectors() { return directors; }
    public int getImageId() { return imageId; }
    public String getFilePath() { return filePath; }
    public LocalDate getLastView() { return lastView; }
    public List<Category> getCategories() { return categories; }

    // ===================== SETTERS =====================
    public void setCategories(List<Category> categories) { this.categories = categories; }
    public void setPersonalRating(Double rating) { this.personalRating = rating; }
    public void setName(String name) { this.name = name; }
    public void setImdbRating(Double imdbRating) { this.imdbRating = imdbRating; }
    public void setDirectors(String directors) { this.directors = directors; }

    // ===================== OVERRIDE =====================
    @Override
    public String toString() {
        return name + " (IMDB: " + (imdbRating != null ? String.format("%.1f", imdbRating) : "N/A") + ")";
    }
}
