package ma.ensaj.dem1.view;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;

import javafx.geometry.Side;
import javafx.scene.*;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.util.Duration;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Client extends Application {

    private static final String OUTSIDE_TEXT = "Outside Label";
    double delta = 0.07;

    Image ndpic = new Image("nd.png");

    Image jet = new Image("jet.png");

    Image plane1 = new Image("plane5.jpg");

    ImageView pl2 = new ImageView(jet);

    Image plane2 = new Image("tanker3.png");
    Image plane3 = new Image("tanker.png");
    ImageView pl = new ImageView(plane2);

    Image tc = new Image("tcas.png");

    Image split = new Image("pfd4.jpg");
    Image indic = new Image("wing8.png");
    String filePath = "Airplane+8.mp3";
    String filePath2 = "warning.mp3";

    Media media = new Media(new File(filePath).toURI().toString());
    Media media2 = new Media(new File(filePath2).toURI().toString());
    Media media3 = new Media(new File(filePath2).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    MediaPlayer warning = new MediaPlayer(media2);
    MediaPlayer speed = new MediaPlayer(media2);

    MediaPlayer heat = new MediaPlayer(media2);

    double distance = 0.0;

    private static final int WIDTH = 80;
    private static final int HEIGHT = 900;
    private static final int CENTER_X = WIDTH / 2;
    private static final int CENTER_Y = HEIGHT / 2;
    private static final int TICK_LENGTH = 10;
    private static final int TEXT_PADDING = 1;
    private static final Duration DURATION = Duration.millis(0.7);

    private static final int Translation = 400;

    int counter = 1;
    int distance2 =10000;
    int helper = 0;
    int helper2 = 0;

    private boolean isAuto = false;

    double X;
    double Y;
    double dx;
    double dy;


    String autoPilotAudioFile = "autopilot.wav";
    String manualFile = "manual.wav";
    String refuellingFile = "fuel.wav";


    Media autoPilot = new Media(new File(autoPilotAudioFile).toURI().toString());
    Media manual = new Media(new File(manualFile).toURI().toString());
    Media refuel = new Media(new File(refuellingFile).toURI().toString());

    MediaPlayer autoPilotPlayer = new MediaPlayer(autoPilot);
    MediaPlayer manualPlayer = new MediaPlayer(manual);
    MediaPlayer refuelPlayer = new MediaPlayer(refuel);

    ScrollPane scp = createScrollPane(createImageView(split));

    private Circle otherAircraft = new Circle(400, 150, 5, Color.GREEN);

    Node needPerc1, needPerc2, needHeat1, needHeat2;

    TranslateTransition translate = new TranslateTransition(Duration.seconds(1), otherAircraft);

    TranslateTransition gloTrans, gloTrans2;

    Line nee, nee2;
    Radar radar = new Radar();



    Double dist;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private TextArea textArea;





    private Robot robot = new Robot();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        connectToServer();
        final Label pfd = new Label();
        final Label scale = new Label();
        final Label scale2 = new Label();
        final Label monitored = new Label();

        VBox root = new VBox(10);
        Scene scene = new Scene(root);


        monitored.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 1100;-fx-min-height: 450;-fx-cursor-size: 100px;");
        //monitored.setCursor(new ImageCursor(plane1));
        monitored.setCursor(Cursor.NONE);

        pl.setFitWidth(200); // Replace 200 with your desired width
        pl.setFitHeight(150);


        pl2.setFitWidth(100); // Replace 200 with your desired width
        pl2.setFitHeight(50);


        Pane planes = new Pane(pl, pl2);

        planes.setStyle("-fx-border-color: red; -fx-min-width: 1100; -fx-max-width: 1100; -fx-min-height: 450; -fx-max-height: 450");
        pl.setTranslateY(300);

        monitored.setGraphic(planes);



        // Scale line
        Line scaleLine = new Line(CENTER_X, CENTER_Y, CENTER_X, CENTER_Y - 400);
        scaleLine.setStrokeWidth(3);
        scaleLine.setStroke(Color.BLACK);

        Line scaleLine2 = new Line(CENTER_X, CENTER_Y, CENTER_X, CENTER_Y - 400);
        scaleLine2.setStrokeWidth(3);
        scaleLine2.setStroke(Color.BLACK);

        // Needle
        Line needle = new Line(CENTER_X, 100, CENTER_X, 0);
        needle.setStroke(Color.RED);
        needle.setStrokeWidth(4);
        needle.setRotate(90);
        needle.setTranslateY(Translation);

        nee = needle;


        // Needle
        Line needle2 = new Line(CENTER_X, 100, CENTER_X, 0);
        needle2.setStroke(Color.RED);
        needle2.setStrokeWidth(4);
        needle2.setRotate(90);
        needle2.setTranslateY(Translation);

        nee2 = needle2;



        // Tick marks and labels
        Pane tickPane = new Pane();
        for (int i = -400; i <= 0; i += 20) {
            Line tick = new Line(CENTER_X - TICK_LENGTH / 2, CENTER_Y + i, CENTER_X + TICK_LENGTH / 2, CENTER_Y + i);
            tickPane.getChildren().addAll(tick);


            if (i % 100 == 0) {
                Text label = new Text(Integer.toString(-i*10)+" m/s");
                label.setFont(Font.font(15));
                label.setX(CENTER_X + TICK_LENGTH + TEXT_PADDING);
                label.setY(CENTER_Y + i + TEXT_PADDING);
                tickPane.getChildren().add(label);
            }
        }

        Pane tickPane2 = new Pane();
        for (int i = -400; i <= 0; i += 20) {
            Line tick = new Line(CENTER_X - TICK_LENGTH / 2, CENTER_Y + i, CENTER_X + TICK_LENGTH / 2, CENTER_Y + i);
            tickPane2.getChildren().addAll(tick);


            if (i % 100 == 0) {
                Text label2 = new Text(Integer.toString(-i*20)+" m");
                label2.setFont(Font.font(15));
                label2.setX(CENTER_X + TICK_LENGTH + TEXT_PADDING);
                label2.setY(CENTER_Y + i + TEXT_PADDING);
                tickPane2.getChildren().add(label2);
            }
        }

        Group tcas = createTCAS();
        tcas.setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-min-width: 500");
        tcas.getChildren().add(otherAircraft);




        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), needle);
        TranslateTransition transition2 = new TranslateTransition(Duration.seconds(0.5), needle2);
        gloTrans = transition;
        gloTrans2 = transition2;
        transition.setInterpolator(Interpolator.EASE_OUT);
        transition2.setInterpolator(Interpolator.EASE_OUT);




        scale.setGraphic(new Group(scaleLine, tickPane, needle));
        scale2.setGraphic(new Group(scaleLine2, tickPane2, needle2));

        StackPane stackPane = new StackPane();
        ScrollPane scrollPane = scp;


        stackPane.getChildren().addAll(scrollPane, new ImageView(indic));
        pfd.setGraphic(stackPane);

        VBox scale1 = createEWD(10, 10, 2, Color.WHITE);
        VBox scale22 = createEWD(305, 10, 2, Color.WHITE);

        Text nd1 = new Text("N1");
        nd1.setFill(Color.BLUE);
        Text nd2 = new Text("%");
        nd2.setFill(Color.WHITE);
        VBox N1 = new VBox(nd1, nd2);
        N1.setAlignment(Pos.CENTER);
        N1.setStyle(" -fx-min-height: 170; -fx-min-width: 60");
        N1.setTranslateX(240);
        N1.setTranslateY(10);


        VBox scale3 = createEWD(10, 210, 2, Color.RED);

        Text eg1 = new Text("EGT");
        eg1.setFill(Color.BLUE);
        Text eg2 = new Text("℃");
        eg2.setFill(Color.WHITE);

        Text fu1 = new Text("F. F");
        fu1.setFill(Color.BLUE);
        Text fu2 = new Text("KG");
        fu2.setFill(Color.WHITE);
        Text lev = new Text("5000");
        lev.setFill(Color.GREEN);


        VBox fuel = new VBox();
        fuel.getChildren().addAll(fu1, fu2, lev);
        fuel.setAlignment(Pos.BOTTOM_CENTER);


        fuel.setTranslateY(50);

        VBox EGT = new VBox(eg1, eg2, fuel);
        EGT.setStyle("-fx-min-height: 170; -fx-min-width: 60");
        EGT.setTranslateX(240);
        EGT.setTranslateY(210);
        EGT.setAlignment(Pos.CENTER);
        VBox scale4 = createEWD(305, 210, 2, Color.RED);




        Label perc1 = (Label) scale1.getChildren().get(2).lookup("#label"); // get reference to Label object
        Label perc2 = (Label) scale22.getChildren().get(2).lookup("#label"); // get reference to Label object
        Label heat1 = (Label) scale3.getChildren().get(2).lookup("#label"); // get reference to Label object
        Label heat2 = (Label) scale4.getChildren().get(2).lookup("#label"); // get reference to Label object

        needPerc1 = scale1.getChildren().get(1); // get reference to Label object
        needPerc2 = scale22.getChildren().get(1); // get reference to Label object
        needHeat1 = scale3.getChildren().get(1); // get reference to Label object
        needHeat2 = scale4.getChildren().get(1); // get reference to Label object

        needPerc1.setRotate(20);
        needPerc2.setRotate(20);
        needHeat1.setRotate(82.22);
        needHeat2.setRotate(82.22);

        Line lin1 = (Line) needPerc1.lookup("#nee");
        Line lin2 = (Line) needPerc2.lookup("#nee");
        Line lin3 = (Line) needHeat1.lookup("#nee");
        Line lin4 = (Line) needHeat2.lookup("#nee");

        Circle cir1 = (Circle) needPerc1.lookup("#cen");
        Circle cir2 = (Circle) needPerc2.lookup("#cen");
        Circle cir3 = (Circle) needHeat1.lookup("#cen");
        Circle cir4 = (Circle) needHeat2.lookup("#cen");






        perc1.setText("20");
        perc2.setText("20");
        heat1.setText("82");
        heat2.setText("82");





        Group pane = new Group(scale1, N1, scale22, scale3, EGT, scale4);



        VBox nd = new VBox();
        nd.getChildren().add(createNd());

        Group comp = radar.start();

        VBox rad = new VBox();
        rad.getChildren().add(comp);
        //rad.setStyle("-fx-padding: 30");
        rad.setPadding(new Insets(30, 0, 0, 0));

        Circle own = (Circle) comp.getChildren().get(8).lookup("#own");



        VBox leftBox = new VBox(10);
        leftBox.getChildren().setAll(monitored);
        leftBox.setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-min-width: 1000; -fx-max-height: 100;");

        HBox downbox = new HBox(10);
        downbox.getChildren().setAll(tcas, pane, nd, rad);
        downbox.setStyle("-fx-background-color: transparent; -fx-padding: 5px; -fx-min-width: 200; -fx-max-height: 100");





        VBox middleBox = new VBox(1);
        middleBox.getChildren().setAll(scale);
        middleBox.setStyle("-fx-background-color: lightgray; -fx-padding: 0px; -fx-min-width: 100; -fx-max-height: 40");

        VBox endBox = new VBox(1);
        endBox.getChildren().setAll(scale2);
        endBox.setStyle("-fx-background-color: lightgray; -fx-padding: 0px; -fx-min-width: 100; -fx-max-height: 40");

        /**VBox tcas = new VBox(10);
         tcas.setStyle("-fx-background-image: url('"+tc.getUrl()+"'); -fx-padding: 5px; -fx-background-size: cover; -fx-min-width: 500;");*/



        VBox rightBox = new VBox(1);
        rightBox.getChildren().setAll(pfd);
        rightBox.setStyle("-fx-background-color: transparent; -fx-padding: 0px; -fx-end-margin: 0");
        rightBox.setPrefWidth(400);
        rightBox.setPrefHeight(50);

        HBox pfBox = new HBox(10);
        pfBox.setStyle("-fx-background-color: transparent; -fx-padding: 10px;");
        pfBox.getChildren().setAll(middleBox, rightBox, endBox);

        HBox pitBox = new HBox(10);
        pitBox.setStyle("-fx-background-color: transparent; -fx-padding: 10px");
        pitBox.getChildren().setAll(pfBox, leftBox);


        root.getChildren().setAll(pitBox, downbox);

        root.setStyle("-fx-background-color: transparent; -fx-padding: 10px;");



        monitored.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(Double.parseDouble(lev.getText()) != 0.5) {
                    lev.setText(String.valueOf(Double.parseDouble(lev.getText()) - 0.5));
                }
                //System.out.println("x jet: "+X+" , y jet: "+Y+" | x tanker: "+pl.getTranslateX()+" , y tanker: "+pl.getTranslateY());
                pl2.setLayoutX(event.getX());
                pl2.setLayoutY(event.getY()-50);
                double x = onDxChange(event.getX());
                double y = onDYChange(event.getY());
                X = x;
                Y = y;
                //System.out.println("moved");
                String msg =
                        "(x: " + event.getX() + ", y: " + event.getY() + ") -- " +
                                "(sceneX: " + event.getSceneX() + ", sceneY: " + event.getSceneY() + ") -- " +
                                "(screenX: " + event.getScreenX() + ", screenY: " + event.getScreenY() + ")";

                Point2D plTopLeftOnScreen = pl.localToScreen(0, 0);
                double dx = event.getScreenX() - plTopLeftOnScreen.getX();
                double dy = event.getScreenY() - plTopLeftOnScreen.getY();
                distance = Math.sqrt(dx * dx + dy * dy) - 218;
                dist = distance;
                System.out.println("distance: "+distance);
                //System.out.println("dx : "+dx+", dy : "+dy);

                if (distance < 200) {
                    if(dx<0){
                        own.setTranslateY(30);
                    }else if(dx>0){
                        own.setTranslateY(-30);
                    }

                    //warning.play();
                    translate.setToY(120);
                    translate.play();
                    otherAircraft.setFill(Color.RED);
                    //scene.setFill(Color.RED);

                } else if (distance < 300) {
                    if(dx<0){
                        own.setTranslateY(60);
                    }else if(dx>0){
                        own.setTranslateY(-60);
                    }

                }else if (distance < 400) {
                    if(dx<0){
                        own.setTranslateY(90);
                    }else if(dx>0){
                        own.setTranslateY(-90);
                    }

                } else if (distance < 500) {
                    if(dx<0){
                        own.setTranslateY(120);
                    }else if(dx>0){
                        own.setTranslateY(-120);
                    }

                    //warning.stop();
                    translate.setToY(50);
                    translate.play();

                    otherAircraft.setFill(Color.YELLOW);
                    //scene.setFill(Color.BLACK);
                } else {
                    if(dx<0){
                        own.setTranslateY(150);
                    }else if(dx>0){
                        own.setTranslateY(-150);
                    }

                    //warning.stop();
                    translate.setToY(0);
                    translate.play();

                    otherAircraft.setFill(Color.GREEN);
                    //scene.setFill(Color.BLACK);
                }



                if(!((needHeat1.getRotate()+10)>180)) {
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(0.5),
                                    new KeyValue(needHeat1.rotateProperty(), needHeat1.getRotate() + 5),
                                    new KeyValue(needHeat2.rotateProperty(), needHeat2.getRotate() + 5)

                            )
                    );
                    timeline.play();

                    heat1.setText(String.valueOf(Math.round(needHeat1.getRotate()+1)));
                    heat2.setText(String.valueOf(Math.round(needHeat2.getRotate()+1)));

                    if(needHeat1.getRotate()+1>130){
                        lin3.setStroke(Color.RED);
                        cir3.setStroke(Color.RED);
                        lin4.setStroke(Color.RED);
                        cir4.setStroke(Color.RED);

                        //warning.play();
                    }else if(needHeat1.getRotate()+1>110){
                        lin3.setStroke(Color.YELLOW);
                        cir3.setStroke(Color.YELLOW);
                        lin4.setStroke(Color.YELLOW);
                        cir4.setStroke(Color.YELLOW);

                        warning.stop();
                    }else{
                        lin3.setStroke(Color.GREEN);
                        cir3.setStroke(Color.GREEN);
                        lin4.setStroke(Color.GREEN);
                        cir4.setStroke(Color.GREEN);
                    }

                }



                /**if (dx>=-17 && dx<=-14){
                 if (dy>=93.00000000000003 && dy<=95.00000000000003){
                 System.out.println("fuelling");
                 }
                 }*/

                //dx = -15
                //dy = 89.00000000000003
                //System.out.println("Distance to pl: " + distance);
            }
        });

        monitored.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent mouseEvent) {
                //System.out.println("mouse button : "+mouseEvent.getButton());
                if(mouseEvent.getButton() == MouseButton.PRIMARY){
                    if(needle.getTranslateY() != 0){
                        //needle.setTranslateY(needle.getTranslateY()-5);
                        transition.setToY(needle.getTranslateY()-50);
                        transition.play();
                    }
                    if(!((needPerc1.getRotate()+20)>180)) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.seconds(0.5),
                                        new KeyValue(needPerc1.rotateProperty(), needPerc1.getRotate() + 20),
                                        new KeyValue(needPerc2.rotateProperty(), needPerc2.getRotate() + 20)

                                )
                        );
                        timeline.play();
                        perc1.setText(String.valueOf(Math.round(needPerc1.getRotate()+20)));
                        perc2.setText(String.valueOf(Math.round(needPerc2.getRotate()+20)));

                        if(needPerc1.getRotate()+20>=140){
                            lin1.setStroke(Color.RED);
                            cir1.setStroke(Color.RED);
                            lin2.setStroke(Color.RED);
                            cir2.setStroke(Color.RED);

                            //warning.play();
                        }else if(needPerc1.getRotate()+20>100){
                            lin1.setStroke(Color.YELLOW);
                            cir1.setStroke(Color.YELLOW);
                            lin2.setStroke(Color.YELLOW);
                            cir2.setStroke(Color.YELLOW);

                            warning.stop();
                        }else{
                            lin1.setStroke(Color.GREEN);
                            cir1.setStroke(Color.GREEN);
                            lin2.setStroke(Color.GREEN);
                            cir2.setStroke(Color.GREEN);
                        }


                    }
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    if(needle.getTranslateY() != 400){
                        //needle.setTranslateY(needle.getTranslateY()-5);
                        transition.setToY(needle.getTranslateY()+50);
                        transition.play();
                    }
                    if(needPerc1.getRotate()>20) {
                        Timeline timeline = new Timeline(
                                new KeyFrame(Duration.seconds(0.5),
                                        new KeyValue(needPerc1.rotateProperty(), needPerc1.getRotate() - 20),
                                        new KeyValue(needPerc2.rotateProperty(), needPerc2.getRotate() - 20)
                                )
                        );
                        timeline.play();

                        perc1.setText(String.valueOf(Math.round(needPerc1.getRotate()-20)));
                        perc2.setText(String.valueOf(Math.round(needPerc2.getRotate()-20)));

                        if(needPerc1.getRotate()-20>=140){
                            lin1.setStroke(Color.RED);
                            cir1.setStroke(Color.RED);
                            lin2.setStroke(Color.RED);
                            cir2.setStroke(Color.RED);

                            //warning.play();
                        }else if(needPerc1.getRotate()-20>100){
                            lin1.setStroke(Color.YELLOW);
                            cir1.setStroke(Color.YELLOW);
                            lin2.setStroke(Color.YELLOW);
                            cir2.setStroke(Color.YELLOW);

                            warning.stop();
                        }else {
                            lin1.setStroke(Color.GREEN);
                            cir1.setStroke(Color.GREEN);
                            lin2.setStroke(Color.GREEN);
                            cir2.setStroke(Color.GREEN);

                        }
                    }
                } else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                    Timeline timeline2 = new Timeline(
                            new KeyFrame(Duration.seconds(0.5),
                                    new KeyValue(needHeat1.rotateProperty(), 82.22),
                                    new KeyValue(needHeat2.rotateProperty(), 82.22)
                            )
                    );
                    timeline2.play();

                    lin3.setStroke(Color.GREEN);
                    lin4.setStroke(Color.GREEN);
                    cir3.setStroke(Color.GREEN);
                    cir4.setStroke(Color.GREEN);


                    heat1.setText("82");
                    heat2.setText("82");
                }
            }
        });


        scene.setOnKeyPressed(event -> {
            double vvalue = scrollPane.getVvalue();
            if (isAuto){
                autoPilotPlayer.stop();

                switch (event.getCode()) {
                    case G:
                        isAuto = !isAuto;
                        //System.out.println("switch to manual");

                        manualPlayer.play();

                        break;
                }
            }else{
                manualPlayer.stop();

                switch (event.getCode()) {
                    case G:
                        isAuto = !isAuto;
                        //System.out.println("switch to auto ");
                        animateScrollPaneVvalue(scrollPane, 0.5);
                        autoPilotPlayer.play();
                        break;
                    case UP:
                        robot.mouseMove(robot.getMouseX(), robot.getMouseY() - 10);
                        double targetVvalueUp = vvalue - delta;
                        animateScrollPaneVvalue(scrollPane, targetVvalueUp);

                        if(!((needPerc1.getRotate()+10)>180)) {
                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.seconds(0.5),
                                            new KeyValue(needPerc1.rotateProperty(), needPerc1.getRotate() + 10),
                                            new KeyValue(needPerc2.rotateProperty(), needPerc2.getRotate() + 10)

                                    )
                            );
                            timeline.play();
                            perc1.setText(String.valueOf(Math.round(needPerc1.getRotate()+10)));
                            perc2.setText(String.valueOf(Math.round(needPerc2.getRotate()+10)));

                            if(needPerc1.getRotate()+10>120){
                                lin1.setStroke(Color.RED);
                                cir1.setStroke(Color.RED);
                                lin2.setStroke(Color.RED);
                                cir2.setStroke(Color.RED);

                                //warning.play();
                            }else if(needPerc1.getRotate()+10>90){
                                lin1.setStroke(Color.YELLOW);
                                cir1.setStroke(Color.YELLOW);
                                lin2.setStroke(Color.YELLOW);
                                cir2.setStroke(Color.YELLOW);

                                warning.stop();
                            }else{
                                lin1.setStroke(Color.GREEN);
                                cir1.setStroke(Color.GREEN);
                                lin2.setStroke(Color.GREEN);
                                cir2.setStroke(Color.GREEN);
                            }


                        }

                        if(!((needHeat1.getRotate()+10)>180)) {
                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.seconds(0.5),
                                            new KeyValue(needHeat1.rotateProperty(), needHeat1.getRotate() + 5),
                                            new KeyValue(needHeat2.rotateProperty(), needHeat2.getRotate() + 5)

                                    )
                            );
                            timeline.play();

                            heat1.setText(String.valueOf(Math.round(needHeat1.getRotate()+5)));
                            heat2.setText(String.valueOf(Math.round(needHeat2.getRotate()+5)));

                            if(needHeat1.getRotate()+5>130){
                                lin3.setStroke(Color.RED);
                                cir3.setStroke(Color.RED);
                                lin4.setStroke(Color.RED);
                                cir4.setStroke(Color.RED);

                                //warning.play();
                            }else if(needHeat1.getRotate()+5>110){
                                lin3.setStroke(Color.YELLOW);
                                cir3.setStroke(Color.YELLOW);
                                lin4.setStroke(Color.YELLOW);
                                cir4.setStroke(Color.YELLOW);

                                warning.stop();
                            }else{
                                lin3.setStroke(Color.GREEN);
                                cir3.setStroke(Color.GREEN);
                                lin4.setStroke(Color.GREEN);
                                cir4.setStroke(Color.GREEN);
                            }

                        }



                        if(needle.getTranslateY() != 0){
                            //needle.setTranslateY(needle.getTranslateY()-5);
                            transition.setToY(needle.getTranslateY()-25);
                            transition.play();
                        }
                        if(needle2.getTranslateY() != 0){
                            //needle.setTranslateY(needle.getTranslateY()-5);
                            transition2.setToY(needle2.getTranslateY()-25);
                            transition2.play();
                        }

                        if(helper!=11)
                            helper++;

                        break;
                    case DOWN:
                        robot.mouseMove(robot.getMouseX(), robot.getMouseY() + 10);
                        //scrollPane.setVvalue(vvalue + delta);

                        if(!((needHeat1.getRotate()+10)>180)) {
                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.seconds(0.5),
                                            new KeyValue(needHeat1.rotateProperty(), needHeat1.getRotate() + 5),
                                            new KeyValue(needHeat2.rotateProperty(), needHeat2.getRotate() + 5)

                                    )
                            );
                            timeline.play();

                            heat1.setText(String.valueOf(Math.round(needHeat1.getRotate()+5)));
                            heat2.setText(String.valueOf(Math.round(needHeat2.getRotate()+5)));

                            if(needHeat1.getRotate()+5>130){
                                lin3.setStroke(Color.RED);
                                cir3.setStroke(Color.RED);
                                lin4.setStroke(Color.RED);
                                cir4.setStroke(Color.RED);

                                //warning.play();
                            }else if(needHeat1.getRotate()+5>110){
                                lin3.setStroke(Color.YELLOW);
                                cir3.setStroke(Color.YELLOW);
                                lin4.setStroke(Color.YELLOW);
                                cir4.setStroke(Color.YELLOW);

                                warning.stop();
                            }else{
                                lin3.setStroke(Color.GREEN);
                                cir3.setStroke(Color.GREEN);
                                lin4.setStroke(Color.GREEN);
                                cir4.setStroke(Color.GREEN);
                            }
                        }

                        if(needPerc1.getRotate()>20) {
                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.seconds(0.5),
                                            new KeyValue(needPerc1.rotateProperty(), needPerc1.getRotate() - 10),
                                            new KeyValue(needPerc2.rotateProperty(), needPerc2.getRotate() - 10)
                                    )
                            );
                            timeline.play();

                            perc1.setText(String.valueOf(Math.round(needPerc1.getRotate()-10)));
                            perc2.setText(String.valueOf(Math.round(needPerc2.getRotate()-10)));

                            if(needPerc1.getRotate()-10>120){
                                lin1.setStroke(Color.RED);
                                cir1.setStroke(Color.RED);
                                lin2.setStroke(Color.RED);
                                cir2.setStroke(Color.RED);

                                //warning.play();
                            }else if(needPerc1.getRotate()-10>90){
                                lin1.setStroke(Color.YELLOW);
                                cir1.setStroke(Color.YELLOW);
                                lin2.setStroke(Color.YELLOW);
                                cir2.setStroke(Color.YELLOW);

                                warning.stop();
                            }else {
                                lin1.setStroke(Color.GREEN);
                                cir1.setStroke(Color.GREEN);
                                lin2.setStroke(Color.GREEN);
                                cir2.setStroke(Color.GREEN);

                            }
                        }




                        double targetVvalueDown = vvalue + delta;
                        animateScrollPaneVvalue(scrollPane, targetVvalueDown);
                        if(needle.getTranslateY() != 0){
                            //needle.setTranslateY(needle.getTranslateY()-5);
                            transition.setToY(needle.getTranslateY()-25);
                            transition.play();
                        }

                        if(needle2.getTranslateY() != 400){
                            //needle.setTranslateY(needle.getTranslateY()-5);
                            transition2.setToY(needle2.getTranslateY()+25);
                            transition2.play();
                        }

                        if(helper!=0)
                            helper--;

                        break;
                    case LEFT:
                        //System.out.println("ne "+needle2.getTranslateY());
                        //needle.setTranslateY(350);
                        transition.setToY(400);
                        transition.play();


                        Timeline timeline2 = new Timeline(
                                new KeyFrame(Duration.seconds(0.5),
                                        new KeyValue(needHeat1.rotateProperty(), 82.22),
                                        new KeyValue(needHeat2.rotateProperty(), 82.22)
                                )
                        );
                        timeline2.play();

                        lin3.setStroke(Color.GREEN);
                        lin4.setStroke(Color.GREEN);
                        cir3.setStroke(Color.GREEN);
                        cir4.setStroke(Color.GREEN);


                        heat1.setText("82");
                        heat2.setText("82");



                        break;
                    case RIGHT:
                        robot.mouseMove(robot.getMouseX() + 10, robot.getMouseY());
                        if(needle.getTranslateY() != 0){
                            //needle.setTranslateY(needle.getTranslateY()-5);
                            transition.setToY(needle.getTranslateY()-25);
                            transition.play();
                        }

                        if(!((needHeat1.getRotate()+10)>180)) {
                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.seconds(0.5),
                                            new KeyValue(needHeat1.rotateProperty(), needHeat1.getRotate() + 5),
                                            new KeyValue(needHeat2.rotateProperty(), needHeat2.getRotate() + 5)
                                    )
                            );
                            timeline.play();

                            heat1.setText(String.valueOf(Math.round(needHeat1.getRotate()+5)));
                            heat2.setText(String.valueOf(Math.round(needHeat2.getRotate()+5)));

                            if(needHeat1.getRotate()+5>130){
                                lin3.setStroke(Color.RED);
                                cir3.setStroke(Color.RED);
                                lin4.setStroke(Color.RED);
                                cir4.setStroke(Color.RED);

                                //warning.play();
                            }else if(needHeat1.getRotate()+5>110){
                                lin3.setStroke(Color.YELLOW);
                                cir3.setStroke(Color.YELLOW);
                                lin4.setStroke(Color.YELLOW);
                                cir4.setStroke(Color.YELLOW);

                                warning.stop();
                            }else{
                                lin3.setStroke(Color.GREEN);
                                cir3.setStroke(Color.GREEN);
                                lin4.setStroke(Color.GREEN);
                                cir4.setStroke(Color.GREEN);
                            }
                        }






                        //System.out.println("v : "+vvalue);
                        if(vvalue > 0.5) {
                            vvalue = 0.5;
                            //scrollPane.setVvalue(vvalue);
                            animateScrollPaneVvalue(scrollPane, vvalue);
                        }
                        else if (vvalue < 0.5) {
                            vvalue = 0.5;
                            //scrollPane.setVvalue(vvalue);
                            animateScrollPaneVvalue(scrollPane, vvalue);
                        }

                    case Q:
                        robot.mouseMove(robot.getMouseX(), robot.getMouseY()+1);
                        pl.setTranslateX(pl.getTranslateX()-10);
                        out.println("q");

                        event.consume();
                        break;
                    case S:
                        robot.mouseMove(robot.getMouseX(), robot.getMouseY()+1);
                        pl.setTranslateY(pl.getTranslateY()+10);
                        out.println("s");

                        event.consume();

                        if(helper2!=0)
                            helper2--;
                        break;
                    case D:
                        robot.mouseMove(robot.getMouseX(), robot.getMouseY()+1);
                        pl.setTranslateX(pl.getTranslateX()+10);
                        out.println("d");

                        event.consume();
                        break;
                    case Z:
                        robot.mouseMove(robot.getMouseX(), robot.getMouseY()+1);
                        pl.setTranslateY(pl.getTranslateY()-10);
                        out.println("z");

                        event.consume();

                        if(helper2!=11)
                            helper2++;
                        break;
                    case T:
                        if(pl.getImage() == plane2){
                            pl.setImage(plane3);
                        } else if (pl.getImage() == plane3) {
                            pl.setImage(plane2);
                        }

                        out.println("t");

                        event.consume();

                        break;
                    case F:
                        System.out.println("F clicked");
                        Timeline timeline = new Timeline();
                        if(dist>-75 && dist<-60){
                            if(pl.getImage() == plane3) {
                                //refuelPlayer.play();
                                //lev.setText(String.valueOf(5000));

                                refuelPlayer.stop();
                                for (double i = 0.0; i <= 5000.0; i+=0.5) {
                                    double value = i;
                                    KeyFrame keyFrame = new KeyFrame(DURATION.multiply(i),
                                            event2 -> lev.setText(String.valueOf(value)));
                                    timeline.getKeyFrames().add(keyFrame);
                                }
                                timeline.play();
                            }
                        }

                        out.println("f");

                        event.consume();
                        break;
                    case W:
                        System.out.println("la distance est : "+dist);
                        own.setTranslateY(-150);
                        System.out.println(own);

                        out.println("w");

                        event.consume();
                    default:
                        break;
                }

            }
        });


        scene.setFill(Color.BLACK);

        stage.setScene(scene);
        stage.setFullScreen(true);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });

        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            }
        });

        mediaPlayer.play();
        stage.show();
        stage.show();

    }

    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(600);
        imageView.setFitHeight(2000);
        return imageView;
    }

    public double calculateDistance(Circle c1, Circle c2) {
        dx = c2.getCenterX() + X;
        dy = c2.getCenterY() + Y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double onDxChange(double diffx){
        double ddx = X + diffx;
        //System.out.println("X : "+diffx);
        return diffx;
    }

    public double onDYChange(double diffy){
        double ddy = Y + diffy;
        double dif = Y - diffy;
        //System.out.println("nee2: "+nee2.getTranslateY());
        double vvalue = scp.getVvalue();
        double targetVvalueUp = vvalue + ddy;
        //System.out.println("ddy : "+targetVvalueUp/1000+" vvalue : "+scp.getVvalue());
        if(targetVvalueUp/1000 > 0.09 || -0.09 > targetVvalueUp/1000) {
            animateScrollPaneVvalue(scp, targetVvalueUp / 1000);
        }else{
            //System.out.println("ok ok ");
            animateScrollPaneVvalue(scp, 0.5);
            //System.out.println("vvalue : "+scp.getVvalue());
        }
        if(dif > 0) {
            //System.out.println("up");

            if ((diffy <= 400) && (diffy >= 0)) {
                //needle.setTranslateY(needle.getTranslateY()-5);
                gloTrans2.setToY(diffy);
                gloTrans2.play();
            }
            if (nee2.getTranslateY() > 0 && nee2.getTranslateY() < 100) {
                helper = 4;
            } else if (nee2.getTranslateY() > 100 && nee2.getTranslateY() < 200) {
                helper = 3;
            } else if (nee2.getTranslateY() > 200 && nee2.getTranslateY() < 300) {
                helper = 2;
            } else if (nee2.getTranslateY() > 300 && nee2.getTranslateY() < 400) {
                helper = 1;
            } else if (nee2.getTranslateY() == 400) {
                helper = 0;
            }
        } else{
            //System.out.println("down");


            if((diffy <= 400) && (diffy >= 0)){
                //needle.setTranslateY(needle.getTranslateY()-5);
                gloTrans2.setToY(diffy);
                gloTrans2.play();
            }
            if (nee2.getTranslateY() > 0 && nee2.getTranslateY() < 100) {
                helper = 4;
            } else if (nee2.getTranslateY() > 100 && nee2.getTranslateY() < 200) {
                helper = 3;
            } else if (nee2.getTranslateY() > 200 && nee2.getTranslateY() < 300) {
                helper = 2;
            } else if (nee2.getTranslateY() > 300 && nee2.getTranslateY() < 400) {
                helper = 1;
            } else if (nee2.getTranslateY() == 400) {
                helper = 0;
            }

        }
        //System.out.println("Y : "+diffy);
        return diffy;
    }

    private Group createTCAS(){
        Group root = new Group();

        // Draw the map
        Circle map = new Circle(400, 300, 150);
        map.setFill(Color.TRANSPARENT);
        map.setStroke(Color.WHITE);

        //System.out.println(map.getRadius());

        Circle map1 = new Circle(400, 300, 90);
        map1.setFill(Color.TRANSPARENT);
        map1.setStroke(Color.DARKGRAY);
        map1.setStrokeWidth(3);
        map1.setStrokeType(StrokeType.OUTSIDE);
        map1.setStrokeLineCap(StrokeLineCap.BUTT);
        map1.getStrokeDashArray().addAll(10d, 20d);

        Circle map2 = new Circle(400, 300, 30);
        map2.setFill(Color.TRANSPARENT);
        map2.setStroke(Color.DARKGRAY);
        map2.setStrokeWidth(3);
        map2.setStrokeType(StrokeType.OUTSIDE);
        map2.setStrokeLineCap(StrokeLineCap.BUTT);
        map2.getStrokeDashArray().addAll(10d, 20d);

        double radius = 120; // set the radius of dispersion to 150 pixels
        double centerX = map.getCenterX();
        double centerY = map.getCenterY();

        Group ticks = new Group();
        for (int i = 0; i < 12; i++) {
            Line tick = new Line(0, -map.getRadius() - 10, 0, -map.getRadius());
            tick.setStroke(Color.WHITE);
            tick.setTranslateX(map.getCenterX());
            tick.setTranslateY(map.getCenterY());
            tick.getStyleClass().add("tick");
            tick.getTransforms().add(new Rotate(i * (360 / 12)));

            Text label = new Text(String.valueOf(i + 1));
            label.setStroke(Color.WHITE);
            label.getStyleClass().add("tick");
            label.getTransforms().add(new Rotate(i * (360 / 12)));
            label.setTranslateX(centerX + 1.4 *radius * Math.sin(Math.toRadians(i * (360 / 12))));
            label.setTranslateY(centerY - 1.4 *radius * Math.cos(Math.toRadians(i * (360 / 12))));


            //ticks.getChildren().add(label);



            ticks.getChildren().addAll(tick, label);

        }
        ticks.setRotate(29);


        // Draw the aircraft
        Circle ownAircraft = new Circle(400, 300, 5, Color.WHITE);
        Circle otherAircraft = new Circle(400, 200, 5, Color.GREEN);
        root.getChildren().addAll(map, map1, map2, ticks, ownAircraft);

        // Check for collisions and update colors


        return root;
    }

    public Group createNd(){
        Group nd = new Group();

        final CategoryAxis xAxis = new CategoryAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();


//creating the line chart with two axis created above
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setAnimated(false); // disable animations
        lineChart.setCreateSymbols(false);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(true);
        lineChart.setVerticalZeroLineVisible(true);

        lineChart.lookup(".chart-plot-background").setStyle("-fx-background:black;");
        lineChart.setPrefSize(350, 400);
//defining series to display data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        Platform.runLater(() ->
                series1.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: yellow;")
        );

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        Platform.runLater(() ->
                series2.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: purple;")
        );

// add series to chart
        lineChart.getData().addAll(series1, series2);

// setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

// put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int a = 2500;

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                int x = counter*distance2;
                counter++;
                // put random numbers with current time for both series
                series1.getData().add(new XYChart.Data<>(String.valueOf(x)+" m", a*helper));
                series2.getData().add(new XYChart.Data<>(String.valueOf(x)+" m", a*helper2));

                if (series1.getData().size() > 10) {
                    series1.getData().remove(0);
                    series2.getData().remove(0);
                }
            });
        }, 0, 1, TimeUnit.SECONDS);


        lineChart.getXAxis().setSide(Side.TOP);
