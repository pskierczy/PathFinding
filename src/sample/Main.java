package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main extends Application
        implements EventHandler<KeyEvent> {
    final private int WIDTH = 600;
    final private int HEIGHT = 600;
    //    private Group root;
    final private double WALL_CHANCE = 0.4;
    private Board board;
    private VBox vBox;
    private ToggleGroup toggleGroupMazeOptions;
    private Button butGenerate;
    //private Map<EventType<MouseEvent>, CheckBox> MouseEventsDebug;
    PathfindingAlgorithms pathfindingAlgorithms = new PathfindingAlgorithms();
    AnimationTimer animationTimer;

    List<Board.Cell> openSet = new ArrayList<>();

    Map<Board.Cell, Board.Cell> cameFrom = new HashMap<Board.Cell, Board.Cell>();
    Map<Board.Cell, Double> gScore = new HashMap<Board.Cell, Double>();
    Map<Board.Cell, Double> fScore = new HashMap<Board.Cell, Double>();

    Board.Cell current;
    Board.Cell endCell;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Initialize(primaryStage, root);

        root.setFocusTraversable(true);
        root.requestFocus();
        root.setOnKeyReleased(this);

        initSolverVariables();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Update();
            }
        };


        //root.setOnMouseMoved(this);
//        root.setOnMouseClicked(this);
    }

    private void Update() {

        if (pathfindingAlgorithms.A_Star_SingleStep(board, openSet, cameFrom, gScore, fScore, board.getEnd()) == false)
            animationTimer.stop();
//        if (current == null)
//            animationTimer.stop();
//        if (current == endCell)
//            animationTimer.stop();
//        if (openSet.isEmpty())
//            animationTimer.stop();
    }

    private void initSolverVariables() {
        openSet.clear();
        cameFrom.clear();
        gScore.clear();
        fScore.clear();

        current = board.getStart();
        endCell = board.getEnd();

        openSet.add(current);
        gScore.put(current, 0.0);
        fScore.put(current, pathfindingAlgorithms.calcDistance(current, endCell));
    }

    void Initialize(Stage stage, Group root) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Path Finding");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        //init main variables
        initControls(root);
//        controlsCount = 0;
        initScene(root);
//        initGame(root);
        initHandlers();

    }

    private void initScene(Group root) {
        board = new Board(49 * 4, 85 * 4, 5);
        board.setLayoutX(vBox.getLayoutX() + vBox.getWidth() + 20);
        board.setLayoutY(20);
        board.generateRandomWalls(WALL_CHANCE);
        root.getChildren().add(board);
    }

    private void initControls(Group root) {
        //
        vBox = new VBox();
        vBox.setLayoutX(20);
        vBox.setLayoutY(20);
        vBox.setSpacing(5);

        //Controls inside Vbox
        butGenerate = new Button("Generate Maze");
        butGenerate.setOnAction(event -> ButtonClickHandler(event));
        vBox.getChildren().add(butGenerate);

        toggleGroupMazeOptions = new ToggleGroup();
        RadioButton rbRandom = new RadioButton("Random");
        rbRandom.fire();
        RadioButton rbLines = new RadioButton("Lines");
        rbRandom.setToggleGroup(toggleGroupMazeOptions);

        rbLines.setToggleGroup(toggleGroupMazeOptions);

        vBox.getChildren().addAll(rbRandom, rbLines);

        root.getChildren().add(vBox);

        Button butSolve = new Button("Solve");
        butSolve.setOnAction(event -> ButtonClickHandler(event));
        vBox.getChildren().add(butSolve);


        //**for the sake of getting proper dimensional data for vbox
        root.applyCss();
        root.layout();
    }

    //**handlers
    @Override
    public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.F5)
            butGenerate.fire();
    }

    private void initHandlers() {
        this.board.setMouseHandler(this::MouseHandler);
        this.board.setOnDrag(this::DragEventHandler);
    }

    private void ButtonClickHandler(ActionEvent buttonAction) {
        if (buttonAction.getSource().toString().contains("Solve")) {
            animationTimer.start();
        } else {
            if (toggleGroupMazeOptions.getSelectedToggle().toString().contains("Random"))
                board.generateRandomWalls(WALL_CHANCE);
            else
                board.generateLinedWalls(WALL_CHANCE);

            initSolverVariables();
        }
    }

    private void MouseHandler(MouseEvent mouseEvent) {

        if (mouseEvent.getSource() instanceof Board.Cell) {
            Board.Cell sender = (Board.Cell) mouseEvent.getSource();
            Board.Cell target = (Board.Cell) mouseEvent.getTarget();

            //System.out.println(mouseEvent.getEventType() + "====" + sender.getId() + " targ:" + target.getId());

            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && mouseEvent.getButton() == MouseButton.PRIMARY) {
                board.setAddWall(!sender.isWall());
                sender.setWall(board.isAddWall());
            }
            if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED) {
                sender.startFullDrag();
                System.out.println("***********FULL DRAG START*************");
            }
            //mouseEvent.consume();
        }
        //mouseEvent.consume();
    }

    private void DragEventHandler(MouseDragEvent dragEvent) {
//        System.out.println(dragEvent.getEventType()
//                + "====gsource" + ((Board.Cell) dragEvent.getGestureSource()).getId()
//                + " source:" + ((Board.Cell) dragEvent.getSource()).getId()
//                + " targ:" + ((Board.Cell) dragEvent.getTarget()).getId());
        if (dragEvent.getSource() instanceof Board.Cell) {
            Board.Cell sender = (Board.Cell) dragEvent.getSource();
            if (dragEvent.getEventType() == MouseDragEvent.MOUSE_DRAG_ENTERED) {
                sender.setWall(board.isAddWall());
            }
            if (dragEvent.getEventType() == MouseDragEvent.MOUSE_DRAG_RELEASED) ;
            //sender.drag
        }
    }

}
