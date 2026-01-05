package BLL;

import BE.Movie;
import DAL.MovieDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO;

    public MovieManager() throws IOException {
        movieDAO = new MovieDAO();
    }

    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.getAllMovies();
    }

    public void updatePersonalRating(Movie movie, int rating) throws SQLException {
        movie.setPersonalRating(rating);
        movieDAO.updatePersonalRating(movie.getId(), rating);
    }

    public void updateLastViewed(Movie movie) throws SQLException {
        movie.setLastViewed(LocalDate.now());
        movieDAO.updateLastViewed(movie.getId(), movie.getLastViewed());
    }
}