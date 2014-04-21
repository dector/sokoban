/**
 * Copyright (c) 2014, dector (dector9@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.dector.sokoban.states;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import io.github.dector.sokoban.level.LevelEventCallback;
import io.github.dector.sokoban.level.World;
import io.github.dector.sokoban.util.Input;
import org.flixel.*;
import org.flixel.plugin.tweens.TweenPlugin;
import org.flixel.plugin.tweens.TweenSprite;

public class GameState extends FlxState implements LevelEventCallback {

    private Input input;
    private World world;

    private FlxSprite fadeForeground;
    private FlxText winText;

    private FlxText uiText;

    @Override
    public void create() {
        FlxG.setBgColor(0xffaaaaaa);

        FlxG.addPlugin(TweenPlugin.class);

        input = new Input();
        world = new World(this);

        add(world);

        uiText = new FlxText(10, 10, 100);
        add(uiText);

        fadeForeground = new FlxSprite();
        fadeForeground.makeGraphic(FlxG.screenWidth, FlxG.screenHeight, 0xff000000);
        fadeForeground.visible = false;
        add(fadeForeground);

        winText = new FlxText(0, 100, 400, "WIN!!!");
        winText.setFormat(null, 85, 0xffff0000, "center");
        winText.visible = false;
        add(winText);

        world.forceCallbackPushInfo();
    }

    @Override
    public void update() {
        super.update();

        if (FlxG.keys.R) {
            FlxG.resetState();
        }
        if (FlxG.keys.ESCAPE) {
            Gdx.app.exit();
        }

        if (input.leftPressed()) {
            world.tryMovePlayer(World.Direction.LEFT);
        }
        if (input.rightPressed()) {
            world.tryMovePlayer(World.Direction.RIGHT);
        }
        if (input.upPressed()) {
            world.tryMovePlayer(World.Direction.UP);
        }
        if (input.downPressed()) {
            world.tryMovePlayer(World.Direction.DOWN);
        }
    }

    @Override
    public void onLevelCompleted() {
        fadeForeground.visible = true;
        fadeForeground.setAlpha(0);

        winText.visible = true;
        winText.scale.make(.1f, .1f);

        Timeline.createParallel()
                .beginParallel()
                .push(Tween.to(fadeForeground, TweenSprite.ALPHA, 1)
                        .target(.75f)
                        .ease(TweenEquations.easeOutExpo))
                .push(Tween.to(winText, TweenSprite.SCALE_XY, .75f)
                        .target(1.2f, 1.2f)
                        .ease(TweenEquations.easeOutExpo)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> baseTween) {
                                if (type == COMPLETE) {
                                    winText.visible = true;
                                    Tween.to(winText, TweenSprite.SCALE_XY, .75f)
                                            .target(1, 1)
                                            .ease(TweenEquations.easeInOutSine)
                                            .repeatYoyo(Tween.INFINITY, 0)
                                            .start(TweenPlugin.manager);
                                }
                            }
                        }))
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(TweenPlugin.manager);
    }

    @Override
    public void onStepsChanged(int steps) {
        uiText.setText("Steps: " + world.getSteps());
    }

    @Override
    public void destroy() {
        super.destroy();

        TweenPlugin.manager.killAll();
    }
}
