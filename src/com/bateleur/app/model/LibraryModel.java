package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class LibraryModel implements Iterable {

    public LibraryModel (ResourceFile file, ResourceFolder data) {

    }

    public void update (SettingsModel settings) {

    }

    public void sortBy(Comparator<BAudio> comparator) {

    }

    public void filterBy(Function<BAudio, Boolean> filter) {

    }

    public int size() {

    }

    public void reset() {

    }


    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }
}
