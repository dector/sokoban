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

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.dector.sokoban.util.Log;
import org.flixel.FlxGroup;
import org.flixel.FlxObject;
import org.flixel.FlxSprite;
import org.flixel.FlxTilemap;

public class World {

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
    }

    private static final int TILE_SIZE = 32;

    private static final int MAX_BOXES_IN_ROW_PUSH = 1;

    private static final int TILEMAP_START_INDEX = 1;

    private FlxSprite player;

    private FlxGroup boxes;
    private FlxObject[][] boxesMap;

    private FlxGroup holders;

    private FlxTilemap level;

    public World() {
        TmxMapLoader mapLoader = new TmxMapLoader();
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.yUp = false;

        TiledMap map = mapLoader.load("assets/levels/level0.tmx", params);

        level = new FlxTilemap();
        level.loadMap(FlxTilemap.tiledmapToCSV(map, "Background"), "assets/tiles.png",
                TILE_SIZE, TILE_SIZE, FlxTilemap.OFF, TILEMAP_START_INDEX);

        boxes = new FlxGroup();
        boxesMap = new FlxObject[level.widthInTiles][level.heightInTiles];

        holders = new FlxGroup();

        for (MapObject obj : map.getLayers().get("Objects").getObjects()) {
            String objName = obj.getName();

            if ("Player".equals(objName)) {
                player = new FlxSprite();
                player.loadGraphic("assets/player.png", true);
                player.addAnimation("stand_down", new int[] { 0 }, 0, false);
                player.addAnimation("walk_down", new int[] { 1, 2 }, 5, true);
                player.addAnimation("stand_left", new int[] { 3 }, 0, false);
                player.addAnimation("walk_left", new int[] { 4, 5 }, 5, true);
                player.addAnimation("stand_right", new int[] { 6 }, 0, false);
                player.addAnimation("walk_right", new int[] { 7, 8 }, 5, true);
                player.addAnimation("stand_up", new int[] { 9 }, 0, false);
                player.addAnimation("walk_up", new int[] { 10, 11 }, 5, true);
                player.x = ((RectangleMapObject) obj).getRectangle().getX();
                player.y = ((RectangleMapObject) obj).getRectangle().getY() - TILE_SIZE;
            } else if ("Box".equals(objName)) {
                FlxSprite box = new FlxSprite();
                box.loadGraphic("assets/box.png");
                box.x = ((RectangleMapObject) obj).getRectangle().getX();
                box.y = ((RectangleMapObject) obj).getRectangle().getY() - TILE_SIZE;
                boxes.add(box);
                boxesMap[(int) box.x / TILE_SIZE][(int) box.y / TILE_SIZE] = box;
            } else if ("Holder".equals(objName)) {
                FlxSprite holder = new FlxSprite();
                holder.loadGraphic("assets/holder.png");
                holder.x = ((RectangleMapObject) obj).getRectangle().getX();
                holder.y = ((RectangleMapObject) obj).getRectangle().getY() - TILE_SIZE;
                holders.add(holder);
            }
        }
    }

    public void update() {
    }

    public void draw() {
        level.draw();
        holders.draw();
        boxes.draw();
        player.draw();
    }

    public void tryMovePlayer(Direction direction) {
        int tileX = (int) player.x / TILE_SIZE;
        int tileY = (int) player.y / TILE_SIZE;
        int nextX = direction.nextTileX(tileX);
        int nextY = direction.nextTileY(tileY);

        boolean moved = tryMoveBox(nextX, nextY, direction, 1);
        if (moved) {
            player.x = nextX * TILE_SIZE;
            player.y = nextY * TILE_SIZE;
        }

        updatePlayer(direction);
    }

    private void updatePlayer(Direction direction) {
        switch (direction) {
            case LEFT:
                player.play("stand_left");
                player.setFacing(FlxObject.LEFT);
                break;
            case RIGHT:
                player.play("stand_right");
                player.setFacing(FlxObject.RIGHT);
                break;
            case UP:
                player.play("stand_up");
                player.setFacing(FlxSprite.UP);
                break;
            case DOWN:
                player.play("stand_down");
                player.setFacing(FlxObject.DOWN);
                break;
        }
    }

    private boolean tryMoveBox(int currentX, int currentY, Direction direction, int indexInRow) {
        boolean moved = false;

        Log.d("Trying to move %d:%d to %s", currentX, currentY, direction);
        Log.d("In row: %d", indexInRow);

        if (indexInRow <= MAX_BOXES_IN_ROW_PUSH
                && ! isMapTileSolid(currentX, currentY)
                && isBoxCoordsValid(currentX, currentY)) {
            FlxObject currentBox = boxesMap[currentX][currentY];

            if (currentBox != null) {
                int nextX = direction.nextTileX(currentX);
                int nextY = direction.nextTileY(currentY);

                Log.d("Next: %d:%d", nextX, nextY);

                if (isBoxCoordsValid(nextX, nextY)
                        && ! isMapTileSolid(nextX, nextY)) {
                    FlxObject nextBox = boxesMap[nextX][nextY];

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
                    currentBox.x = nextX * TILE_SIZE;
                    currentBox.y = nextY * TILE_SIZE;

                    boxesMap[nextX][nextY] = currentBox;
                    boxesMap[currentX][currentY] = null;
                }
            } else {
                // Cheat
                moved = true;
            }
        }

        return moved;
    }

    private boolean isBoxCoordsValid(int tileX, int tileY) {
        return 0 <= tileX && tileX < boxesMap.length
                && 0 <= tileY && tileY < boxesMap[tileX].length;
    }

    private boolean isMapTileSolid(int tileX, int tileY) {
        return Tile.byId(level.getTile(tileX, tileY)).solid;
    }
}
