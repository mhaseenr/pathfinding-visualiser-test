package com.jpro.pathfinding;

public interface TileTree {
    public void initiateTree();

    public void cascadeConnect(Tile tree1, Tile tree2);

    public boolean isConnected(Tile tree1, Tile tree2);

    public void connect(Tile tree1, Tile tree2);

}