package edu.hitsz.application;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BgmThread extends MusicThread{




    public BgmThread(String filename) {
        super(filename);
    }



    @Override
    public void run() {
        InputStream stream = new ByteArrayInputStream(samples);
            play(stream);
    }

}
