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
package io.github.dector.sokoban.level;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.dector.sokoban.util.Log;
import io.github.dector.sokoban.util.Settings;
import org.flixel.*;
import org.flixel.plugin.tweens.TweenPlugin;
import org.flixel.plugin.tweens.TweenSprite;

public class World extends FlxGroup {

    public enum Tile {
        WALL(true), GRASS(false);

        public final boolean solid;

        Tile(boolean solid) {
            this.solid = solid;
        }

        public int id() {
            return ordinal();
        }

        public static Tile byId(int id) {
            return values()[id - TILEMAP_START_INDEX];
        }
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public int nextTileX(int x) {
            switch (this) {
                case LEFT:
                    return x-1;
                case RIGHT:
                    return x+1;
                default:
                    return x;
            }
        }

        public int nextTileY(int y) {
            switch (this) {
                case UP:
                    return y-1;
                case DOWN:
                    return y+1;
                default:
                    return y;
            }
        }

        public int getFacing() {
            switch (this) {
                case UP:
                    return FlxObject.UP;
                case DOWN:
                    return FlxObject.DOWN;
                case LEFT:
                    return FlxObject.LEFT;
                case RIGHT:
                    return FlxObject.RIGHT;
                default:
                    return FlxObject.DOWN;
            }
        }
    }

    private static final int TILE_SIZE = 32;

    private static final int MAX_BOXES_IN_ROW_PUSH = 1;

    private static final int TILEMAP_START_INDEX = 1;

    private FlxSprite player;

    private FlxGroup boxes;
    private FlxObject[][] boxesMask;

    private FlxGroup holders;
    private FlxPoint[] holdersMask;

    private FlxTilemap level;

    private boolean playerMoving;
    private boolean levelCompleted;

    private int steps;

    private LevelEventCallback callback;

    public World(LevelEventCallback callback) {
        this.callback = callback;

        TmxMapLoader mapLoader = new TmxMapLoader();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.yUp = false;

        TiledMap map = mapLoader.load("assets/levels/level0.tmx", params);

        level = new FlxTilemap();
        level.loadMap(FlxTilemap.tiledmapToCSV(map, "Background"), "assets/tiles.png",
                TILE_SIZE, TILE_SIZE, FlxTilemap.OFF, TILEMAP_START_INDEX);
        add(level);

        holders = new FlxGroup();
        add(holders);

        boxes = new FlxGroup();
        add(boxes);
        boxesMask = new FlxObject[level.widthInTiles][level.heightInTiles];

        player = new FlxSprite();
        add(player);

        for (MapObject obj : map.getLayers().get("Objects").getObjects()) {
            String objName = obj.getName();

            if ("Player".equals(objName)) {
                player.loadGraphic(Settings.PLAYER_SKIN.getAssetFile(), true, false, TILE_SIZE);
                player.addAnimation("stand_down",   new int[]{  0,  8,        }, 1, true);
                player.addAnimation("walk_down",    new int[]{  1,  9, 17,    }, 3, true);
                player.addAnimation("stand_left",   new int[]{  2, 10,        }, 1, true);
                player.addAnimation("walk_left",    new int[]{  3, 11, 19,    }, 3, true);
                player.addAnimation("stand_right",  new int[]{  4, 12,        }, 1, true);
                player.addAnimation("walk_right",   new int[]{  5, 13, 21,    }, 3, true);
                player.addAnimation("stand_up",     new int[]{  6, 14,        }, 1, true);
                player.addAnimation("walk_up",      new int[]{  7, 15, 23,    }, 3, true);
                player.x = ((RectangleMapObject) obj).getRectangle().getX();
                player.y = ((RectangleMapObject) obj).getRectangle().getY() - TILE_SIZE;
                player.setFacing(FlxObject.DOWN);
                updatePlayerSprite();
            } else if ("Box".equals(objName)) {
                FlxSprite box = new FlxSprite();
                box.loadGraphic("assets/box.png");
                box.x = ((RectangleMapObject) obj).getRectangle().getX();
                box.y = ((RectangleMapObject) obj).getRectangle().getY() - TILE_SIZE;
                boxes.add(box);
                boxesMask[(int) box.x / TILE_SIZE][(int) box.y / TILE_SIZE] = box;
            } else if ("Holder".equals(objName)) {
                FlxSprite holder = new FlxSprite();
                holder.loadGraphic("assets/holder.png");
                holder.x = ((RectangleMapObject) obj).getRectangle().getX();
                holder.y = ((RectangleMapObject) obj).getRectangle().getY() - TILE_SIZE;
                holders.add(holder);
            }

            holdersMask = new FlxPoint[holders.members.size];
            for (int i = 0; i < holders.members.size; i++) {
                FlxSprite holder = (FlxSprite) holders.members.get(i);
                FlxPoint p = new FlxPoint(holder.x / TILE_SIZE, holder.y / TILE_SIZE);
                holdersMask[i] = p;
            }
        }
    }

    public void forceCallbackPushInfo() {
        notifyCallbackSteps();
    }

