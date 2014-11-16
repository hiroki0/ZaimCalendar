package hm.orz.key0note.zaimcalendar.model;

import java.util.HashMap;

public class GenreList {

    private HashMap<Integer, Genre> mGenreMap = new HashMap<Integer, Genre>();

    public void addGenre(int id, Genre genre) {
        mGenreMap.put(id, genre);
    }

    public Genre getGenre(int id) {
        return mGenreMap.get(id);
    }
}
