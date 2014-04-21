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

import com.badlogic.gdx.files.FileHandle;
import org.flixel.system.gdx.loaders.FlxFileHandleResolver;

import java.util.ArrayList;
import java.util.List;

public class LevelSet {

    private List<String> levels = new ArrayList<String>();

    private int selectedLevelIndex = 0;

    public static LevelSet fromDir(String path) {
        FileHandle dir = new FlxFileHandleResolver().resolve(path);

        if (! dir.exists()) {
            return null;
        }
        if (! dir.isDirectory()) {
            return null;
        }

        LevelSet levelSet = new LevelSet();

        for (FileHandle file : dir.list(".tmx")) {
            if (! file.isDirectory()) {
                String levelPath = path + (path.endsWith("/") ? "" : "/") + file.name();
                levelSet.levels.add(levelPath);
            }
        }

        return levelSet;
    }

    public List<String> getList() {
        return levels;
    }

    public boolean hasMore() {
        return levels.size() > 0
                && selectedLevelIndex < levels.size() - 1;
    }

    public String next() {
        selectedLevelIndex++;
        return getCurrent();
    }

    public String getCurrent() {
        return levels.get(selectedLevelIndex);
    }
}
