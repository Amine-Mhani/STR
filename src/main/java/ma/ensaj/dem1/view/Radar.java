package ma.ensaj.dem1.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Radar{

    public Group start() throws Exception {
        Group root = new Group();

        // Draw the map
        Circle map = new Circle(150, 150, 150);
        map.setFill(Color.LIMEGREEN);
        map.setStroke(Color.TRANSPARENT);

        Circle outermap = new Circle(150, 150, 150);
        outermap.setFill(Color.TRANSPARENT);
        outermap.setStroke(Color.LIMEGREEN);
        outermap.setStrokeWidth(3);

        //System.out.println(map.getRadius());

        Circle map1 = new Circle(150, 150, 90);
        map1.setFill(Color.TRANSPARENT);
        map1.setStroke(Color.LIMEGREEN);
        map1.setStrokeWidth(1);
        map1.setStrokeType(StrokeType.OUTSIDE);

        Circle map2 = new Circle(150, 150, 30);
        map2.setFill(Color.TRANSPARENT);
        map2.setStroke(Color.LIMEGREEN);
        map2.setStrokeWidth(1);
        map2.setStrokeType(StrokeType.OUTSIDE);

        Circle map3 = new Circle(150, 150, 120);
        map3.setFill(Color.TRANSPARENT);
        map3.setStroke(Color.LIMEGREEN);
        map3.setStrokeWidth(1);
        map3.setStrokeType(StrokeType.OUTSIDE);

        //System.out.println(map.getRadius());

        Circle map4 = new Circle(150, 150, 60);
        map4.setFill(Color.TRANSPARENT);
        map4.setStroke(Color.LIMEGREEN);
        map4.setStrokeWidth(1);
        map4.setStrokeType(StrokeType.OUTSIDE);

        Line ver = new Line(150, 0, 150, 300);

        ver.setStroke(Color.LIMEGREEN);

        Line hor = new Line(150, 0, 150, 300);
        hor.setStroke(Color.LIMEGREEN);
        hor.setRotate(90);

        map.setOpacity(0.1);

        Circle ownAircraft = new Circle(150, 150, 5, Color.LIMEGREEN);
        ownAircraft.setId("own");

        Arc arc = new Arc(150, 150, 150, 150, 0, 30);
        RadialGradient gradient = new RadialGradient(90, 0, 0.3, 0.3, 2, true,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.LIMEGREEN));
        arc.setFill(gradient);
        arc.setType(ArcType.ROUND);

        Arc arc2 = new Arc(150, 150, 150, 150, 180, 30);
        arc2.setFill(Color.TRANSPARENT);
        arc2.setType(ArcType.ROUND);



        Group dete = new Group(arc, arc2);

        dete.setRotate(60);



        arc.setStrokeWidth(3);






        root.getChildren().addAll(outermap, map, map1, map2, map3, map4, ver, hor, ownAircraft, dete);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dete.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(5), new KeyValue(dete.rotateProperty(), 360))
        );
        timeline.setCycleCount(Animation.INDEFINITE); // repeat indefinitely
        timeline.play();


        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);

        return root;
    }
}
