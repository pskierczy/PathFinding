package sample;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathfindingAlgorithms {

    public boolean A_Star(Board board) {
        List<Board.Cell> openSet = new ArrayList<>();

        Map<Board.Cell, Board.Cell> cameFrom = new HashMap<Board.Cell, Board.Cell>();
        Map<Board.Cell, Double> gScore = new HashMap<Board.Cell, Double>();
        Map<Board.Cell, Double> fScore = new HashMap<Board.Cell, Double>();

        Board.Cell current;
        Board.Cell endCell = board.getEnd();
        Double tentative_gScore;

        current = board.getStart();
        openSet.add(current);
        gScore.put(current, 0.0);
        fScore.put(current, calcDistance(current, endCell));

        while (!openSet.isEmpty()) {
            current = getKeyOfMinimum(fScore, openSet);

            if (current == endCell) {
                Platform.runLater(() -> {
                    CreatePath(board, cameFrom, endCell);
                });
                return true;
            }
            openSet.remove(current);

            for (Board.Cell neighbour : board.getNeighbours(current, true)) {

                gScore.putIfAbsent(neighbour, Double.MAX_VALUE);
                fScore.putIfAbsent(neighbour, Double.MAX_VALUE);

                tentative_gScore = gScore.get(current) + calcDistance(current, neighbour);
//                System.out.println(String.format("Current: ID:%1s ROW:%2d COL:%3d gScore=%4f fScore=%5f", current.getId(), current.getRow(), current.getColumn(), gScore.get(current), fScore.get(current)));
//                System.out.println(String.format("+++++Neighbour: ID:%1s ROW:%2d COL:%3d tgScore=%4f fScore= %5f", neighbour.getId(), neighbour.getRow(), neighbour.getColumn(), tentative_gScore, tentative_gScore + calcDistance(neighbour, endCell)));

                if (tentative_gScore < gScore.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentative_gScore);
                    fScore.put(neighbour, tentative_gScore + calcDistance(neighbour, endCell));

                    Platform.runLater(() -> {
                        CreatePath(board, cameFrom, endCell);
                    });


                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
            if (!(current == board.getStart() || current == endCell)) {
                Board.Cell finalCurrent = current;
                Platform.runLater(() -> {
                    finalCurrent.setFill(Color.YELLOW);
                    for (Board.Cell openSetCell : openSet
                    ) {
                        if (openSetCell != endCell)
                            openSetCell.setFill(Color.VIOLET);
                    }
                });
            }
        }

        return false;
    }


    public boolean A_Star_SingleStep(Board board, List<Board.Cell> openSet, Map<Board.Cell, Board.Cell> cameFrom,
                                     Map<Board.Cell, Double> gScore, Map<Board.Cell, Double> fScore, Board.Cell endCell) {

        Double tentative_gScore;
        if (openSet.isEmpty())
            return false;

        Board.Cell current = getKeyOfMinimum(fScore, openSet);

        if (current == endCell) {
            CreatePath(board, cameFrom, endCell);
            return false;
        }
        openSet.remove(current);


        if (!(current == board.getStart() || current == endCell)) {
            current.setFill(Color.YELLOW);
            for (Board.Cell openSetCell : openSet
            ) {
                if (openSetCell != endCell)
                    openSetCell.setFill(Color.VIOLET);
            }
        }

        for (Board.Cell neighbour : board.getNeighbours(current, true)) {
            gScore.putIfAbsent(neighbour, Double.MAX_VALUE);
            fScore.putIfAbsent(neighbour, Double.MAX_VALUE);

            tentative_gScore = gScore.get(current) + calcDistance(current, neighbour);

            if (tentative_gScore < gScore.get(neighbour)) {
                cameFrom.put(neighbour, current);
                gScore.put(neighbour, tentative_gScore);
                fScore.put(neighbour, tentative_gScore + calcDistance(neighbour, endCell));

                CreatePath(board, cameFrom, neighbour);

                if (!openSet.contains(neighbour))
                    openSet.add(neighbour);
            }
        }
        return true;
    }


    //TODO: PATH DRAWING TO BE UPDATED.
    //TODO: REMEMBER ABOUT REMOVAL OF NODES
    private void CreatePath(Board board, Map<Board.Cell, Board.Cell> pathCellsList, Board.Cell current) {
        //Board.Cell finalCurrent = current;
        board.resetPath();
        while (pathCellsList.containsKey(current)) {
            board.addToPath(current, pathCellsList.get(current));
            current = pathCellsList.get(current);
        }
        board.redrawPath();
    }

//    private Board.Cell getKeyOfMinimum(Map<Board.Cell, Double> map) {
//        Double minValue = Double.MAX_VALUE;
//        Board.Cell keyOfMin = null;
//        for (Board.Cell key : map.keySet()) {
//            if (map.get(key) < minValue) {
//                keyOfMin = key;
//                minValue = map.get(key);
//            }
//        }
//        return keyOfMin;
//    }

    private Board.Cell getKeyOfMinimum(Map<Board.Cell, Double> map, List<Board.Cell> openSetCells) {
        Double minValue = Double.MAX_VALUE;
        Board.Cell keyOfMin = null;
        for (Board.Cell openSetNode : openSetCells) {
            if (map.get(openSetNode) < minValue) {
                keyOfMin = openSetNode;
                minValue = map.get(openSetNode);
            }
        }
        return keyOfMin;
    }


    public boolean A_Star_dist_test(Board board) {
        double maxDistanceValue = -1;
        double distance;
        Board.Cell cell;
        //get max value of distance (for first row and first and last column);
        //first row
        int i = 1;
        for (int j = 0; j < board.getColumns(); j++) {
            System.out.println("i=" + i + " j=" + j);
            cell = board.getItemAt(i, j);
            if (!cell.isWall())
                maxDistanceValue = Math.max(maxDistanceValue, calcDistance(cell, board.getEnd()));
        }
        System.out.println("MAX VALUE = " + maxDistanceValue);
        //first and last column
        for (i = 0; i < board.getRows(); i++) {
            System.out.println("i=" + i + " j=" + 0);
            cell = board.getItemAt(i, 0);
            maxDistanceValue = Math.max(maxDistanceValue, calcDistance(cell, board.getEnd()));

            System.out.println("i=" + i + " j=" + (board.getColumns() - 1));
            cell = board.getItemAt(i, board.getColumns() - 1);
            maxDistanceValue = Math.max(maxDistanceValue, calcDistance(cell, board.getEnd()));
        }
        System.out.println("MAX VALUE = " + maxDistanceValue);

        for (i = 1; i < board.getRows() - 1; i++)
            for (int j = 0; j < board.getColumns(); j++) {
                cell = board.getItemAt(i, j);
                if (!(cell.isWall() || board.getStart().equals(cell) || board.getEnd().equals(cell))) {
                    distance = calcDistance(cell, board.getEnd());
                    cell.setFill(new Color(((float) (255 / maxDistanceValue * distance)) / 255.0f, 0, 0, 1.0));
                }
            }

        return false;
    }

    public double calcDistance(Board.Cell current, Board.Cell end, boolean includeDiagonals) {
        if (includeDiagonals)
            return Math.sqrt(Math.pow(end.getCenterX() - current.getCenterX(), 2) + Math.pow(end.getCenterY() - current.getCenterY(), 2));
        else
            return (end.getCenterX() - current.getCenterX()) + (end.getCenterY() - current.getCenterY());
    }

    public double calcDistance(Board.Cell current, Board.Cell end) {
        return calcDistance(current, end, true);
    }

}
