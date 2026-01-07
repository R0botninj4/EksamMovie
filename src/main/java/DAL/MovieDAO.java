package DAL;

import BE.Category;
import BE.Movie;
import DAL.DB.DBConnector;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private final DBConnector dbConnector;

    public MovieDAO() {
        try {
            dbConnector = new DBConnector();
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to database", e);
        }
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movie";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("imdb_rating"),
                        rs.getInt("personal_rating"),
                        rs.getInt("duration"),
                        rs.getString("directors"),
                        rs.getInt("image_id"),
                        rs.getString("file_path"),
                        rs.getDate("last_view") != null
                                ? rs.getDate("last_view").toLocalDate()
                                : null
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public int addMovie(Movie movie) {
        String sql = """
            INSERT INTO Movie
            (name, imdb_rating, personal_rating, duration,
             directors, image_id, file_path, last_view)
            VALUES (?, ?, ?, ?, ?, ?, ?, NULL)
        """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, movie.getName());
            stmt.setInt(2, movie.getImdbRating());
            stmt.setInt(3, movie.getPersonalRating());
            stmt.setInt(4, movie.getDuration());
            stmt.setString(5, movie.getDirectors());
            stmt.setInt(6, movie.getImageId());
            stmt.setString(7, movie.getFilePath());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updatePersonalRating(int movieId, int rating) {
        String sql = "UPDATE Movie SET personal_rating = ? WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLastViewed(int movieId) {
        String sql = "UPDATE Movie SET last_view = GETDATE() WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMovie(int movieId) {
        String sql = "DELETE FROM Movie WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCategoryToMovie(int movieId, int categoryId) {
        String sql = "INSERT INTO CatMovie (movie_id, category_id) VALUES (?, ?)";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Category> getCategoriesForMovie(int movieId) {
        List<Category> categories = new ArrayList<>();
        String sql = """
            SELECT c.id, c.name
            FROM Category c
            JOIN CatMovie cm ON c.id = cm.category_id
            WHERE cm.movie_id = ?
        """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
}
