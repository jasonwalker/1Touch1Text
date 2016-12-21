package onemessage.jmw.com.onemessage;

import android.os.Parcel;
import android.os.Parcelable;

public class PhoneEntry implements Parcelable {
    private final String number;
    private final String type;

    public PhoneEntry(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }
    public String getType() {
        return type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(type);
        out.writeString(number);
    }

    public static final Parcelable.Creator<PhoneEntry> CREATOR
            = new Parcelable.Creator<PhoneEntry>() {
        public PhoneEntry createFromParcel(Parcel in) {
            return new PhoneEntry(in);
        }

        public PhoneEntry[] newArray(int size) {
            return new PhoneEntry[size];
        }
    };

    private PhoneEntry(Parcel in) {
        type = in.readString();
        number = in.readString();

    }
}

