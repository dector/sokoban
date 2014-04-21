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

import aurelienribon.tweenengine.TweenAccessor;
import org.flixel.FlxSprite;

public class SpriteColorAccessor implements TweenAccessor<FlxSprite> {

    public static final int R = 0x01;
    public static final int G = 0x02;
    public static final int B = 0x04;

    public static final int ALL = R | G | B;

    @Override
    public int getValues(FlxSprite flxSprite, int type, float[] values) {
        int valueIndex = 0;

        int color = flxSprite.getColor();
        int[] rgb = ColorUtils.parseColor(color);

        if ((type & R) != 0) {
            values[valueIndex] = rgb[0];
            valueIndex++;
        }
        if ((type & G) != 0) {
            values[valueIndex] = rgb[1];
            valueIndex++;
        }
        if ((type & B) != 0) {
            values[valueIndex] = rgb[2];
            valueIndex++;
        }

        return valueIndex;
    }

    @Override
    public void setValues(FlxSprite flxSprite, int type, float[] values) {
        int lastIndex = 0;

        int color = flxSprite.getColor();
        int[] rgb = ColorUtils.parseColor(color);

        if ((type & R) != 0) {
            ColorUtils.updateR(rgb, (int) values[lastIndex]);
            lastIndex++;
        }
        if ((type & G) != 0) {
            ColorUtils.updateG(rgb, (int) values[lastIndex]);
            lastIndex++;
        }
        if ((type & B) != 0) {
            ColorUtils.updateB(rgb, (int) values[lastIndex]);
            lastIndex++;
        }

        color = ColorUtils.composeColor(rgb[0], rgb[1], rgb[2]);

        flxSprite.setColor(color);
    }


}
