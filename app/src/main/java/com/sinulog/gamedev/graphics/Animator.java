package com.sinulog.gamedev.graphics;

import android.graphics.Canvas;

import com.sinulog.gamedev.game.Game;
import com.sinulog.gamedev.game.GameDisplay;
import com.sinulog.gamedev.gameobject.Player;
import com.sinulog.gamedev.gameobject.PlayerState;

public class Animator {
    private Sprite[] playerSpriteArrayRight;
    private Sprite[] playerSpriteArrayLeft;
    private int idxNotMovingFrame = 0;
    private int updateBeforeNextMoveFrame;
    private static final int MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME = 5 ;
    private int currentMovingFrame = 1;

    public Animator(Sprite[] spriteRight, Sprite[] spriteLeft) {
        this.playerSpriteArrayRight = spriteRight;
        this.playerSpriteArrayLeft = spriteLeft;
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay, Player player) {
        boolean facingRight = player.getDirectionX() >= 0;
        Sprite [] currentSpriteArray = facingRight ? playerSpriteArrayRight : playerSpriteArrayLeft;
        switch (player.getPlayerState().getState()) {
            case NOT_MOVING:
                drawFrame(canvas, gameDisplay, player, currentSpriteArray[idxNotMovingFrame]);
                break;
            case STARED_MOVING:
                currentMovingFrame = 1;
                updateBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
                drawFrame(canvas, gameDisplay, player, currentSpriteArray[currentMovingFrame]);
                break;
            case IS_MOVING:
                updateBeforeNextMoveFrame --;
                if (updateBeforeNextMoveFrame == 0) {
                    updateBeforeNextMoveFrame = MAX_UPDATES_BEFORE_NEXT_MOVE_FRAME;
                    toggleIdxMovingFrame();
                }
                drawFrame(canvas, gameDisplay, player, currentSpriteArray[currentMovingFrame]);
                break;
            default:
                break;
        }
    }

    private void toggleIdxMovingFrame() {
        currentMovingFrame++;
        if (currentMovingFrame > 5) {
            currentMovingFrame = 1;
        }
    }

    public void drawFrame (Canvas canvas, GameDisplay gameDisplay, Player player, Sprite sprite) {
        sprite.draw (
                canvas,
                (int) gameDisplay.gameToDisplayCoordinatesX(player.getPositionX()) - sprite.getWidth()/2,
                (int) gameDisplay.gameToDisplayCoordinatesY(player.getPositionY()) - sprite.getHeight()/2
        );
    }
}
