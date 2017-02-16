package onemessage.jmw.com.onemessage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jasonw on 2/5/17.
 */

public class InsertEntry {
    private final String description;
    private final String value;

    public InsertEntry(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }
    public String getValue() {
        return value;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(description);
        out.writeString(value);
    }

    public static final Parcelable.Creator<InsertEntry> CREATOR
            = new Parcelable.Creator<InsertEntry>() {
        public InsertEntry createFromParcel(Parcel in) {
            return new InsertEntry(in);
        }

        public InsertEntry[] newArray(int size) {
            return new InsertEntry[size];
        }
    };

    private InsertEntry(Parcel in) {
        description = in.readString();
        value = in.readString();

    }
}
