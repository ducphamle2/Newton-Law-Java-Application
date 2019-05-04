package hust.soict.ictglobal.miniproject.secondlaw;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SecondLawDemoController implements Initializable {
    @FXML
    private TextField forceInput;

    @FXML
    private TextField weightInput;

    @FXML
    private Text accelerationText;

    @FXML
    private Text distanceText;

    @FXML
    private Line kickingLeg;

    @FXML
    private Circle ball;

    @FXML
    private Button startBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        forceInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue;
            if (!newValue.matches("\\d*")) {
                text = newValue.replaceAll("[^\\d]", "");
            }
            if (!text.equals("") && Integer.parseInt(text) > 5000) {
                text = "5000";
            }
            forceInput.setText(text);
        });

        weightInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue;
            if (!newValue.matches("\\d*")) {
                text = newValue.replaceAll("[^\\d]", "");
            }
            if (!text.equals("") && Integer.parseInt(text) > 3000) {
                text = "3000";
            }
            weightInput.setText(text);
        });
    }

    @FXML
    private void kickStarted() {
        String forceText = forceInput.getText();
        String weightText = weightInput.getText();
        if (!forceText.equals("") && !weightText.equals("")) {
            // calculate acceleration and distance
            int force = Integer.parseInt(forceText);
            int weight = Integer.parseInt(weightText);

            double acceleration = ((double) force) / weight;
            double velocity = acceleration * 0.1;

            double velocityX = velocity * Math.cos(Math.toRadians(30));
            double velocityY = velocity * Math.sin(Math.toRadians(30));

            double flyTime = Math.sqrt(2 * velocityY / 9.8);
            double distance = velocityX * flyTime;

            accelerationText.setText(String.format("Acceleration: %.5f m/s\u00B2", acceleration));
            distanceText.setText(String.format("Distance: %.5f m", distance));

            // disable button
            startBtn.setDisable(true);

            // start animation
            Rotate rotate = new Rotate();
            rotate.setPivotX(kickingLeg.getEndX());
            rotate.setPivotY(kickingLeg.getEndY());

            kickingLeg.getTransforms() .add(rotate);

            Timeline timeline = new Timeline(50);

            KeyFrame frame1 = new KeyFrame(Duration.millis(0), new KeyValue(rotate.angleProperty(), 0));

            KeyFrame frame2 = new KeyFrame(Duration.millis(250), new KeyValue(rotate.angleProperty(), -120));

            timeline.getKeyFrames().addAll(frame1, frame2);

            timeline.playFromStart();

            new Thread(() -> {
                try {
                    double x = ball.getTranslateX();
                    double y = ball.getTranslateY();
                    Thread.sleep(250);
                    CubicCurveTo curve = new CubicCurveTo(300f, -100f, 500f, -100f, 600f, ball.getCenterY());
                    Path path = new Path();
                    path.getElements().addAll(new MoveTo(ball.getCenterX(), ball.getCenterY()), curve);
                    PathTransition pathTransition = new PathTransition(Duration.millis(1000), path, ball);
                    pathTransition.play();

                    Thread.sleep(1000);

                    startBtn.setDisable(false);
                    rotate.angleProperty().setValue(0);
                    pathTransition.stop();
                    ball.setTranslateX(x);
                    ball.setTranslateY(y);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }
}
