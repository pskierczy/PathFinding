package sample;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class Board
        extends Group {

    public class Cell
            extends Rectangle {

        private boolean isWall = false;
        private int row, column;

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

        private void setRow(int row) {
            this.row = row;
        }

        private void setColumn(int column) {
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
    }

    private int rows, columns, size;
    private Cell[][] grid;
    private Cell start, end;
    private boolean addWall = false;
    private List<Line> path = new ArrayList<>();
    private int maxID = 0;

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
                this.grid[i][j].setRow(i);
                this.grid[i][j].setColumn(j);
                this.grid[i][j].setId(String.valueOf(i * columns + j));
                this.getChildren().add(this.grid[i][j]);
            }
        maxID = (rows - 1) * columns + (columns - 1);
        this.getChildren().addAll(this.path);
    }

    private void resetWalls() {
        this.getChildren().forEach(grid ->
                ((Cell) grid).setWall(false));
    }

    private void setWallRow(int row, boolean isWall) {
        for (int i = 0; i < columns; i++)
            getItemAt(row, i).setWall(isWall);
    }

    public Cell[][] getGrid() {
        return grid;
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
                    setWallRow(i, false);
                else
                    this.setIsWall(i, j, r.nextDouble() < wallPossibility);
            }
        setStart();
        setEnd();
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
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
        try {
            return grid[row][column];
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
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
//                getItemAt(i, j).setOnMouseEntered(eventHandler);
                getItemAt(i, j).setOnMouseDragEntered(eventHandler);
                getItemAt(i, j).setOnMouseDragged(eventHandler);
                getItemAt(i, j).setOnMousePressed(eventHandler);
                getItemAt(i, j).setOnMouseReleased(eventHandler);
                getItemAt(i, j).setOnMouseDragOver(eventHandler);
//                getItemAt(i, j).setOnMouseClicked(eventHandler);
//                getItemAt(i, j).setOnMouseExited(eventHandler);
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

    public void resetPath() {
        this.path = new ArrayList<>();
        int childrenCount = this.getChildren().size();
        for (int i = maxID; i < childrenCount; i++)
            this.getChildren().remove(i);
    }

    public void addToPath(double x0, double y0, double x1, double y1) {
        Line line = new Line(x0, y0, x1, y1);
        line.setStrokeWidth(2);
        line.setStroke(Color.DARKGREEN);
        this.path.add(line);
    }

    public void addToPath(Cell beginCell, Cell endCell) {
        addToPath(beginCell.getCenterX(), beginCell.getCenterY(), endCell.getCenterX(), endCell.getCenterY());
    }

    public void redrawPath() {
        //this.resetPath();
        this.getChildren().addAll(this.path);
    }

    private Cell getNeighbour_Top(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow() - 1, cell.getColumn());
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_Left(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow(), cell.getColumn() - 1);
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_Right(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow(), cell.getColumn() + 1);
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_Bottom(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow() + 1, cell.getColumn());
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_TopLeft(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow() - 1, cell.getColumn() - 1);
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_TopRight(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow() - 1, cell.getColumn() + 1);
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_BottomLeft(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow() + 1, cell.getColumn() - 1);
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }

    private Cell getNeighbour_BottomRight(Cell cell) {
        Cell neighbour = this.getItemAt(cell.getRow() + 1, cell.getColumn() + 1);
        if (neighbour == null)
            return null;
        return (neighbour.isWall() ? null : neighbour);
    }


    /**
     * @param cell
     * @param diagonal x-selected cell
     *                 1-8 neighbours numbers
     *                 |5 1 6
     *                 |2 x 3
     *                 |7 4 8
     * @return
     */
    public Cell[] getNeighbours(Cell cell, boolean diagonal) {
        //int row, column;
        List<Cell> neighbours = new ArrayList<>();
        neighbours.add(this.getNeighbour_Top(cell));
        neighbours.add(this.getNeighbour_Left(cell));
        neighbours.add(this.getNeighbour_Right(cell));
        neighbours.add(this.getNeighbour_Bottom(cell));

        if (diagonal) {
            neighbours.add(this.getNeighbour_TopLeft(cell));
            neighbours.add(this.getNeighbour_TopRight(cell));
            neighbours.add(this.getNeighbour_BottomLeft(cell));
            neighbours.add(this.getNeighbour_BottomRight(cell));
        }
        neighbours.removeIf(Objects::isNull);

        if (neighbours.size() > 0)
            return neighbours.toArray(new Cell[neighbours.size()]);
        else
            return null;
    }
}
