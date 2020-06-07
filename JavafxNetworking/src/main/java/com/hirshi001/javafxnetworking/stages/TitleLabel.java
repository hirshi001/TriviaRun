package com.hirshi001.javafxnetworking.stages;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class TitleLabel extends Label {

    public TitleLabel(){
        setText("TRIVIA RUN!");
        setId("title");
        setPrefWidth(Double.MAX_VALUE);
        setPrefHeight(50);
        setAlignment(Pos.CENTER);
        setTextFill(Color.WHITE);
    }


}
