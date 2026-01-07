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

import java.io.File;
import java.util.List;

public class MainController {

    // ===== TableView =====
    @FXML private TableView<Movie> tblMovies;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, Integer> colImdb;
    @FXML private TableColumn<Movie, Integer> colPersonal;
    @FXML private TableColumn<Movie, String> colCategories;
    @FXML private TableColumn<Movie, String> colLastViewed;
    @FXML private TableColumn<Movie, String> colDirectors;

    // ===== Add Movie panel =====
    @FXML private TextField txtTitle;
    @FXML private TextField txtDirectors;
    @FXML private Spinner<Integer> spnImdbRating;
    @FXML private Spinner<Integer> spnPersonalRating;
    @FXML private ListView<Category> lstAddCategories;
    @FXML private Label lblFilePath;

    // ===== Bottom controls =====
    @FXML private Slider sldPersonalRating;

    private final MovieManager movieManager = new MovieManager();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private File selectedMovieFile;

    // =======================================================
    @FXML
    public void initialize() {

        // ----- Spinners -----
        spnImdbRating.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 5)
        );
        spnPersonalRating.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 5)
        );

        // ----- Categories (Add Movie) med checkboxes -----
        List<Category> categories = categoryDAO.getAllCategories();
        lstAddCategories.setItems(FXCollections.observableArrayList(categories));

        // Custom CheckBoxListCell, som viser navn og binder korrekt til selectedProperty
        lstAddCategories.setCellFactory(lv -> {
            CheckBoxListCell<Category> cell = new CheckBoxListCell<>(cat -> cat.selectedProperty());
            cell.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Category cat) {
                    return cat.getName();
                }
                @Override
                public Category fromString(String string) {
                    return null; // ikke brugt
                }
            });
            return cell;
        });

        // ===== TableColumn bindings =====
        colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colDirectors.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDirectors()));
        colImdb.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getImdbRating()));
        colPersonal.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPersonalRating()));
        colCategories.setCellValueFactory(data -> {
            List<Category> cats = data.getValue().getCategories();
            String categoryString = cats == null || cats.isEmpty()
                    ? ""
                    : cats.stream().map(Category::getName).reduce((a,b) -> a + ", " + b).orElse("");
            return new SimpleStringProperty(categoryString);
        });
        colLastViewed.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getLastView() == null ? "" : data.getValue().getLastView().toString()
        ));

        // Load movies i TableView
        loadMovies();
    }

    // =======================================================
    private void loadMovies() {
        List<Movie> movies = movieManager.getAllMovies();

        // Hent kategorier for hver film
        for (Movie movie : movies) {
            movie.setCategories(movieManager.getCategoriesForMovie(movie));
        }

        tblMovies.setItems(FXCollections.observableArrayList(movies));
    }

    // =======================================================
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

    // =======================================================
    @FXML
    private void handleAddMovie() {
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

        // Hent alle kategorier hvor checkbox er markeret
        List<Category> selectedCategories = lstAddCategories.getItems().stream()
                .filter(Category::isSelected)
                .toList();

        // Tilføj filmen med kategorier
        movieManager.addMovie(movie, selectedCategories);

        // Ryd inputfelter og checkboxes
        lstAddCategories.getItems().forEach(c -> c.setSelected(false));
        txtTitle.clear();
        txtDirectors.clear();
        lblFilePath.setText("");

        loadMovies();
    }

    // =======================================================
    @FXML
    private void handleRemoveMovie() {
        Movie selected = tblMovies.getSelectionModel().getSelectedItem();
        if (selected != null) {
            movieManager.deleteMovie(selected);
            loadMovies();
        }
    }

    // =======================================================
    @FXML
    private void handlePlayMovie() {
        loadMovies();
        Movie movie = tblMovies.getSelectionModel().getSelectedItem();
        if (movie == null) return;

        try {
            File file = new File(movie.getFilePath());
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file); // Åbner filmen i standardplayer
            } else {
                System.out.println("Filen findes ikke: " + movie.getFilePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        movieManager.moviePlayed(movie);
    }

    // =======================================================
    @FXML
    private void handleSaveRating() {
        Movie movie = tblMovies.getSelectionModel().getSelectedItem();
        if (movie != null) {
            movieManager.updatePersonalRating(movie, (int) sldPersonalRating.getValue());
            loadMovies();
        }
    }

    // =======================================================
    @FXML
    private void handleClearFilters() {
        loadMovies();
    }
}
