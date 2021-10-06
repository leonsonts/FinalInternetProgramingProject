
import java.util.Objects;
import java.io.Serializable;


public class Index implements Serializable{
    int row, column;

    public Index(final int row, final int column) {
        this.row=row;
        this.column=column;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return row == index.row &&
                column == index.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "("+row +
                "," + column +
                ')';
    }

}
