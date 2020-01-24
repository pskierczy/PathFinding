package sample;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;


public class Board
        extends Group {

    public class Cell
            extends Rectangle {

        private boolean isWall = false;


        public Cell(int x, int y, int width, int height, boolean isWall) {
            super(x, y, width, height);
            setWall(isWall);
            this.setStroke(Color.BLACK);
            this.setStrokeWidth(1);
        }

        public Cell() {
            this(0, 0, 10, 10, false);
        }

        public Cell(int width, int height) {
            this(0, 0, width, height, false);
        }

        public Cell(int width, int height, boolean isWall) {
            this(0, 0, width, height, isWall);
        }

        public Cell(int x, int y, int width, int height) {
            this(x, y, width, height, false);
        }

        public boolean isWall() {
            return isWall;
        }

        public void setWall(boolean wall) {
            isWall = wall;
            this.setFill(isWall ? Color.BLACK : Color.WHITE);
        }

        public double getCenterX() {
            return this.getX() + this.getWidth() / 2.0;
        }

        public double getCenterY() {
            return this.getY() + this.getHeight() / 2.0;
        }
    }

    private int rows, columns, size;
    private Cell[][] grid;
    private Cell start, end;
    private boolean addWall = false;


    public Board(int rows, int cols, int size) {
        this.rows = (rows % 2 == 1 ? rows : rows + 1); //**TO ENSURE ROWS NUMBER IS ODD
        this.columns = cols;
        this.grid = new Cell[rows][cols];
        this.size = size;
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                this.grid[i][j] = new Cell(size * j, size * i, size, size);
                this.grid[i][j].setId(String.valueOf(i * columns + j));
                this.getChildren().add(this.grid[i][j]);
            }
    }

    private void resetWalls() {
        this.getChildren().forEach(grid ->
                ((Cell) grid).setWall(false));
    }

    private void setWallRow(int row, boolean isWall) {
        for (int i = 0; i < columns; i++)
            getItemAt(row, i).setWall(isWall);
    }

    public void generateRandomWalls(double wallPossibility) {
        generateRandomWalls(wallPossibility, System.nanoTime());
    }

    public void generateRandomWalls(double wallPossibility, long seed) {
        Random r = new Random(seed);
        for (int i = 0; i < this.rows; i++)
            for (int j = 0; j < columns; j++) {
                if (i == 0 || i == this.rows - 1) {
                    setWallRow(i, true);
                    fillGaps(i, r.nextInt(columns - 2), 1, columns);
                } else if (i == 1 || i == this.rows - 2)
                    continue;
                else
                    this.setIsWall(i, j, r.nextDouble() < wallPossibility);
            }
        setStart();
        setEnd();
    }


    public void generateLinedWalls(double wallPossibility) {
        generateLinedWalls(wallPossibility, System.nanoTime());
    }

    public void generateLinedWalls(double wallPossibility, long seed) {
        Random r = new Random(seed);
        int nextStart;
        resetWalls();

        for (int i = 0; i < this.rows; i += 2) {
            setWallRow(i, true);
            if (i == 0 || i == this.rows - 1) {
                fillGaps(i, r.nextInt(columns - 2), 1, columns);
            } else {
                nextStart = r.nextInt(columns / 2 - 2);
                do {
                    nextStart = fillGaps(i, nextStart + r.nextInt(columns / 2), r.nextInt(2) + 1, columns);
                } while (nextStart < columns);
            }
        }
        setStart();
        setEnd();
    }

    private int fillGaps(int row, int startColumn, int gapLength, int columnsCount) {
        int validatedGap = Math.min(gapLength, columns - startColumn);
        for (int j = startColumn; j < startColumn + validatedGap; j++)
            this.setIsWall(row, j, false);
        return startColumn + gapLength + 1;
    }

    public Cell getItemAt(int row, int column) {
        return grid[row][column];
    }

    public void setIsWall(int row, int column, boolean isWall) {
        getItemAt(row, column).setWall(isWall);
    }

    public boolean getIsWall(int row, int column) {
        return getItemAt(row, column).isWall();
    }

    private void setStart() {
        for (int j = 0; j < this.columns; j++)
            if (getItemAt(0, j).isWall() == false) {
                start = getItemAt(0, j);
            }
        start.setFill(Color.GREEN);
    }

    private void setEnd() {
        for (int j = 0; j < this.columns; j++)
            if (getItemAt(this.rows - 1, j).isWall() == false) {
                end = getItemAt(this.rows - 1, j);
            }
        end.setFill(Color.BLUE);
    }

    public Cell getStart() {
        return start;
    }

    public Cell getEnd() {
        return end;
    }

    public void setMouseHandler(EventHandler<MouseEvent> eventHandler) {
        for (int i = 1; i < this.rows - 1; i++)
            for (int j = 0; j < this.columns; j++) {
                getItemAt(i, j).setOnMousePressed(eventHandler);
                getItemAt(i, j).setOnMouseEntered(eventHandler);
                getItemAt(i, j).setOnMouseDragEntered(eventHandler);
                getItemAt(i, j).setOnMouseDragged(eventHandler);
                getItemAt(i, j).setOnMousePressed(eventHandler);
                getItemAt(i, j).setOnMouseReleased(eventHandler);
                getItemAt(i, j).setOnMouseDragOver(eventHandler);
                getItemAt(i, j).setOnMouseClicked(eventHandler);
                getItemAt(i, j).setOnMouseExited(eventHandler);
                getItemAt(i, j).setOnDragDetected(eventHandler);
            }
    }

    public void setOnDrag(EventHandler<MouseDragEvent> dragEventEventHandler) {
        for (int i = 1; i < this.rows - 1; i++)
            for (int j = 0; j < this.columns; j++) {
                getItemAt(i, j).setOnMouseDragEntered(dragEventEventHandler);
            }
    }

    public boolean isAddWall() {
        return addWall;
    }

    public void setAddWall(boolean addWall) {
        this.addWall = addWall;
    }
}
