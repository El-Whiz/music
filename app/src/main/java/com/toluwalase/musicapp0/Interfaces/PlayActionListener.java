package com.toluwalase.musicapp0.Interfaces;

import com.toluwalase.musicapp0.Models.QueueListItems;

public interface PlayActionListener {
    void previousbuttonClicked();
    void nextbuttonClicked();
    void playpausebuttonClicked();
    void loopClicked();
    void completed();
    void load_search();
    void playpauseForOtherQueues(int pos, QueueListItems item);
}
