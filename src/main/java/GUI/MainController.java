package GUI;

import BE.Category;
import BE.Movie;
import BLL.MovieManager;
import DAL.CategoryDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class MainController {

    // ===== TABLE =====
    @FXML private TableView<Movie> tblMovies;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, Integer> colImdb;
    @FXML private TableColumn<Movie, Integer> colPersonal;
    @FXML private TableColumn<Movie, String> colCategories;
    @FXML private TableColumn<Movie, String> colLastViewed;
    @FXML private TableColumn<Movie, String> colDirectors;

    // ===== FILTER =====
    @FXML private TextField txtSearch;
    @FXML private Spinner<Integer> spnMinRating;
    @FXML private ListView<Category> lstCategories;

    // ===== ADD / EDIT =====
    @FXML private TextField txtTitle;
    @FXML private TextField txtDirectors;
    @FXML private Spinner<Integer> spnImdbRating;
    @FXML private Spinner<Integer> spnPersonalRating;
    @FXML private ListView<Category> lstAddCategories;
    @FXML private Label lblFilePath;

    // ===== BOTTOM =====
    @FXML private Slider sldPersonalRating;

    private final MovieManager movieManager = new MovieManager();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private File selectedMovieFile;
    private Movie movieBeingEdited = null;
    private List<Movie> cachedMovies;


    // =====================================================
    @FXML
    public void initialize() {

        // ----- Spinners -----
        spnImdbRating.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 5)
        );
        spnPersonalRating.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 5)
        );

        spnMinRating.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0)
        );

        // ----- Categories (ADD) -----
        lstAddCategories.setItems(
                FXCollections.observableArrayList(categoryDAO.getAllCategories())
        );

        lstAddCategories.setCellFactory(
                CheckBoxListCell.forListView(Category::selectedProperty)
        );

        // ----- Categories (FILTER) -----
        lstCategories.setItems(
                FXCollections.observableArrayList(categoryDAO.getAllCategories())
        );

        lstCategories.setCellFactory(
                CheckBoxListCell.forListView(Category::selectedProperty)
        );

        // ----- Table bindings -----
        colTitle.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName())
        );

        colImdb.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getImdbRating())
        );

        colPersonal.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getPersonalRating())
        );

        colCategories.setCellValueFactory(d -> {
            List<Category> cats = d.getValue().getCategories();
            String text = (cats == null || cats.isEmpty())
                    ? ""
                    : cats.stream().map(Category::getName)
                    .reduce((a, b) -> a + ", " + b).orElse("");
            return new SimpleStringProperty(text);
        });

        colLastViewed.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getLastView() == null
                                ? ""
                                : d.getValue().getLastView().toString()
                )
        );

        colDirectors.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDirectors())
        );

        // ----- Live filtering -----
        txtSearch.textProperty().addListener((obs, o, n) -> applyFilters());
        spnMinRating.valueProperty().addListener((obs, o, n) -> applyFilters());

        lstCategories.getItems().forEach(c ->
                c.selectedProperty().addListener((obs, o, n) -> applyFilters())
        );

        loadMovies();
    }

    // =====================================================
    private void loadMovies() {

        cachedMovies = movieManager.getAllMovies();

        for (Movie m : cachedMovies) {
            m.setCategories(movieManager.getCategoriesForMovie(m));
        }

        tblMovies.setItems(FXCollections.observableArrayList(cachedMovies));
    }


    // =====================================================
    private void applyFilters() {

        String searchText = txtSearch.getText();
        int minImdb = spnMinRating.getValue();

        List<Category> selectedCategories = lstCategories.getItems().stream()
                .filter(Category::isSelected)
                .toList();

        List<Movie> filtered = movieManager.searchMovies(
                cachedMovies,
                searchText,
                minImdb,
                selectedCategories
        );

        tblMovies.setItems(FXCollections.observableArrayList(filtered));
    }

    // =====================================================
    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        spnMinRating.getValueFactory().setValue(0);
        lstCategories.getItems().forEach(c -> c.setSelected(false));
        loadMovies();
    }

    // =====================================================
    @FXML
    private void chooseMovieFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Video files", "*.mp4", "*.mpeg4")
        );

        selectedMovieFile = fc.showOpenDialog(null);

        if (selectedMovieFile != null) {
            lblFilePath.setText(selectedMovieFile.getName());
        }
    }

    // =====================================================
    @FXML
    private void handleAddMovie() {

        List<Category> selectedCategories = lstAddCategories.getItems().stream()
                .filter(Category::isSelected)
                .toList();

        if (movieBeingEdited == null) {

            if (selectedMovieFile == null) return;

            Movie movie = new Movie(
                    0,
                    txtTitle.getText(),
                    spnImdbRating.getValue(),
                    spnPersonalRating.getValue(),
                    0,
                    txtDirectors.getText(),
                    0,
                    selectedMovieFile.getAbsolutePath(),
                    null
            );

            movieManager.addMovie(movie, selectedCategories);

        } else {

            movieManager.updateMovie(
                    movieBeingEdited,
                    txtTitle.getText(),
                    txtDirectors.getText(),
                    spnImdbRating.getValue(),
                    spnPersonalRating.getValue(),
                    selectedCategories
            );

            movieBeingEdited = null;
        }

        clearForm();
        loadMovies();
    }

    // =====================================================
    @FXML
    private void handleEditMovie() {
        Movie selected = tblMovies.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        movieBeingEdited = selected;

        txtTitle.setText(selected.getName());
        txtDirectors.setText(selected.getDirectors());
        spnImdbRating.getValueFactory().setValue(selected.getImdbRating());
        spnPersonalRating.getValueFactory().setValue(selected.getPersonalRating());
        lblFilePath.setText(new File(selected.getFilePath()).getName());

        lstAddCategories.getItems().forEach(c ->
                c.setSelected(
                        selected.getCategories() != null &&
                                selected.getCategories().contains(c)
                )
        );
    }

    // =====================================================
    @FXML
    private void handleRemoveMovie() {
        Movie selected = tblMovies.getSelectionModel().getSelectedItem();
        if (selected != null) {
            movieManager.deleteMovie(selected);
            loadMovies();
        }
    }

    // =====================================================
    @FXML
    private void handlePlayMovie() {
        Movie movie = tblMovies.getSelectionModel().getSelectedItem();
        if (movie == null || movie.getFilePath() == null) return;

        try {
            Desktop.getDesktop().open(new File(movie.getFilePath()));
            movieManager.moviePlayed(movie);
            loadMovies();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    @FXML
    private void handleSaveRating() {
        Movie movie = tblMovies.getSelectionModel().getSelectedItem();
        if (movie != null) {
            movieManager.updatePersonalRating(
                    movie,
                    (int) sldPersonalRating.getValue()
            );
            loadMovies();
        }
    }

    // =====================================================
    private void clearForm() {
        txtTitle.clear();
        txtDirectors.clear();
        lblFilePath.setText("No file selected");
        selectedMovieFile = null;

        lstAddCategories.getItems().forEach(c -> c.setSelected(false));
    }
}
