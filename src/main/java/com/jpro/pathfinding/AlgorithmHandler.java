package com.jpro.pathfinding;

import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AlgorithmHandler {
    //-----------------------------------------------ALGORITHM CONSTANTS------------------------------------------------
    private int maxRow = Dimensions.getMaxRow();
    private int maxCol = Dimensions.getMaxCol();
    Tile[][] grid = new Tile[maxRow][maxCol];
    Tile startTile, goalTile, currentTile;
    //    Comparator<vertex> comparator = new vertex(-1,-1,-1);
//    PriorityQueue<vertex> openList = new PriorityQueue<>(10, comparator);
    ArrayList<Tile> checkedList = new ArrayList<>();
    ArrayList<Tile> animatedVertices = new ArrayList<>();
    //    ArrayList<vertex> neighbours = new ArrayList<>();
    ArrayList<Tile> path = new ArrayList<>();
    ArrayList<Tile> openList = new ArrayList<>();

    //-----------------------------------------------ALGORITHM VARIABLES------------------------------------------------
    boolean goalReached = false;
    int step = 0;
    int runningAlgorithm = -1;
    boolean finishedDrawing = false;
    int index = 0;

    //-------------------------------------------------MAZE VARIABLES---------------------------------------------------
    ArrayList<Tile> openNodes = new ArrayList<>();
    boolean colourMade;
    boolean wallMade;
    int runningMaze = -1;
    int connected = 0;


    //-------------------------------------------------OBJECTS NEEDED---------------------------------------------------
    public PathfindingController pathfindingController;
    public AnimationHandler animationHandler = new AnimationHandler(pathfindingController, this);


    //-------------------------------------------------CONSTRUCTOR------------------------------------------------------
    public AlgorithmHandler(PathfindingController pathfindingController) {
        this.pathfindingController = pathfindingController;
    }


    //-------------------------------------------------INITIATION-------------------------------------------------------

    public void algorithmInitiator(String selectedAlgorithm) {
        reset();
        if (selectedAlgorithm.equals("A* Search")) {
            if (pathfindingController.setStart && pathfindingController.setGoal) {
                a_star_setup();
                runningAlgorithm = 4;
                runAlgorithm();
            }
        } else {
            pathfindingController.enable();
        }
    }
    public void runAlgorithm() {
        if (runningAlgorithm == 4) {
            if (!goalReached && step < 1250) {
                a_star_search();
            }
        }
    }

    public void mazeInitiator(String selectedMaze) {
        reset();
        if (selectedMaze.equals("Kruskal's Maze")) {
            runningMaze = 1;
            kruskals_setup();
        }
    }

    public void runMaze() {
        if (runningMaze == 1) {
            kruskals();
        }
    }

    //--------------------------------------------------ALGORITHMS------------------------------------------------------

    //------------------------A*-------------------------//
    private void a_star_setup() {
        currentTile = startTile;
        setCostOnNodes();
    }
    private void setCostOnNodes() {
        for (int row=0; row<maxRow; row++) {
            for (int col=0; col<maxCol; col++) {
                getManhattanCost(grid[row][col]);
            }
        }
    }
    private void getManhattanCost(Tile node) {
        int xDistance = Math.abs(node.col - startTile.col);
        int yDistance = Math.abs(node.row - startTile.row);
        node.gCost = xDistance + yDistance;
        xDistance = Math.abs(node.col - goalTile.col);
        yDistance = Math.abs(node.row - goalTile.row);
        node.hCost = xDistance + yDistance;
        node.fCost = node.gCost + node.hCost;
    }
    public void a_star_search() {
        if (!goalReached && step < 1250) {
            if (currentTile == goalTile) {
                goalReached = true;
                drawPathInitiate();
                return;
            } else {
                animatedVertices.removeAll(animatedVertices);
                int col = currentTile.col;
                int row = currentTile.row;

                currentTile.setAsChecked();
                checkedList.add(currentTile);
                openList.remove(currentTile);

                if (row-1 >= 0) {
                    openNode(grid[row-1][col]);
                }
                if (col-1 >= 0) {
                    openNode(grid[row][col-1]);
                }
                if (row+1 < maxRow) {
                    openNode(grid[row+1][col]);
                }
                if (col+1 < maxCol) {
                    openNode(grid[row][col+1]);
                }
                int bestNodeIndex = 0;
                int bestNodefCost = Integer.MAX_VALUE;
                for (int i = 0; i < openList.size(); i++) {
                    if (openList.get(i).fCost < bestNodefCost) {
                        bestNodeIndex = i;
                        bestNodefCost = openList.get(i).fCost;
                    }
                    else if (openList.get(i).fCost == bestNodefCost) {
                        if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                            bestNodeIndex = i;
                        }
                    }
                }
                currentTile = openList.get(bestNodeIndex);
                animatedVertices.add(currentTile);
                step++;
                animationHandler.algorithmRunAnimation(animatedVertices);
            }
        } else {
            return;
        }
    }
    private void openNode(Tile node) {
        if (!node.open && !node.checked && !node.solid) {
            node.setAsOpen();
            node.previousNode = currentTile;
            openList.add(node);
        }
    }
    /*private void a_star_setup() {
        currentTile = startTile;
        openList.add(currentTile);
        setCostOnNodes();
        startTile.gCost = 0;
        startTile.fCost = h(startTile);
    }

    private void setCostOnNodes() {
        for (int row=0; row<maxRow; row++) {
            for (int col=0; col<maxCol; col++) {
                grid[row][col].gCost = Integer.MAX_VALUE;
                grid[row][col].fCost = Integer.MAX_VALUE;
            }
        }
    }

    private int g(vertex node){
        int xDistance = Math.abs(node.col - startTile.col);
        int yDistance = Math.abs(node.row - startTile.row);
        return xDistance + yDistance;
    }

    private int h(vertex node) {
        int xDistance = Math.abs(node.col - goalTile.col);
        int yDistance = Math.abs(node.row - goalTile.row);
        return xDistance + yDistance;
    }

    private int f(vertex node) { return node.gCost + node.hCost;}

    private int d(vertex start, vertex end) {
        int xDistance = Math.abs(start.col - end.col);
        int yDistance = Math.abs(start.row - end.row);
        return xDistance + yDistance;
    }

    private void addNeighbour(vertex node) {
        try {
            if (!node.solid) {
                neighbours.add(node);
            }
        }catch (Exception e) {}
    }

    private void getNeighbours(vertex node) {
        int row = node.row;
        int col = node.col;
        if (row-1 >= 0) {addNeighbour(grid[row-1][col]);}
        if (col-1 >= 0) {addNeighbour(grid[row][col-1]);}
//        if (col-1 >= 0 && row-1 >= 0) {addNeighbour(grid[row-1][col-1]);}
        if (row+1 < maxRow) {addNeighbour(grid[row+1][col]);}
        if (col+1 < maxCol) {addNeighbour(grid[row][col+1]);}
//        if (col+1 < maxCol && row+1 < maxRow) {addNeighbour(grid[row+1][col+1]);}
//        if (row+1 < maxRow && col-1 >= 0) {addNeighbour(grid[row+1][col-1]);}
//        if (col+1 < maxCol && row-1 >= 0) {addNeighbour(grid[row-1][col+1]);}
    }

    public boolean a_star_search () {
        if (!(openList.isEmpty())) {
            animatedTiles.removeAll(animatedTiles);
            currentTile = openList.peek();
            checkedList.add(currentTile);
            animatedTiles.add(currentTile);
            if (currentTile == goalTile) {
                goalReached = true;
                return goalReached;
            }
            openList.remove(currentTile);
            getNeighbours(currentTile);
            for (vertex neighbour : neighbours) {
                int tentative_g_score = g(currentTile) + d(currentTile, neighbour);
                if (tentative_g_score < neighbour.gCost) {
                    neighbour.parent = currentTile;
                    neighbour.gCost = tentative_g_score;
                    neighbour.fCost = tentative_g_score + h(neighbour);
                    if (!openList.contains(neighbour)) {
                        openList.add(neighbour);
                    }
                }
            }
            neighbours.removeAll(neighbours);
            ParallelTransition anim = prototypeAnimationHandler.algorithmRunAnimation(animatedTiles);
            anim.setOnFinished(evt -> a_star_search());
            anim.play();
        }
        return goalReached;
    }*/

    //----------------------------------------------------MAZES---------------------------------------------------------
    //----------------------KRUSKAL'S-------------------//
    public void kruskals_setup() {
        animatedVertices.removeAll(animatedVertices);
        if (!colourMade) {
            kruskals_colour_up(0,0,0,255,0,0);
        } else {
            if (!wallMade) {
                kruskals_wall_up(true, 0 , 1);
            } else {
                for (int x = 0; x < maxRow; x+=2) {
                    for (int y= 0; y < maxCol; y+=2) {
                        Tile node = grid[x][y];
                        openNodes.add(node);
                        Tile.currentTrees.add(node);
                    }
                }
                Collections.shuffle(openNodes);
                runMaze();
            }
        }

    }
    public void kruskals_colour_up(int direction, int row, int col, int r, int g, int b) {
        Tile tile = grid[row][col];
        if (direction == 0) {
            if (row == maxRow-1) {
                col++;
                direction = 1;
            } else {
                row++;
            }
        } else if (direction == 1) {
            if (row == 0) {
                col++;
                direction = 0;
            } else {
                row--;
            }
        }
        Timeline anim = animationHandler.colourify(tile, direction, row, col, r, g, b);
        anim.playFromStart();
    }
    private void kruskals_wall_up(boolean runningCol, int row, int col) {
        // False -> Running a column || True -> Running a row
        animatedVertices.removeAll(animatedVertices);
        if (runningCol) {
            for (int x = 1; x<maxCol; x+=2) {
                Tile node = grid[row][x];
                if (!node.solid) {
                    node.setAsSolid();
                    animatedVertices.add(node);
                }
            }
            ParallelTransition parallelTransition = animationHandler.drawingWallsAnimation(animatedVertices);
            parallelTransition.playFromStart();
            if (row == maxRow-1) {
                runningCol = false;
                row = 1;
                col = 0;
            } else {
                row++;
            }
            final boolean frunningCol = runningCol;
            final int frow = row;
            final int fcol = col;
            parallelTransition.setOnFinished(actionEvent -> {
                kruskals_wall_up(frunningCol, frow, fcol);
            });
        }
        else {
            for (int x = 1; x<maxRow; x+=2) {
                Tile node = grid[x][col];
                if (!node.solid) {
                    node.setAsSolid();
                    animatedVertices.add(node);
                }
            }
            ParallelTransition parallelTransition = animationHandler.drawingWallsAnimation(animatedVertices);
            parallelTransition.playFromStart();
            if (col == maxCol-1) {
                wallMade = true;
                kruskals_setup();
                return;
            } else {
                col+=2;
            }
            final boolean frunningCol = runningCol;
            final int frow = row;
            final int fcol = col;
            parallelTransition.setOnFinished(actionEvent -> {
                kruskals_wall_up(frunningCol, frow, fcol);
            });
        }
    }
    private void kruskals() {
        if (Tile.currentTrees.size() != 1) {
            iterateKruskals();
        } else {
            setStyleBack();
            pathfindingController.enable();
        }
    }
    private void iterateKruskals() {
        int[] direction = getRandomDirection();
        int x = direction[0];
        int y = direction[1];
        int i = new Random().nextInt(openNodes.size());
        Tile cellA = openNodes.get(i);
        if (!(cellA.row + y < maxRow &&
                cellA.col + x < maxCol &&
                cellA.row + y >= 0 &&
                cellA.col + x >= 0)) {
            runMaze();
            return;
        }
        Tile wall = grid[cellA.row+y][cellA.col+x];
        if (!wall.solid) {
            runMaze();
            return;
        }
        if (!(wall.row + y < maxRow &&
                wall.col + x < maxCol &&
                wall.row + y >= 0 &&
                wall.col + x >= 0)) {
            runMaze();
            return;
        }
        Tile cellB = grid[wall.row+y][wall.col+x];
        if (cellA.isConnected(cellA, cellB)) {
            runMaze();
            return;
        }
        Timeline anim = new Timeline(new KeyFrame(Duration.millis(10),
                evt -> {
                    openWall(wall);
                    cellA.connect(cellA, cellB);
                    cellA.connect(cellA, wall);
                    connected++;
                    runMaze();
                }));
        anim.play();
    }

    //-------------------------------------------------FUNCTIONALITY----------------------------------------------------
    public void drawPathInitiate() {
        while (currentTile != startTile) {
            path.add(currentTile);
            currentTile = currentTile.previousNode;
        }
        path.add(startTile);
        Collections.reverse(path);
        drawPath();
    }
    public void drawPath() {
        if (!finishedDrawing) {
            if (!(index == path.size())) {
                animationHandler.algorithmRecreatePath(path.get(index));
                index++;
            } else {
                pathfindingController.enable();
            }
        }
    }
    private void openWall(Tile wall) {
        wall.getChildren().clear();
        wall.reset();
    }

    private int[] getRandomDirection() {
        int[][] directions = new int[][]{{0,1}, {1,0}, {0,-1}, {-1,0}};
        int x = new Random().nextInt(directions.length);
        int y = x;
        x=0;
        return directions[y];
    }

    private void setStyleBack() {
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                Tile node = grid[row][col];
                node.setStyle("-fx-border-color: rgba(0, 128, 255, 0.5); -fx-background-color: white");
            }
        }
    }

    private void reset() {
        goalReached = false;
        step = 0;
        runningAlgorithm = -1;
        runningMaze = -1;
        currentTile = null;
        index = 0;
        finishedDrawing = false;
        checkedList.removeAll(checkedList);
        openList.removeAll(openList);
        animatedVertices.removeAll(animatedVertices);
        path.removeAll(path);
        openNodes.removeAll(openNodes);
        colourMade = false;
        wallMade = false;
        runningMaze = -1;
        connected = 0;
    }
    public void graphReset() {
        maxRow = Dimensions.getMaxRow();
        maxCol = Dimensions.getMaxCol();
        grid = null;
        grid = new Tile[maxRow][maxCol];
        startTile = null;
        goalTile = null;
        reset();
    }
}