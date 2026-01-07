package BLL;

import BE.Category;
import BE.Movie;
import DAL.MovieDAO;

import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO = new MovieDAO();

    public List<Movie> getAllMovies() {
        return movieDAO.getAllMovies();
    }

    public void addMovie(Movie movie, List<Category> categories) {
        int movieId = movieDAO.addMovie(movie);

        for (Category c : categories) {
            movieDAO.addCategoryToMovie(movieId, c.getId());
        }
    }

    public void deleteMovie(Movie movie) {
        movieDAO.deleteMovie(movie.getId());
    }

    public void updatePersonalRating(Movie movie, int rating) {
        movieDAO.updatePersonalRating(movie.getId(), rating);
        movie.setPersonalRating(rating);
    }

    public void moviePlayed(Movie movie) {
        movieDAO.updateLastViewed(movie.getId());
    }

    public List<Category> getCategoriesForMovie(Movie movie) {
        return movieDAO.getCategoriesForMovie(movie.getId());
    }
}
