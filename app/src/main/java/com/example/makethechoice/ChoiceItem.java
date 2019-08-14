package com.example.makethechoice;

public class ChoiceItem {

    private String choiceText;
    private Boolean isChoosen;
    private Boolean isRight;
    private Boolean isShowingRight = false;
    private Boolean isShowingWrong = false;

    public ChoiceItem(String choiceText, Boolean isChoosen, Boolean isRight) {
        this.choiceText = choiceText;
        this.isChoosen = isChoosen;
        this.isRight = isRight;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public Boolean getChoosen() {
        return isChoosen;
    }

    public void setChoosen(Boolean choosen) {
        isChoosen = choosen;
    }

    public Boolean getRight() {
        return isRight;
    }

    public void setRight(Boolean right) {
        isRight = right;
    }

    public Boolean getShowingRight() {
        return isShowingRight;
    }

    public void setShowingRight(Boolean showingRight) {
        isShowingRight = showingRight;
    }

    public Boolean getShowingWrong() {
        return isShowingWrong;
    }

    public void setShowingWrong(Boolean showingWrong) {
        isShowingWrong = showingWrong;
    }
}
