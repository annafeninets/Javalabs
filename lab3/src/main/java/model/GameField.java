package model;

import model.exception.DimensionOutOfField;
import model.exception.IndexOutOfField;

public interface GameField {
    Object getRepresentation();
    boolean isCellFree(int ... pos) throws IndexOutOfField, DimensionOutOfField;
    void assignValueToPosition(Object value, int ... pos) throws IndexOutOfField, DimensionOutOfField;
    int[] getSizeField();
    void renew();
    int removeFullLayers(int from, int length);
}