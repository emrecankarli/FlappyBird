package sample;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Optional;
import java.util.Random;

public class Controller {
    @FXML
    private ImageView kare;
    @FXML
    private Cylinder kolonust;
    @FXML
    private Cylinder kolonalt;
    @FXML
    private Label skorLabel;

    private double ziplama = 50.0;
    private double yercekimi = 0.03;
    private double hiz = 0.0;

    private double taban = 350.0;
    private double tavan = 0.0;

    private boolean zipliyor = false;
    private boolean oyundurdu = false;

    private double kolonhizi = 1.5;

    private int skor = -1;

    private Timeline gameLoopTimeline;
    private Timeline kolonTimeline;

    public void initialize() {
        kare.setFocusTraversable(true);

        kare.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && !zipliyor && !oyundurdu) {
                jump();
            }
        });

        kolonanimasyon();

    }

    public void jump() {
        if (!zipliyor && !oyundurdu) {
            zipliyor = true;
            hiz = 0.0;

            Timeline jumpTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(kare.translateYProperty(), kare.getTranslateY())),
                    new KeyFrame(Duration.millis(150), new KeyValue(kare.translateYProperty(), kare.getTranslateY() - ziplama, Interpolator.EASE_OUT))
            );

            jumpTimeline.setOnFinished(event -> zipliyor = false);
            jumpTimeline.play();
        }
    }

    public void applyyercekimi() {
        hiz += yercekimi;
        double newY = kare.getTranslateY() + hiz;

        if (newY > taban) {
            newY = taban;
            hiz = 0.0;
        } else if (newY < tavan) {
            newY = tavan;
            hiz = 0.0;
        }

        kare.setTranslateY(newY);
    }

    public void startGameLoop() {
        gameLoopTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            if (!oyundurdu) {
                applyyercekimi();
                kolonhareket();
                carpisma();
            }
        }));
        gameLoopTimeline.setCycleCount(Animation.INDEFINITE);
        gameLoopTimeline.play();
    }

    public void kolonanimasyon() {
        kolonTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            if (!oyundurdu) {
                kolonhareket();
                carpisma();
            }
        }));
        kolonTimeline.setCycleCount(Animation.INDEFINITE);
        kolonTimeline.play();
    }

    public void kolonhareket() {
        kolonust.setTranslateX(kolonust.getTranslateX() - kolonhizi);
        kolonalt.setTranslateX(kolonalt.getTranslateX() - kolonhizi);

        if (kolonust.getTranslateX() + kolonust.getRadius() < kare.getTranslateX() && kolonust.getTranslateX() + kolonust.getRadius() > kare.getTranslateX() - kolonhizi) {
            skor++;
            skorLabel.setText(String.valueOf(skor));
        }

        if (kolonust.getTranslateX() + kolonust.getRadius() < -500) {
            resetkolonpozisyon(kolonust);
            resetKolonYukseklik(kolonust);
        }

        if (kolonalt.getTranslateX() + kolonalt.getRadius() < -500) {
            resetkolonpozisyon(kolonalt);
            resetKolonYukseklik(kolonalt);
        }
    }

    private void resetKolonYukseklik(Cylinder kolon) {
        kolon.setTranslateY(new Random().nextInt(50) + 50);
    }

    private void resetkolonpozisyon(Cylinder kolon) {
        kolon.setTranslateX(600);
    }

    private void carpisma() {
        if (kare.getBoundsInParent().intersects(kolonust.getBoundsInParent()) ||
                kare.getBoundsInParent().intersects(kolonalt.getBoundsInParent())) {
            oyundurdu = true;
            oyunbitti();
        }
    }

    private void oyunbitti() {
        Platform.runLater(() -> {
            gameLoopTimeline.stop();
            kolonTimeline.stop();
            Alert gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
            gameOverAlert.setTitle("Game Over");
            gameOverAlert.setHeaderText("Kaybettin! Skorun:" + skor);
            gameOverAlert.setContentText("Yeniden başlamak ister misin?");

            ButtonType evetButton = new ButtonType("Evet");
            ButtonType hayirButton = new ButtonType("Hayır");

            gameOverAlert.getButtonTypes().setAll(evetButton, hayirButton);

            Optional<ButtonType> result = gameOverAlert.showAndWait();

            if (result.isPresent() && result.get() == evetButton) {
                resetleme();
                durdurma();
            } else {
                System.exit(0);
            }
        });
    }

    private void resetleme() {
        kare.setTranslateY(0);
        resetkolonpozisyon(kolonust);
        resetkolonpozisyon(kolonalt);
        skor = 0;
        skorLabel.setText(String.valueOf(skor));
        zipliyor = false;
        // Timeline'ları sıfırla
        gameLoopTimeline.play();
        kolonTimeline.play();
    }

    private void durdurma() {
        oyundurdu = false;
    }
}
