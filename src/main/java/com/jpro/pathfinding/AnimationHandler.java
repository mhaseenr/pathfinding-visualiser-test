package com.jpro.pathfinding;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class AnimationHandler {
    com.jpro.pathfinding.PathfindingController pathfindingController;
    com.jpro.pathfinding.AlgorithmHandler algorithmHandler;

    Color startColor = Color.rgb(0, 255, 208, 0.9);
    Color rectColor = Color.rgb(3, 144, 252, 0.9);
    Color finishedCheckingColor = Color.rgb(76, 245, 146, 1);
    Color drawingColor = Color.rgb(255, 188, 33,1);
    int viewOrder = -10;

    public AnimationHandler(com.jpro.pathfinding.PathfindingController pathfindingController, com.jpro.pathfinding.AlgorithmHandler algorithmHandler) {
        this.pathfindingController = pathfindingController;
        this.algorithmHandler = algorithmHandler;
    }

    public void algorithmRecreatePath(Tile tilex) {
        ParallelTransition parallelTransition = new ParallelTransition();
        tilex.getChildren().removeIf(e -> e instanceof Rectangle);
        Timeline anim = drawingAnimation(tilex);
        parallelTransition.getChildren().add(anim);
        parallelTransition.getChildren().add(new PauseTransition(Duration.seconds(0.1)));
        viewOrder-=1;
        parallelTransition.play();
    }
    public Timeline drawingAnimation(Tile tile) {
        Bounds bounds = tile.getBoundsInParent();
        Rectangle rect = new Rectangle();
        rect.setWidth(bounds.getWidth());
        rect.setHeight(bounds.getHeight());

        Rectangle outerRect = new Rectangle();
        outerRect.setWidth(bounds.getWidth()+10);
        outerRect.setHeight(bounds.getHeight()+10);
        outerRect.setX((bounds.getWidth()/2)-(outerRect.getHeight()/2));
        outerRect.setY((bounds.getHeight()/2)-(outerRect.getHeight()/2));

        Rectangle animRect = new Rectangle(rect.getX() + 0.5*rect.getWidth(), rect.getY() + 0.5*rect.getHeight());
        animRect.setX(rect.getX() + 0.5 * rect.getWidth());
        animRect.setY(rect.getY() + 0.5 * rect.getHeight());

        tile.getChildren().add(animRect);
        animRect.setClip(outerRect);
        tile.setViewOrder(-4);
        animRect.setViewOrder(viewOrder);

        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animRect.fillProperty(), drawingColor),
                        new KeyValue(animRect.heightProperty(), 0),
                        new KeyValue(animRect.widthProperty(), 0),
                        new KeyValue(animRect.arcHeightProperty(), animRect.getHeight()),
                        new KeyValue(animRect.arcWidthProperty(), animRect.getWidth())),
                new KeyFrame(Duration.seconds(0.05),
                        evt -> {
                            if (!algorithmHandler.finishedDrawing) {
                                algorithmHandler.drawPath();
                            }
                        }),
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(animRect.fillProperty(), drawingColor),
                        new KeyValue(animRect.heightProperty(), rect.getHeight()),
                        new KeyValue(animRect.widthProperty(), rect.getWidth()),
                        new KeyValue(animRect.arcHeightProperty(), 0),
                        new KeyValue(animRect.arcWidthProperty(), 0),
                        new KeyValue(animRect.xProperty(), rect.getX()),
                        new KeyValue(animRect.yProperty(), rect.getY())),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(animRect.fillProperty(), drawingColor),
                        new KeyValue(animRect.heightProperty(), outerRect.getHeight()),
                        new KeyValue(animRect.widthProperty(), outerRect.getWidth()),
                        new KeyValue(animRect.xProperty(), outerRect.getX()),
                        new KeyValue(animRect.yProperty(), outerRect.getY())),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(animRect.fillProperty(), drawingColor),
                        new KeyValue(animRect.heightProperty(), rect.getHeight()),
                        new KeyValue(animRect.widthProperty(), rect.getWidth()),
                        new KeyValue(animRect.xProperty(), rect.getX()),
                        new KeyValue(animRect.yProperty(), rect.getY()))
        );
    }

    public Timeline colourify(Tile tileToAnimate, int direction, int row, int col, int r, int g, int b) {
//        Color rgb = Color.rgb(r, g, b, 1);
        String rgb = String.format("-fx-stroke: rgba(0, 128, 255, 0.5); -fx-fill: rgba(%s,%s,%s,1)", r,g,b);
        String style = String.format("-fx-border-color: rgba(0, 128, 255, 0.5); -fx-background-color: rgba(%s,%s,%s,1)", r,g,b);
        tileToAnimate.color = style;
        Timeline anim = shadeNode(tileToAnimate, rgb, style, direction, row, col);
        if (r == 255 && g == 0 && b == 0) {
            g+=5;
        } else if (r == 255 && g < 255 && b == 0) {
            g+=5;
        } else if (r == 255 && g == 255 && b == 0) {
            r-=5;
        } else if (r > 0 && g == 255 && b == 0) {
            r-=5;
        } else if (r == 0 && g == 255 && b == 0) {
            b+=5;
        } else if (r == 0 && g == 255 && b < 255) {
            b+=5;
        } else if (r == 0 && g == 255 && b == 255) {
            g-=5;
        } else if (r == 0 && g > 0 && b == 255) {
            g-=5;
        } else if (r == 0 && g == 0 && b == 255) {
            r+=5;
        } else if (r < 255 && g == 0 && b == 255) {
            r+=5;
        } else if (r == 255 && g == 0 && b == 255) {
            b-=5;
        } else if (r == 255 && g == 0 && b > 0) {
            b-=5;
        }

        int finalR = r;
        int finalG = g;
        int finalB = b;

        anim.getKeyFrames().add(new KeyFrame(Duration.millis(5), actionEvent -> {
            if (col == com.jpro.pathfinding.Dimensions.maxCol && ((direction == 0 && row == 0) || (direction == 1 && row == com.jpro.pathfinding.Dimensions.maxRow-1))) {
                return;
            } else {
                algorithmHandler.kruskals_colour_up(direction, row, col, finalR, finalG, finalB);
            }
        }));
        viewOrder -= 1;
        return anim;
    }
    public Timeline shadeNode(Tile tile, String endColor, String style, int direction, int row, int col) {
        Bounds bounds = tile.getBoundsInParent();
        Rectangle rect = new Rectangle();
        rect.setWidth(bounds.getWidth());
        rect.setHeight(bounds.getHeight());

        Rectangle outerRect = new Rectangle();
        outerRect.setWidth(bounds.getWidth()+10);
        outerRect.setHeight(bounds.getHeight()+10);
        outerRect.setX((bounds.getWidth()/2)-(outerRect.getHeight()/2));
        outerRect.setY((bounds.getHeight()/2)-(outerRect.getHeight()/2));

        Rectangle animRect = new Rectangle(rect.getX() + 0.5*rect.getWidth(), rect.getY() + 0.5*rect.getHeight());
        animRect.setX(rect.getX() + 0.5 * rect.getWidth());
        animRect.setY(rect.getY() + 0.5 * rect.getHeight());
        animRect.setStyle(endColor);

        tile.getChildren().add(animRect);
        animRect.setClip(outerRect);
        tile.setViewOrder(-4);
        animRect.setViewOrder(viewOrder);

        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animRect.heightProperty(), 0),
                        new KeyValue(animRect.widthProperty(), 0),
                        new KeyValue(animRect.arcHeightProperty(), animRect.getHeight()),
                        new KeyValue(animRect.arcWidthProperty(), animRect.getWidth())),
                new KeyFrame(Duration.seconds(0.25),
//                        new KeyValue(animRect.fillProperty(), endColor),
                        new KeyValue(animRect.styleProperty(), endColor),
                        new KeyValue(animRect.heightProperty(), rect.getHeight()),
                        new KeyValue(animRect.widthProperty(), rect.getWidth()),
                        new KeyValue(animRect.arcHeightProperty(), 0),
                        new KeyValue(animRect.arcWidthProperty(), 0),
                        new KeyValue(animRect.xProperty(), rect.getX()),
                        new KeyValue(animRect.yProperty(), rect.getY())),
                new KeyFrame(Duration.seconds(0.5),
//                        new KeyValue(animRect.fillProperty(), endColor),
                        new KeyValue(animRect.styleProperty(), endColor),
                        new KeyValue(animRect.heightProperty(), outerRect.getHeight()),
                        new KeyValue(animRect.widthProperty(), outerRect.getWidth()),
                        new KeyValue(animRect.xProperty(), outerRect.getX()),
                        new KeyValue(animRect.yProperty(), outerRect.getY())),
                new KeyFrame(Duration.seconds(0.75),
                        new KeyValue(animRect.styleProperty(), endColor),
//                        new KeyValue(animRect.fillProperty(), endColor),
                        new KeyValue(animRect.heightProperty(), rect.getHeight()),
                        new KeyValue(animRect.widthProperty(), rect.getWidth()),
                        new KeyValue(animRect.xProperty(), rect.getX()),
                        new KeyValue(animRect.yProperty(), rect.getY())),
                new KeyFrame(Duration.seconds(1),
                        evt -> {
                            tile.getChildren().clear();
                            if (col == com.jpro.pathfinding.Dimensions.maxCol && ((direction == 0 && row == 0) || (direction == 1 && row == com.jpro.pathfinding.Dimensions.maxRow-1))) {
                                algorithmHandler.colourMade = true;
                                algorithmHandler.kruskals_setup();
                            }
                        },
                        new KeyValue(tile.styleProperty(), style))
        );
    }

    public ParallelTransition drawingWallsAnimation(ArrayList<Tile> tilesToAnimate) {
        ParallelTransition parallelTransition = new ParallelTransition();

        for (Tile x : tilesToAnimate) {
            x.getChildren().clear();
            Timeline anim = concurrentSolidify(x);
            parallelTransition.getChildren().add(anim);
            viewOrder -= 1;
        }

        return parallelTransition;
    }
    public Timeline concurrentSolidify(Tile tile) {
        Bounds bounds = tile.getBoundsInParent();

        Rectangle rect = new Rectangle();
        rect.setWidth(bounds.getWidth());
        rect.setHeight(bounds.getHeight());

        Rectangle rec = new Rectangle(0, 0);
        rec.setX(rect.getX() + 0.5 * rect.getWidth());
        rec.setY(rect.getY() + 0.5 * rect.getHeight());
        rec.setClip(rect);

        tile.getChildren().add(rec);

        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(rec.fillProperty(), Color.web("#212f38")),
                        new KeyValue(rec.heightProperty(), 0),
                        new KeyValue(rec.widthProperty(), 0)),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(rec.fillProperty(), Color.web("#212f38")),
                        new KeyValue(rec.heightProperty(), rect.getHeight()),
                        new KeyValue(rec.widthProperty(), rect.getWidth()),
                        new KeyValue(rec.xProperty(), rect.getX()),
                        new KeyValue(rec.yProperty(), rect.getY()))
        );
    }

    public void solidifyAnimation(Tile tile) {
        Bounds bounds = tile.getBoundsInParent();

        Rectangle rect = new Rectangle();
        rect.setWidth(bounds.getWidth());
        rect.setHeight(bounds.getHeight());

        Rectangle rec = new Rectangle(0, 0);
        rec.setX(rect.getX() + 0.5 * rect.getWidth());
        rec.setY(rect.getY() + 0.5 * rect.getHeight());
        rec.setClip(rect);

        tile.getChildren().add(rec);

        Timeline animation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(rec.fillProperty(), Color.web("#212f38")),
                        new KeyValue(rec.heightProperty(), 0),
                        new KeyValue(rec.widthProperty(), 0)),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(rec.fillProperty(), Color.web("#212f38")),
                        new KeyValue(rec.heightProperty(), rect.getHeight()),
                        new KeyValue(rec.widthProperty(), rect.getWidth()),
                        new KeyValue(rec.xProperty(), rect.getX()),
                        new KeyValue(rec.yProperty(), rect.getY()))
        );
        animation.playFromStart();
    }

    public void algorithmRunAnimation(ArrayList<Tile> tilesToAnimate) {
        ParallelTransition parallelTransition = new ParallelTransition();

        for (Tile x : tilesToAnimate) {
            Timeline anim = checkingAnimation(x);
            parallelTransition.getChildren().add(anim);
            viewOrder-=1;
        }
        parallelTransition.getChildren().add(new PauseTransition(Duration.seconds(0.1)));
        parallelTransition.play();

    }
    public Timeline checkingAnimation(Tile tile) {
        Bounds bounds = tile.getBoundsInParent();
        Rectangle rect = new Rectangle();
        rect.setWidth(bounds.getWidth());
        rect.setHeight(bounds.getHeight());

        Rectangle outerRect = new Rectangle();
        outerRect.setWidth(bounds.getWidth()+10);
        outerRect.setHeight(bounds.getHeight()+10);
        outerRect.setX((bounds.getWidth()/2)-(outerRect.getHeight()/2));
        outerRect.setY((bounds.getHeight()/2)-(outerRect.getHeight()/2));

        Rectangle animRect = new Rectangle(rect.getX() + 0.5*rect.getWidth(), rect.getY() + 0.5*rect.getHeight());
        animRect.setX(rect.getX() + 0.5 * rect.getWidth());
        animRect.setY(rect.getY() + 0.5 * rect.getHeight());

        tile.getChildren().add(animRect);
        animRect.setClip(outerRect);
        tile.setViewOrder(-4);
        animRect.setViewOrder(viewOrder);

        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(animRect.fillProperty(), startColor),
                        new KeyValue(animRect.heightProperty(), 0),
                        new KeyValue(animRect.widthProperty(), 0),
                        new KeyValue(animRect.arcHeightProperty(), animRect.getHeight()),
                        new KeyValue(animRect.arcWidthProperty(), animRect.getWidth())),
                new KeyFrame(Duration.millis(20),
                        evt -> {
                            if (!algorithmHandler.goalReached) {
                                algorithmHandler.runAlgorithm();
                            }}
                        ),
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(animRect.fillProperty(), rectColor),
                        new KeyValue(animRect.heightProperty(), rect.getHeight()),
                        new KeyValue(animRect.widthProperty(), rect.getWidth()),
                        new KeyValue(animRect.arcHeightProperty(), 0),
                        new KeyValue(animRect.arcWidthProperty(), 0),
                        new KeyValue(animRect.xProperty(), rect.getX()),
                        new KeyValue(animRect.yProperty(), rect.getY())),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(animRect.fillProperty(), finishedCheckingColor),
                        new KeyValue(animRect.heightProperty(), outerRect.getHeight()),
                        new KeyValue(animRect.widthProperty(), outerRect.getWidth()),
                        new KeyValue(animRect.xProperty(), outerRect.getX()),
                        new KeyValue(animRect.yProperty(), outerRect.getY())),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(animRect.fillProperty(), rectColor),
                        new KeyValue(animRect.heightProperty(), rect.getHeight()),
                        new KeyValue(animRect.widthProperty(), rect.getWidth()),
                        new KeyValue(animRect.xProperty(), rect.getX()),
                        new KeyValue(animRect.yProperty(), rect.getY()))
        );
    }

}
