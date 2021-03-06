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
package io.github.dector.sokoban.util;

import org.flixel.FlxG;

public class Input {

    public boolean left() {
        return FlxG.keys.LEFT;
    }

    public boolean right() {
        return FlxG.keys.RIGHT;
    }

    public boolean up() {
        return FlxG.keys.UP;
    }

    public boolean down() {
        return FlxG.keys.DOWN;
    }

    public boolean leftPressed() {
        return FlxG.keys.justPressed("LEFT");
    }

    public boolean rightPressed() {
        return FlxG.keys.justPressed("RIGHT");
    }

    public boolean upPressed() {
        return FlxG.keys.justPressed("UP");
    }

    public boolean downPressed() {
        return FlxG.keys.justPressed("DOWN");
    }

    public boolean actionPressed() {
        return FlxG.keys.justPressed("X");
    }

    public boolean isDebugPressed() {
        return FlxG.keys.justPressed("F2");
    }
}
