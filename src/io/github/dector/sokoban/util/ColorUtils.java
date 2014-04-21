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

public class ColorUtils {

    private static final int COLOR_MASK_R = 0x00ff0000;
    private static final int COLOR_MASK_G = 0x0000ff00;
    private static final int COLOR_MASK_B = 0x000000ff;

    private static final int COLOR_SHIFT_R = 16;
    private static final int COLOR_SHIFT_G = 8;
    private static final int COLOR_SHIFT_B = 0;

    public static int[] parseColor(int colorRGB) {
        int[] rgb = new int[3];

        rgb[0] = (colorRGB & COLOR_MASK_R) >>> COLOR_SHIFT_R;
        rgb[1] = (colorRGB & COLOR_MASK_G) >>> COLOR_SHIFT_G;
        rgb[2] = (colorRGB & COLOR_MASK_B) >>> COLOR_SHIFT_B;

        return rgb;
    }

    public static int composeColor(int r, int g, int b) {
        int colorRGB = 0;

        colorRGB |= r << COLOR_SHIFT_R;
        colorRGB |= g << COLOR_SHIFT_G;
        colorRGB |= b << COLOR_SHIFT_B;

        return colorRGB;
    }

    public static int[] updateR(int[] rgb, int r) {
        if (r < 0)
            r = 0;
        if (r > 0xff)
            r = 0xff;

        rgb[0] = r;

        return rgb;
    }

    public static int[] updateG(int[] rgb, int g) {
        if (g < 0)
            g = 0;
        if (g > 0xff)
            g = 0xff;

        rgb[1] = g;

        return rgb;
    }

    public static int[] updateB(int[] rgb, int b) {
        if (b < 0)
            b = 0;
        if (b > 0xff)
            b = 0xff;

        rgb[2] = b;

        return rgb;
    }

}
