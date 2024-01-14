package sample;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Optional;
import java.util.Random;

public class Controller {
    @FXML
    private Rectangle kare;
    @FXML
    private Cylinder kolonust;
    @FXML
    private Cylinder kolonalt;

    private double ziplama = 50.0;
    private double jumpVelocity = -20.0;

    private double yercekimi = 0.02;
    private double hiz = 0.0;

    private double taban = 350.0;
    private double tavan = 0.0;

    private boolean zipliyor = false;
    private boolean oyundurdu = false;

    private double kolonhizi = 5.0;

    private double baslangicY = taban;

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

        zipliyor = true;

        // Yüksekliği azalt
        TranslateTransition translateUp = new TranslateTransition(Duration.seconds(0.25), kare);
        translateUp.setByY(-ziplama);

        // Yüksekliği geri al
        TranslateTransition translateDown = new TranslateTransition(Duration.seconds(0.25), kare);
        translateDown.setByY(ziplama);

        // Zıplama animasyonunu bitiminde zıplama durumunu sıfırla
        translateUp.setOnFinished(event -> {
            translateDown.play();
            translateDown.setOnFinished(e -> zipliyor = false);
        });

        translateUp.play();
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
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                applyyercekimi();
            }
        };
        gameLoop.start();
    }

    public void kolonanimasyon() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            if (!oyundurdu) {
                kolonhareket();
                carpisma();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        //Anim başlat
        timeline.play();
    }

    public void kolonhareket() {
        kolonust.setTranslateX(kolonust.getTranslateX() - kolonhizi);
        kolonalt.setTranslateX(kolonalt.getTranslateX() - kolonhizi);

        // Kolonların ekrandan çıkması durumunda tekrar sağa al ve yeni bir yükseklik ataması yap
        if (kolonust.getTranslateX() + kolonust.getRadius() < -300) {
            resetkolonpozisyon(kolonust);
            resetKolonYukseklik(kolonust);
        }
        if (kolonalt.getTranslateX() + kolonalt.getRadius() < -300) {
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
        // Kare ile kolonların çarpışmasını kontrol et
        if (kare.getBoundsInParent().intersects(kolonust.getBoundsInParent()) ||
                kare.getBoundsInParent().intersects(kolonalt.getBoundsInParent())) {
            oyundurdu = true;
            oyunbitti();
            resetleme();
        }
    }

    private void oyunbitti() {
        Platform.runLater(() -> {
            Alert gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
            gameOverAlert.setTitle("Game Over");
            gameOverAlert.setHeaderText("Kaybettin!");
            gameOverAlert.setContentText("Yeniden başlamak ister misin?");

            ButtonType evetButton = new ButtonType("Evet");
            ButtonType hayirButton = new ButtonType("Hayır");

            gameOverAlert.getButtonTypes().setAll(evetButton, hayirButton);

            Optional<ButtonType> result = gameOverAlert.showAndWait();

            if (result.isPresent() && result.get() == evetButton) {
                // Yeniden başlamak istendiğinde oyunu sıfırla
                resetleme();
                durdurma();
            } else {
                // Hayır'a basıldığında uygulamayı kapat
                System.exit(0);
            }
        });
    }

    private void resetleme() {
        // Tüm pozisyonları ve durumları sıfırla
        kare.setTranslateY(0);
        resetkolonpozisyon(kolonust);
        resetkolonpozisyon(kolonalt);
        zipliyor = false;
    }

    private void durdurma() {
        oyundurdu = false;
    }


}