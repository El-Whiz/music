package com.toluwalase.musicapp0.Customs;

import android.provider.MediaStore;

import com.toluwalase.musicapp0.Models.MusicFiles;

import java.util.Comparator;

public class MusicComparator implements Comparator<MusicFiles> {
    private boolean pattern;
    private String sortBy;

    public MusicComparator(String sortBy, boolean ascending){
        this.pattern = ascending;
        this.sortBy = sortBy;
    }

    @Override
    public int compare(MusicFiles o1, MusicFiles o2) {
        int result = 0;

        switch (sortBy){
            case "title":
                result = compareStrings(o1.getTitle(), o2.getTitle());
                break;
            case "artist":
                result = compareStrings(o1.getArtist(), o2.getArtist());
                break;
            case "album":
                result = compareStrings(o1.getAlbum(), o2.getAlbum());
                break;
            case "genre":
                result = compareStrings(o1.getGenre(), o2.getGenre());
                break;
            case "duration":
                Integer first, second;
                try {
                    first = Integer.parseInt(o1.getDuration());
                } catch (NumberFormatException e){
                    first = null;
                }
                try {
                    second = Integer.parseInt(o2.getDuration());
                } catch (NumberFormatException e){
                    second = null;
                }
                result = compareInt(first, second);
                break;
            case "year":
                result = compareStrings(o1.getYear(), o2.getYear());
                break;
            case "size":
                Integer size1, size2;
                try {
                    size1 = Integer.parseInt(o1.getSize());
                } catch (NumberFormatException e){
                    size1 = null;
                }
                try {
                    size2 = Integer.parseInt(o2.getSize());
                } catch (NumberFormatException e){
                    size2 = null;
                }
                result = compareInt(size1, size2);
                break;
            case "date":
                result = compareStrings(o1.getDate(), o2.getDate());
                break;

//            default:
//                result = compareStrings(o1.getTitle(), o2.getTitle());
//                break;
        }

        return pattern ? result : -result;
    }

    private int compareStrings(String str1, String str2){
        if(str1 == null && str2 == null){
            return 0;
        } else if (str1 == null) {
            return 1;
        } else if (str2 == null) {
            return -1;
        } else {
            return str1.compareToIgnoreCase(str2);
        }
    }

    private int compareInt(Integer o1, Integer o2){
        if(o1 == null && o2 == null){
            o1 = Integer.MIN_VALUE;
            o2 = Integer.MIN_VALUE;
        } else if (o1 == null) {
            o1 = Integer.MIN_VALUE;
        } else if (o2 == null) {
            o2 = Integer.MIN_VALUE;
        }

        return Integer.compare(o1, o2);
    }
}
