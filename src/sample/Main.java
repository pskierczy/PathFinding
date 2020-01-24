package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;


public class Main extends Application
        implements EventHandler<KeyEvent> {
    final private int WIDTH = 600;
    final private int HEIGHT = 600;
    //    private Group root;
    private Board board;
    private VBox vBox;
    private ToggleGroup toggleGroupMazeOptions;
    private Button butGenerate;
    private Map<EventType<MouseEvent>, CheckBox> MouseEventsDebug;

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
        //root.setOnMouseMoved(this);
//        root.setOnMouseClicked(this);
    }


    void Initialize(Stage stage, Group root) {
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Path Finding");
        stage.setScene(scene);
        stage.show();

        //init main variables
        initControls(root);
//        controlsCount = 0;
        initScene(root);
//        initGame(root);
        initHandlers();

    }

    private void initScene(Group root) {
        board = new Board(21, 20, 20);
        board.setLayoutX(vBox.getLayoutX() + vBox.getWidth() + 20);
        board.setLayoutY(20);
        board.generateRandomWalls(0.25);
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
        butGenerate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ButtonClickHandler(event);
            }
        });
        vBox.getChildren().add(butGenerate);

        toggleGroupMazeOptions = new ToggleGroup();
        RadioButton rbRandom = new RadioButton("Random");
        rbRandom.fire();
        RadioButton rbLines = new RadioButton("Lines");
        rbRandom.setToggleGroup(toggleGroupMazeOptions);

        rbLines.setToggleGroup(toggleGroupMazeOptions);

        vBox.getChildren().addAll(rbRandom, rbLines);

        root.getChildren().add(vBox);
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
        if (toggleGroupMazeOptions.getSelectedToggle().toString().contains("Random"))
            board.generateRandomWalls(0.25);
        else
            board.generateLinedWalls(0.25);
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
