package com.hirshi001.javafxnetworking;

import com.hirshi001.javafxnetworking.client.Client;
import com.hirshi001.javafxnetworking.client.ClientHandler;
import com.hirshi001.javafxnetworking.client.Question;
import com.hirshi001.javafxnetworking.stages.AnswerButton;
import com.hirshi001.javafxnetworking.stages.TitleLabel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    public Stage stage;
    private Scene startingScene, connectingScene, gameStartingScene, gameScene, winScene, loseScene;
    private Timeline connectingTimeline, startingTimeline, winGameTimeLine, loseGameTimeLine;
    private Client c;

    private AnswerButton b1, b2, b3, b4;
    private Label questionLabel;

    private Question currQuestion;

    private Label yourScoreVal, otherScoreVal;

    @Override
    public void init() throws Exception {
        initStartingScene();
        initConnectingScene();
        initGameStartingScene();
        initGameScene();
        initWinScene();
        initLoseScene();
    }

    private void initStartingScene(){
        BorderPane base = new BorderPane();


        startingScene = new Scene(base);
        Label title = new TitleLabel();

        Button button = new Button("Connect");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    initGameScene();
                    c = new Client(MainApp.this);
                    //ClientHandler.stop();
                    new Thread(() -> ClientHandler.run(c)).start();

                } catch (Exception e){e.printStackTrace();}
                stage.setScene(connectingScene);
                connectingTimeline.play();
            }
        });
        button.setPrefSize(800,200);
        base.setCenter(button);

        base.setTop(title);
        startingScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private void initConnectingScene(){
        BorderPane base = new BorderPane();

        connectingScene = new Scene(base);
        Label title = new TitleLabel();


        Label connectingLabel = new Label();
        connectingLabel.setTextFill(Color.WHITE);
        connectingLabel.setId("connecting-label");
        connectingLabel.setPrefSize(800,200);
        connectingLabel.setAlignment(Pos.CENTER);

        connectingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(connectingLabel.textProperty(), "Connecting")),
                new KeyFrame(Duration.seconds(1), new KeyValue(connectingLabel.textProperty(), "Connecting.")),
                new KeyFrame(Duration.seconds(2), new KeyValue(connectingLabel.textProperty(), "Connecting..")),
                new KeyFrame(Duration.seconds(3), new KeyValue(connectingLabel.textProperty(), "Connecting...")),
                new KeyFrame(Duration.seconds(4), new KeyValue(connectingLabel.textProperty(), "Connecting...."))
        );
        connectingTimeline.setAutoReverse(true);
        connectingTimeline.setCycleCount(Animation.INDEFINITE);

        base.setCenter(connectingLabel);

        base.setTop(title);
        connectingScene.getStylesheets().addAll(
                getClass().getResource("/style.css").toExternalForm(),
                getClass().getResource("/gameconnecting.css").toExternalForm());
    }

    private void initGameStartingScene(){
        BorderPane base = new BorderPane();

        gameStartingScene = new Scene(base);
        Label title = new TitleLabel();

        VBox vbox = new VBox();

        Label connectingLabel = new Label();
        connectingLabel.setId("starting-game-label");
        connectingLabel.setText("GAME STARTING IN");
        connectingLabel.setTextFill(Color.WHITE);
        connectingLabel.setPrefSize(800,200);
        connectingLabel.setAlignment(Pos.CENTER);

        Label countdown = new Label();
        countdown.setId("starting-game-label");
        countdown.setPrefSize(800,200);
        countdown.setTextFill(Color.WHITE);
        countdown.setAlignment(Pos.CENTER);

        startingTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), new KeyValue(countdown.textProperty(),"3")),
                new KeyFrame(Duration.seconds(2), new KeyValue(countdown.textProperty(),"2")),
                new KeyFrame(Duration.seconds(3), new KeyValue(countdown.textProperty(),"1")),
                new KeyFrame(Duration.seconds(4), new KeyValue(countdown.textProperty(),"0"))
        );

        startingTimeline.setAutoReverse(false);
        startingTimeline.setCycleCount(1);

        startingTimeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("FINISHED");
                stage.setScene(gameScene);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                });
            }
        });

        vbox.getChildren().addAll(connectingLabel, countdown);
        vbox.setAlignment(Pos.CENTER);
        base.setCenter(vbox);

        base.setTop(title);
        gameStartingScene.getStylesheets().addAll(
            getClass().getResource("/style.css").toExternalForm(),
            getClass().getResource("/gamestarting.css").toExternalForm());
    }

    private void initGameScene(){
        BorderPane root = new BorderPane();
        gameScene = new Scene(root);
        root.setTop(new TitleLabel());


        VBox questionSection = new VBox();
        questionSection.setPadding(new Insets(100));

        questionLabel = new Label("What is the squareroot of 9?");
        questionLabel.setId("question-label");
        questionLabel.setWrapText(true);

        GridPane answers = new GridPane();
        answers.setId("answers");

        b1 = new AnswerButton(1);
        b2 = new AnswerButton(2);
        b3 = new AnswerButton(3);
        b4 = new AnswerButton(4);

        answers.add(b1,0,0);
        answers.add(b2,1,0);
        answers.add(b3,0,1);
        answers.add(b4,1,1);

        questionSection.getChildren().addAll(questionLabel, answers);
        root.setCenter(questionSection);


        VBox scores = new VBox();
        scores.setPadding(new Insets(50,50,50,0));

        Label yourScore = new Label();
        yourScore.setText("Your Score");
        yourScore.setAlignment(Pos.CENTER);
        yourScoreVal = new Label();
        yourScoreVal.setText("0");
        yourScoreVal.setAlignment(Pos.CENTER);

        Label otherScore = new Label();
        otherScore.setText("Opponent's Score");
        otherScore.setAlignment(Pos.CENTER);
        otherScoreVal = new Label();
        otherScoreVal.setText("0");
        otherScoreVal.setAlignment(Pos.CENTER);


        scores.getChildren().addAll(yourScore, yourScoreVal, otherScore, otherScoreVal);
        root.setRight(scores);

        gameScene.getStylesheets().addAll(
                getClass().getResource("/style.css").toExternalForm(),
                getClass().getResource("/game.css").toExternalForm()
        );
    }

    private void initWinScene(){
        BorderPane root = new BorderPane();
        winScene = new Scene(root);
        root.setTop(new TitleLabel());

        Label winLabel = new Label("YOU WIN!!!!");
        winLabel.setId("win-label");
        winLabel.setTextFill(Color.WHITE);

        winGameTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(winLabel.textFillProperty(), new Color(1,1,1,1))),
                new KeyFrame(Duration.seconds(2), new KeyValue(winLabel.textFillProperty(), new Color(1,1,1,0)))
        );

        winGameTimeLine.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(startingScene);
            }
        });

        root.setCenter(winLabel);

        winScene.getStylesheets().addAll(
                getClass().getResource("/style.css").toExternalForm(),
                getClass().getResource("/win.css").toExternalForm()
        );
    }

    private void initLoseScene(){
        BorderPane root = new BorderPane();
        loseScene = new Scene(root);
        root.setTop(new TitleLabel());

        Label loseLabel = new Label("YOU LOSE!!!!");
        loseLabel.setId("lose-label");
        loseLabel.setTextFill(Color.WHITE);

        loseGameTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(loseLabel.textFillProperty(), new Color(1,1,1,1))),
                new KeyFrame(Duration.seconds(2), new KeyValue(loseLabel.textFillProperty(), new Color(1,1,1,0)))
        );

        loseGameTimeLine.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.setScene(startingScene);
            }
        });

        root.setCenter(loseLabel);

        loseScene.getStylesheets().addAll(
                getClass().getResource("/style.css").toExternalForm(),
                getClass().getResource("/lose.css").toExternalForm()
        );
    }



    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setWidth(1200);
        stage.setHeight(1000);
        stage.centerOnScreen();
        stage.setTitle("Trivia Run");

        stage.setScene(startingScene);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        System.out.println("STOPPING");
        ClientHandler.stop();
    }

    public void startingGame(){
        stage.setScene(gameStartingScene);
        startingTimeline.play();
    }

    public void updateOpponentScore(int score){
        if(score>=0) otherScoreVal.setText(String.valueOf(score));
        else{
            if(score==-1){
                stage.setScene(winScene);
                winGameTimeLine.play();
            }
            else if(score==-2){
                stage.setScene(loseScene);
                loseGameTimeLine.play();
            }
        }
    }

    private void startGame(){
        currQuestion = c.getNextQuestion();
        setQuestion(currQuestion);
        b1.setQuestionId(1);
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                submitAnswer(b1.getQuestionId());
            }
        });
        b2.setQuestionId(2);
        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                submitAnswer(b2.getQuestionId());
            }
        });
        b3.setQuestionId(3);
        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                submitAnswer(b3.getQuestionId());
            }
        });
        b4.setQuestionId(4);
        b4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                submitAnswer(b4.getQuestionId());
            }
        });

    }

    private void setQuestion(Question q){
        questionLabel.setText(q.getQuestion());
        b1.setText(q.getAnswer1());
        b2.setText(q.getAnswer2());
        b3.setText(q.getAnswer3());
        b4.setText(q.getAnswer4());
    }

    private void submitAnswer(int answer){

        if(currQuestion.getCorrectAnswer() == answer){
            System.out.println("correct");
            c.incrementScore();
            c.updateScore();
            yourScoreVal.setText(String.valueOf(c.getScore()));
        }

        if(!c.hasNextQuestion()){
            System.out.println("TEST DONE");
            questionLabel.setText("YOU ARE DONE");
            b1.setNull();
            b2.setNull();
            b3.setNull();
            b4.setNull();
            c.isDone();
            return;
        }
        currQuestion = c.getNextQuestion();
        setQuestion(currQuestion);
    }

    public static void main(String[] args) {
        launch(args);

    }


}