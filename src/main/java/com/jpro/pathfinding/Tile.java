package com.jpro.pathfinding;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;


public class Tile extends Pane implements com.jpro.pathfinding.TileTree, Comparator<Tile> {
    public Tile previousNode = null;
    public String color = "-fx-border-color: rgba(0, 128, 255, 0.5); -fx-background-color: rgba(%d,%d,%d,1)";

    //---------------------------------------------------TREE VARIABLES-------------------------------------------------
    public static ArrayList<Tile> currentTrees = new ArrayList<>();;
    public Tile root;
    public Tile parent;
    public ArrayList<Tile> children = new ArrayList<>();
    public int size;
    //------------------------------------------------------------------------------------------------------------------

    int col;
    int row;
    int gCost;
    int hCost;
    int fCost;
    int index;

    boolean start = false;
    boolean goal = false;
    boolean solid = false;
    boolean open = false;
    boolean checked = false;

    public Tile(int row, int col, int index) {
        this.row = row;
        this.col = col;
        this.index = index;
        initiateTree();
    }


    public void setAsStart() {
        start = true;
    }

    public void setAsGoal() {
        goal = true;
    }

    public void setAsSolid() {
        this.getChildren().clear();
        solid = true;
    }

    public void setAsOpen() {
        open = true;
    }

    public void setAsChecked() {
        checked = true;
    }

    public void reset() {
        start = false;
        goal = false;
        solid = false;
        checked = false;
        open = false;
    }

    public void restart() {
        checked = false;
        open = false;
    }

    @Override
    public int compare(Tile o1, Tile o2) {
        return Integer.compare(o1.fCost, o2.fCost);
    }

    @Override
    public void initiateTree() {
        this.root = this;
        this.parent = null;
        this.size = 1;
        this.children = null;
        this.children = new ArrayList<>();
    }

    public void colourise(Tile tree) {
        for (Tile child: tree.children) {
            child.setStyle(tree.root.color);
        }
    }


    @Override
    public void cascadeConnect(Tile root1, Tile root2) {
        Tile.currentTrees.remove(root2.root);
        root1.children.add(root2);
        Iterator<Tile> iter = root2.children.iterator();
        while (iter.hasNext()) {
            Tile child = iter.next();
            child.root = root1;
            child.setStyle(root2.root.color);
            root1.children.add(child);
            root1.size++;
        }
        root2.root = root1;
        root2.setStyle(root2.root.color);
        root2.parent = root1;
        root1.size++;
        colourise(root1);
    }

    @Override
    public boolean isConnected(Tile tree1, Tile tree2) {
        return tree1.root.index == tree2.root.index;
    }

    @Override
    public void connect(Tile tree1, Tile tree2) {
        if (tree2.root.size <= tree1.root.size) {
            cascadeConnect(tree1.root, tree2.root);
        }
        if (tree2.root.size > tree1.root.size) {
            cascadeConnect(tree2.root, tree1.root);
        }
    }
}