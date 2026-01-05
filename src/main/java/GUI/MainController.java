package GUI;

import BE.Category;
import BE.Movie;
import BLL.MovieManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class MainController {

    @FXML private TableView<Movie> tblMovies;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, Integer> colImdb;
    @FXML private TableColumn<Movie, Integer> colPersonal;
    @FXML private TableColumn<Movie, String> colCategories;
    @FXML private TableColumn<Movie, LocalDate> colLastViewed;
    @FXML private Slider sldPersonalRating;

    private MovieManager movieManager;
    private final ObservableList<Movie> movies = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws Exception {
        movieManager = new MovieManager();

        colTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colImdb.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getImdbRating()).asObject());
        colPersonal.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getPersonalRating()).asObject());
        colLastViewed.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getLastViewed()));

        colCategories.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getCategories().stream()
                                .map(Category::getName)
                                .reduce((a,b) -> a + ", " + b)
                                .orElse("")
                )
        );

        tblMovies.setItems(movies);
        movies.setAll(movieManager.getAllMovies());
    }

    @FXML
    private void handleSaveRating() throws SQLException {
        Movie m = tblMovies.getSelectionModel().getSelectedItem();
        if (m == null) return;

        movieManager.updatePersonalRating(m, (int) sldPersonalRating.getValue());
        tblMovies.refresh();
    }

    @FXML
    private void handlePlayMovie() throws Exception {
        Movie m = tblMovies.getSelectionModel().getSelectedItem();
        if (m == null) return;

        Desktop.getDesktop().open(new File(m.getFilePath()));
        movieManager.updateLastViewed(m);
        tblMovies.refresh();
    }

    @FXML
    private void handleClearFilters() {

    }

    @FXML private void handleAddMovie(){}
    @FXML private void handleRemoveMovie(){}
}