// disable default symbols
        lineChart.setCreateSymbols(false);
        lineChart.setStyle("-fx-background-color: black");

        nd.getChildren().add(lineChart);

        return nd;
    }

    private void animateScrollPaneVvalue(ScrollPane scrollPane, double targetVvalue) {
        double currentVvalue = scrollPane.getVvalue();
        Duration duration = Duration.millis(380);
        KeyValue keyValue = new KeyValue(scrollPane.vvalueProperty(), targetVvalue);
        KeyFrame keyFrame = new KeyFrame(duration, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }



    private ScrollPane createScrollPane(Node content) {

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double width = 200;
            double height = 50;
            double contentWidth = content.getBoundsInLocal().getWidth();
            double contentHeight = content.getBoundsInLocal().getHeight();

            if (width > contentWidth) {
                content.setScaleX(1.3);
                content.setScaleY(1.3);
            } else if (height > contentHeight) {
                content.setScaleX(1.19);
                content.setScaleY(1.19);
            } else {
                content.setScaleX(1);
                content.setScaleY(1);
            }
        });
        scrollPane.setVvalue(0.5);
        scrollPane.setHvalue(0.5);

        return scrollPane;
    }

    public VBox createEWD(int X, int Y, int center, Color color){
        Text text = new Text("300.00");
        text.setStyle("-fx-highlight-text-fill: white");

        Label label = new Label("300.00");
        label.setTextFill(Color.GREEN);
        label.setId("label");
        //label.setStyle("-fx-text-fill: green; -fx-border-color: blue");

        label.setTranslateX(center);

        VBox scale = new VBox();
        scale.setTranslateX(X);
        scale.setTranslateY(Y);

        Arc arc = new Arc();

        Rectangle rec = new Rectangle(80, 40);

        Arc dang = new Arc();
        dang.setRadiusX(110);
        dang.setRadiusY(110);
        dang.setStartAngle(0);
        dang.setLength(50);
        dang.setFill(Color.TRANSPARENT);
        dang.setStroke(color);


        rec.setFill(Color.TRANSPARENT);
        rec.setStroke(Color.WHITE);
        rec.setTranslateX(center);

        double startX = 380;
        double startY = 0;
        double endX = 270;
        double endY = 0;


        Line hourHand = new Line(startX, startY, endX, endY);
        hourHand.setStrokeWidth(2);
        hourHand.setStroke(Color.GREEN);
        hourHand.setStrokeLineCap(StrokeLineCap.ROUND);

        hourHand.setId("nee");

        Circle round = new Circle();
        round.setCenterX(startX);
        round.setCenterY(startY);
        round.setStroke(Color.GREEN);
        round.setStrokeWidth(2);
        round.setRadius(5);

        round.setId("cen");



        Line hourHand2 = new Line(startX, startY, 483, endY);
        hourHand2.setStrokeWidth(2);
        hourHand2.setStroke(Color.TRANSPARENT);
        hourHand2.setStrokeLineCap(StrokeLineCap.ROUND);

        Group neele = new Group();
        neele.setTranslateY(-10);
        neele.getChildren().addAll(hourHand, round, hourHand2);


        StackPane s = new StackPane();
        s.getChildren().addAll(rec, label);

        arc.setRadiusX(110);
        arc.setRadiusY(110);
        arc.setStartAngle(0);
        arc.setLength(180);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.WHITE);

        Group curv = new Group();
        curv.getChildren().addAll(arc, dang);


        scale.getChildren().addAll(curv, neele,s);


        return scale;
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5555);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readLine();
                        if (message == null) break;
                        Platform.runLater(() -> textArea.appendText(message + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
