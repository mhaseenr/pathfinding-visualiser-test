package com.jpro.pathfinding;

import com.jpro.webapi.JProApplication;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class PathfindingController {
    final int SIZE = 10;

    Image startImg = new Image(getClass().getResourceAsStream("/com/jpro/pathfinding/img/start_image.png"));
    ImageView startView = new ImageView();
    Image goalImg = new Image(getClass().getResourceAsStream("/com/jpro/pathfinding/img/goal_image.png"));
    ImageView goalView = new ImageView();

    @FXML
    private HBox headerBar;

    @FXML
    private HBox footer;

    @FXML
    public Label messageLabel;

    @FXML
    private Slider gridSizeSlider;

    @FXML
    private Button gridSizeApply;

    @FXML
    private BorderPane primaryBorderPane;

    @FXML
    private ComboBox pathAlgorithmSelection;

    @FXML
    private Button visualiseButton;

    @FXML
    private ComboBox mazeGenerationSelection;

    @FXML
    private Button mazeButton;

    @FXML
    private Button clearGraphButton;

    @FXML
    public StackPane centerPane;

    @FXML
    public GridPane gridPane;
    private int maxRow = Dimensions.getMaxRow();
    private int maxCol = Dimensions.getMaxCol();
    public boolean setStart = false;
    public boolean setGoal = false;
    private String color = "-fx-border-color: rgba(0, 128, 255, 0.5); -fx-background-color: white";

    public com.jpro.pathfinding.AlgorithmHandler algorithmHandler = new com.jpro.pathfinding.AlgorithmHandler(this);
    public AnimationHandler animationHandler = new AnimationHandler(this, algorithmHandler);

    private Tile buttonPressedSource;
    private Tile buttonExitedSource;
    private Tile previousButtonLocation;
    private ArrayList<Tile> listOfButtonsSolidified = new ArrayList<>();
    private ArrayList<Tile> listOfButtonsDesolidified = new ArrayList<>();

    private boolean draggingStart;
    private boolean draggingGoal;
    private JProApplication jProApplication;

    public void init(JProApplication jProApplication) {
        this.jProApplication = jProApplication;
    }

    public void clearGraph() {
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                Tile node = algorithmHandler.grid[row][col];
                node.restart();
                if (!node.solid && !node.start && !node.goal) {
                    try {
                        algorithmHandler.grid[row][col].reset();
                        Tile tile = (Tile) getNodeFromGridPane(row, col);
                        tile.getChildren().clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (!node.solid) {
                    Tile tile = (Tile) getNodeFromGridPane(row, col);
                    if (tile.getChildren() != null) {
                        tile.getChildren().removeIf(child -> child instanceof Rectangle);
                    }
                }
            }
        }
    }
    public void resetGraph() {
        setStart = false;
        setGoal = false;
        draggingStart = false;
        draggingGoal = false;
        buttonPressedSource = null;
        buttonExitedSource = null;
        listOfButtonsSolidified.removeAll(listOfButtonsSolidified);
        listOfButtonsDesolidified.removeAll(listOfButtonsDesolidified);
        Tile.currentTrees = null;
        Tile.currentTrees = new ArrayList<>();
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                Tile node = algorithmHandler.grid[row][col];
                node.reset();
                node.getChildren().clear();
                node.initiateTree();
            }
        }
    }

    @FXML
    public void visualiseAlgorithm() {
        if (setStart && setGoal) {
            messageLabel.setText("");
            clearGraph();
            disable();
            String algorithm = pathAlgorithmSelection.getValue().toString();
            algorithmHandler.algorithmInitiator(algorithm);
        } else {
            if (!setStart) {
                messageLabel.setText("You haven't set the start node!");
            } else {
                messageLabel.setText("You haven't set the goal node!");
            }
        }
    }
    @FXML
    public void mazeGeneration() {
        resetGraph();
        disable();
        String maze = mazeGenerationSelection.getValue().toString();
        algorithmHandler.mazeInitiator(maze);
    }

    @FXML
    public void cleanGraph() {
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                if (!algorithmHandler.grid[row][col].start && !algorithmHandler.grid[row][col].goal) {
                    try {
                        algorithmHandler.grid[row][col].reset();
                        Tile tile = (Tile) getNodeFromGridPane(row, col);
                        tile.getChildren().clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Tile tile = (Tile) getNodeFromGridPane(row, col);
                    if (tile.getChildren() != null) {
                        tile.getChildren().removeIf(child -> child instanceof Rectangle);
                    }
                }
            }
        }
    }

    private Node getNodeFromGridPane(int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return node;
            }
        }
        return null;
    }
    public void initializeBorderPane() {
        primaryBorderPane.setPadding(new Insets(5));
        BorderPane.setMargin(headerBar, new Insets(10));
        BorderPane.setMargin(footer, new Insets(10));
    }

    public void initializeGrid() {
        int index = 0;
        for(int row=0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                Tile tile = new Tile(row, col, index);
                tile.setStyle(color);
                gridPane.add(tile, col, row);
                algorithmHandler.grid[row][col] = tile;
                tile.prefWidthProperty().bind(Bindings.min(centerPane.widthProperty().divide(SIZE),
                        centerPane.heightProperty().divide(SIZE)));
                tile.prefHeightProperty().bind(Bindings.min(centerPane.widthProperty().divide(SIZE),
                        centerPane.heightProperty().divide(SIZE)));
                index++;
            }
        }
    }
    public void initializeImages() {
        startView.setImage(startImg);
        startView.setPreserveRatio(true);
        startView.setViewOrder(-100000);

        goalView.setImage(goalImg);
        goalView.setPreserveRatio(true);
        goalView.setViewOrder(-100000);
    }
    public void dragFunction(MouseEvent event, Node node) {
        try {
            Tile clickedTile;
            if (node instanceof Tile) {
                clickedTile = (Tile) node;
            } else {
                clickedTile = (Tile) node.getParent();
            }
            if (event.getButton() == MouseButton.SECONDARY && !event.isPrimaryButtonDown()) {
                if (clickedTile.solid && !clickedTile.start && !clickedTile.goal && !listOfButtonsSolidified.contains(clickedTile)) {
                    clickedTile.reset();
                    clickedTile.getChildren().clear();
                    listOfButtonsDesolidified.add(clickedTile);
                }
                if (!clickedTile.solid && !clickedTile.start && !clickedTile.goal && !listOfButtonsDesolidified.contains(clickedTile)) {
                    clickedTile.setAsSolid();
                    animationHandler.solidifyAnimation(clickedTile);
                    listOfButtonsSolidified.add(clickedTile);
                }
            }
            if (event.getButton() == MouseButton.PRIMARY && !event.isSecondaryButtonDown()) {
                if (setStart && setGoal) {
                    if (draggingStart) {
                        if (buttonPressedSource.start) {
                            if (!clickedTile.solid) {
                                clickedTile.getChildren().add(startView);
                            }
                        }
                    } else if (draggingGoal) {
                        if (buttonPressedSource.goal) {
                            if (!clickedTile.solid) {
                                clickedTile.getChildren().add(goalView);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {}
    }
    public void releasedFunction(MouseEvent event, Node node) {
        if (node instanceof Tile) {
            buttonExitedSource = (Tile) node;
        } else {
            buttonExitedSource = (Tile) node.getParent();
        }
        if (buttonExitedSource.solid) {
            buttonExitedSource = previousButtonLocation;
        }
        if (draggingStart) {
            buttonPressedSource.reset();
            buttonExitedSource.setAsStart();
            algorithmHandler.startTile = buttonExitedSource;
        } else if (draggingGoal) {
            buttonPressedSource.reset();
            buttonExitedSource.setAsGoal();
            algorithmHandler.goalTile = buttonExitedSource;
        }
        draggingStart = false;
        draggingGoal = false;
        listOfButtonsSolidified.removeAll(listOfButtonsSolidified);
        listOfButtonsDesolidified.removeAll(listOfButtonsDesolidified);
    }
    public void pressedFunction (MouseEvent event, Node node) {
        if (node instanceof Tile) {
            if (((Tile) node).start && setStart && setGoal) {draggingStart = true;}
            if (((Tile) node).goal && setStart && setGoal) {draggingGoal = true;}
            buttonPressedSource = (Tile) node;
        } else {
            if (((Tile) node.getParent()).start && setStart && setGoal) {draggingStart = true;}
            if (((Tile) node.getParent()).goal && setStart && setGoal) {draggingGoal = true;}
            buttonPressedSource = (Tile) node.getParent();
        }
    }
    public void clickFunction(MouseEvent event, Node node) {
        try {
            Tile clickedTile;
            if (node instanceof Tile) {
                clickedTile = (Tile) node;
            } else {
                clickedTile = (Tile) node.getParent();
            }
            if (event.getButton() == MouseButton.SECONDARY && !event.isPrimaryButtonDown()) {
                if (clickedTile.index == buttonPressedSource.index) {
                    if (!clickedTile.solid && !clickedTile.start && !clickedTile.goal) {
                        clickedTile.setAsSolid();
                        animationHandler.solidifyAnimation(clickedTile);
                    } else if (clickedTile.solid) {
                        clickedTile.reset();
                        clickedTile.getChildren().clear();
                    }
                }
            }
            if (event.getButton() == MouseButton.PRIMARY && !event.isSecondaryButtonDown()) {
                if (!setStart && !setGoal && !clickedTile.solid) {
                    startView.setFitWidth(clickedTile.getWidth());
                    algorithmHandler.startTile = clickedTile;
                    clickedTile.setAsStart();
                    clickedTile.getChildren().add(startView);
                    startView.fitHeightProperty().bind(clickedTile.heightProperty());
                    setStart = true;

                } else if (setStart && !setGoal && !clickedTile.start && !clickedTile.solid) {
                    goalView.setFitWidth(clickedTile.getWidth());
                    algorithmHandler.goalTile = clickedTile;
                    clickedTile.setAsGoal();
                    clickedTile.getChildren().add(goalView);
                    goalView.fitHeightProperty().bind(clickedTile.heightProperty());
                    setGoal = true;
                }
            }
        }
        catch (Exception e) {}
    }
    public void initializeGridMouseHandler() {
        startView.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observableValue, Parent parent, Parent t1) {
                previousButtonLocation = (Tile) t1;
            }
        });

        goalView.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observableValue, Parent parent, Parent t1) {
                previousButtonLocation = (Tile) t1;
            }
        });

        gridPane.addEventHandler(MouseEvent.ANY, new MouseHandler(
                dragEvent -> {
                    dragEvent.consume();
                    Node node = dragEvent.getPickResult().getIntersectedNode();
                    dragFunction(dragEvent, node);
                },
                clickEvent -> {
                    clickEvent.consume();
                    Node node = clickEvent.getPickResult().getIntersectedNode();
                    clickFunction(clickEvent, node);
                },
                pressedEvent -> {
                    pressedEvent.consume();
                    Node node = pressedEvent.getPickResult().getIntersectedNode();
                    pressedFunction(pressedEvent, node);
                },
                releasedEvent -> {
                    releasedEvent.consume();
                    Node node = releasedEvent.getPickResult().getIntersectedNode();
                    releasedFunction(releasedEvent, node);
                }
                ));
    }
    public void destroyGrid() {
        gridPane.getChildren().clear();
    }
    public void updateGrid() {
        resetGraph();
        algorithmHandler.graphReset();
        maxRow = Dimensions.getMaxRow();
        maxCol = Dimensions.getMaxCol();
        destroyGrid();
        initializeGrid();
    }
    @FXML
    public void applyNewSize() {
        int newGridSize = (int) gridSizeSlider.getValue();
        Dimensions.setGrid(newGridSize);
        updateGrid();
    }
    public void initializeSlider() {
        final ChangeListener<Number> numberChangeListener = (obs, old, val) -> {
            final double roundedValue = Math.floor(val.doubleValue() / 1) * 1;
            gridSizeSlider.valueProperty().set(roundedValue);
        };
        gridSizeSlider.valueProperty().addListener(numberChangeListener);
    }
    public void initialize() {
        initializeBorderPane();
        initializeSlider();
        initializeImages();
        initializeGrid();
        initializeGridMouseHandler();
    }
    public void enable() {
        centerPane.setDisable(false);
        visualiseButton.setDisable(false);
        clearGraphButton.setDisable(false);
        mazeButton.setDisable(false);
        gridSizeApply.setDisable(false);
        gridSizeSlider.setDisable(false);
    }
    public void disable() {
        centerPane.setDisable(true);
        visualiseButton.setDisable(true);
        clearGraphButton.setDisable(true);
        mazeButton.setDisable(true);
        gridSizeApply.setDisable(true);
        gridSizeSlider.setDisable(true);
    }
}