    @Override
    public void postUpdate() {
        super.postUpdate();

        if (! levelCompleted && checkBoxesPlaced()) {
            levelCompleted = true;

            if (callback != null) {
                callback.onLevelCompleted();
            }
        }
    }

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public int getSteps() {
        return steps;
    }

    private boolean checkBoxesPlaced() {
        boolean result = true;

        for (int i = 0; i < holdersMask.length && result; i++) {
            FlxPoint p = holdersMask[i];

            if (boxesMask[(int) p.x][(int) p.y] == null) {
                result = false;
            }
        }

        return result;
    }

    public void tryMovePlayer(Direction direction) {
        if (playerMoving) {
            return;
        }
        if (levelCompleted) {
            return;
        }

        int tileX = (int) player.x / TILE_SIZE;
        int tileY = (int) player.y / TILE_SIZE;
        int nextX = direction.nextTileX(tileX);
        int nextY = direction.nextTileY(tileY);

        boolean moved = tryMoveBox(nextX, nextY, direction, 1);
        if (moved) {
            playerMoving = true;
            player.velocity.x += nextX * TILE_SIZE;
            player.velocity.y += nextY * TILE_SIZE;
        }
        startMovePlayer(direction);
    }

    private void startMovePlayer(Direction direction) {
        player.setFacing(direction.getFacing());
        updatePlayerSprite();

        if (playerMoving) {
            animateObjectMoving(player, (int) player.velocity.x, (int) player.velocity.y,
                    new TweenCallback() {
                        @Override
                        public void onEvent(int event, BaseTween<?> baseTween) {
                            if (event == TweenCallback.COMPLETE) {
                                playerMoving = false;
                                updatePlayerSprite();
                            }
                        }
                    });

            steps++;
            notifyCallbackSteps();
        } else {
            updatePlayerSprite();
        }

        player.velocity.x = 0;
        player.velocity.y = 0;
    }

    private void notifyCallbackSteps() {
        if (callback != null) {
            callback.onStepsChanged(steps);
        }
    }

    private void updatePlayerSprite() {
        switch (player.getFacing()) {
            case FlxObject.LEFT:
                player.play(playerMoving ? "walk_left" : "stand_left");
                break;
            case FlxObject.RIGHT:
                player.play(playerMoving ? "walk_right" : "stand_right");
                break;
            case FlxObject.UP:
                player.play(playerMoving ? "walk_up" : "stand_up");
                break;
            case FlxObject.DOWN:
                player.play(playerMoving ? "walk_down" : "stand_down");
                break;
        }
    }

    private boolean tryMoveBox(final int currentX, final int currentY, Direction direction, int indexInRow) {
        boolean moved = false;

        Log.d("Trying to move %d:%d to %s", currentX, currentY, direction);
        Log.d("In row: %d", indexInRow);

        if (indexInRow <= MAX_BOXES_IN_ROW_PUSH
                && ! isMapTileSolid(currentX, currentY)
                && isBoxCoordsValid(currentX, currentY)) {
            final FlxObject currentBox = boxesMask[currentX][currentY];

            if (currentBox != null) {
                final int nextX = direction.nextTileX(currentX);
                final int nextY = direction.nextTileY(currentY);

                Log.d("Next: %d:%d", nextX, nextY);

                if (isBoxCoordsValid(nextX, nextY)
                        && ! isMapTileSolid(nextX, nextY)) {
                    final FlxObject nextBox = boxesMask[nextX][nextY];

                    Log.d("Next object: %s", nextBox);

                    if (nextBox == null || !nextBox.getSolid()) {
                        moved = true;
                    } else {
                        boolean nextMoved = tryMoveBox(nextX, nextY, direction, indexInRow + 1);

                        if (nextMoved) {
                            moved = true;
                        }
                    }
                }

                if (moved) {
                    animateObjectMoving(currentBox, nextX * TILE_SIZE, nextY * TILE_SIZE,
                            new TweenCallback() {
                                @Override
                                public void onEvent(int event, BaseTween<?> baseTween) {
                                    if (event == TweenCallback.COMPLETE) {
                                        boxesMask[nextX][nextY] = currentBox;
                                        boxesMask[currentX][currentY] = null;
                                    }
                                }
                            }
                    );
                }
            } else {
                // Cheat
                moved = true;
            }
        }

        return moved;
    }

    private void animateObjectMoving(FlxObject obj, int toX, int toY, TweenCallback callback) {
        Tween.to(obj, TweenSprite.XY, .5f)
                .target(toX, toY)
                .setCallback(callback)
                .ease(TweenEquations.easeNone)
                .start(TweenPlugin.manager);
    }

    private boolean isBoxCoordsValid(int tileX, int tileY) {
        return 0 <= tileX && tileX < boxesMask.length
                && 0 <= tileY && tileY < boxesMask[tileX].length;
    }

    private boolean isMapTileSolid(int tileX, int tileY) {
        return Tile.byId(level.getTile(tileX, tileY)).solid;
    }
}
