package DAL;

import BE.Category;
import BE.Movie;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private final DBConnector db;

    public MovieDAO() throws IOException {
        db = new DBConnector();
    }

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movie";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie m = new Movie(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("imdb_rating"),
                        rs.getInt("personal_rating"),
                        rs.getInt("duration"),
                        rs.getString("directors"),
                        rs.getString("file_path"),
                        rs.getDate("last_view") != null
                                ? rs.getDate("last_view").toLocalDate()
                                : null
                );

                m.setCategories(getCategoriesForMovie(m.getId(), conn));
                movies.add(m);
            }
        }
        return movies;
    }

    private List<Category> getCategoriesForMovie(int movieId, Connection conn) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.id, c.name FROM Category c " +
                "JOIN CatMovie cm ON c.id = cm.category_id " +
                "WHERE cm.movie_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Category(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return categories;
    }

    public void updatePersonalRating(int movieId, int rating) throws SQLException {
        String sql = "UPDATE Movie SET personal_rating=? WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setInt(2, movieId);
            ps.executeUpdate();
        }
    }

    public void updateLastViewed(int movieId, LocalDate date) throws SQLException {
        String sql = "UPDATE Movie SET last_view=? WHERE id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, movieId);
            ps.executeUpdate();
        }
    }
}