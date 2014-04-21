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

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import io.github.dector.sokoban.SokobanGame;
import io.github.dector.sokoban.util.*;
import org.flixel.*;
import org.flixel.event.IFlxCamera;
import org.flixel.plugin.tweens.TweenPlugin;

public class MenuState extends FlxState {

    private enum Item {
        NEW_GAME("New game") {

            @Override
            public void onSelected() {
                FlxG.fade(0xff000000, .5f, new IFlxCamera() {
                    @Override
                    public void callback() {
                        LevelSet levelSet = LevelSet.fromDir("assets/levels/");
                        FlxG.switchState(new GameState(levelSet));
                    }
                });
            }
        },

        OPTIONS("Options") {

            @Override
            public void onSelected() {
            }
        },

        ABOUT("About") {

            @Override
            public void onSelected() {
            }
        },

        EXIT("Exit") {

            @Override
            public void onSelected() {
                SokobanGame.exit();
            }
        };

        public final String label;

        Item(String label) {
            this.label = label;
        }

        public abstract void onSelected();
    }

    private Input input;

    private FlxGroup menu;

    private int selectedItemIndex;

    @Override
    public void create() {
        // TODO draw circular gradient
        FlxG.setBgColor(0xff191919);

        FlxG.addPlugin(TweenPlugin.class);
        Tween.registerAccessor(MenuButton.class, new SpriteColorAccessor());

        input = new Input();

        menu = new FlxGroup();

        final int buttonsCount = Item.values().length;
        final int spacing = 25;
        final int topMargin = (FlxG.height - (buttonsCount - 1) * spacing - buttonsCount * 20) / 2;
        for (int i = 0; i < buttonsCount; i++) {
            Item item = Item.values()[i];
            int x = FlxG.width / 2;
            menu.add(newButtonCentered(x, i * spacing + topMargin, item));
        }
        add(menu);

        setButtonSelected(selectedItemIndex, true, false);
    }

    @Override
    public void update() {
        super.update();

        if (input.downPressed()) {
            setButtonSelected(selectedItemIndex, false, true);

            selectedItemIndex++;
            if (selectedItemIndex >= menu.length) {
                selectedItemIndex = 0;
            }

            setButtonSelected(selectedItemIndex, true, true);
            FlxG.play(Audio.Sounds.MENU_ITEM_MOVED.file);
        }
        if (input.upPressed()) {
            setButtonSelected(selectedItemIndex, false, true);

            selectedItemIndex--;
            if (selectedItemIndex < 0) {
                selectedItemIndex = menu.length - 1;
            }

            setButtonSelected(selectedItemIndex, true, true);
            FlxG.play(Audio.Sounds.MENU_ITEM_MOVED.file);
        }
        if (input.actionPressed()) {
            MenuButton menuButton = (MenuButton) menu.members.get(selectedItemIndex);
            menuButton.flicker(.3f);
            menuButton.item.onSelected();
            FlxG.play(Audio.Sounds.MENU_ITEM_SELECTED.file);
        }
    }

    private void setButtonSelected(int index, boolean selected, boolean animate) {
        MenuButton menuButton = (MenuButton) menu.members.get(index);
        if (animate) {
            menuButton.animateSelected(selected);
        } else {
            menuButton.setSelected(selected);
        }
    }

    private MenuButton newButtonCentered(int cx, int y, Item item) {
        MenuButton button = new MenuButton(cx, y, item);
        button.x -= button.width / 2;
        return button;
    }

    private static class MenuButton extends FlxText {

        private static final int COLOR_NORMAL = 0x797979;
        private static final int COLOR_SELECTED = 0xffffff;

        public final Item item;

        private MenuButton(int x, int y, Item item) {
            super(x, y, 80, item.label);
            setAlignment("center");

            setSelected(false);
            this.item = item;
        }

        public void setSelected(boolean selected) {
            setColor(selected ? COLOR_SELECTED : COLOR_NORMAL);
        }

        public void animateSelected(boolean selected) {
            int[] rgb = ColorUtils.parseColor(selected ? COLOR_SELECTED : COLOR_NORMAL);

            Tween.to(this, SpriteColorAccessor.ALL, .5f)
                    .target(rgb[0], rgb[1], rgb[2])
                    .ease(TweenEquations.easeInOutSine)
                    .start(TweenPlugin.manager);
        }
    }
}
