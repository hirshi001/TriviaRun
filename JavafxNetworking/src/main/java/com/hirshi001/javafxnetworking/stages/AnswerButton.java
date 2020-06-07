package com.hirshi001.javafxnetworking.stages;

import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

public class AnswerButton extends Button {

    private int id;

    public AnswerButton(int id){
        this.id = id;
        setPrefSize(300,100);
        setId("question-button");
    }
    public void  setQuestionId(int id){
        this.id = id;
    }

    public int getQuestionId(){
        return id;
    }

    public void setNull(){
        setText("-----");
        id = -1;
    }


}
