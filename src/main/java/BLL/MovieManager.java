package BLL;

import BE.Category;
import BE.Movie;
import DAL.MovieDAO;

import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO = new MovieDAO();

    // ===================== BASIC CRUD =====================
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

    public void updateMovie(Movie movie,
                            String title,
                            String directors,
                            int imdb,
                            int personal,
                            List<Category> categories) {

        movie.setName(title);
        movie.setDirectors(directors);
        movie.setImdbRating(imdb);
        movie.setPersonalRating(personal);

        movieDAO.updateMovie(movie);
        movieDAO.updateMovieCategories(movie.getId(), categories);
    }

    // ===================== SEARCH / FILTER =====================
    public List<Movie> searchMovies(
            List<Movie> movies,
            String searchText,
            int minImdb,
            List<Category> selectedCategories) {

        return movies.stream()
                .filter(m -> matchesSearchText(m, searchText))
                .filter(m -> matchesMinImdb(m, minImdb))
                .filter(m -> matchesCategories(m, selectedCategories))
                .toList();
    }

    private boolean matchesSearchText(Movie m, String text) {
        if (text == null || text.isBlank()) return true;

        String lower = text.toLowerCase();
        return m.getName().toLowerCase().contains(lower)
                || m.getDirectors().toLowerCase().contains(lower);
    }

    private boolean matchesMinImdb(Movie m, int minImdb) {
        return m.getImdbRating() != null && m.getImdbRating() >= minImdb;
    }

    private boolean matchesCategories(Movie m, List<Category> selected) {
        if (selected == null || selected.isEmpty()) return true;
        if (m.getCategories() == null) return false;

        return m.getCategories().containsAll(selected);
    }
}
