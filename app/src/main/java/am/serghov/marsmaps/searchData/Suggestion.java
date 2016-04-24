package am.serghov.marsmaps.searchData;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import am.serghov.marsmaps.Data.MarsLocation;

/**
 * Created by serghov
 */
public class Suggestion implements SearchSuggestion {
    public MarsLocation mLocation;

    private String locationName;

    private boolean mIsHistory;

    public Suggestion(MarsLocation location){

        this.mLocation = location;
        this.locationName = location.getName();
    }

    public Suggestion(Parcel source) {
        this.locationName = source.readString();
    }

    public MarsLocation getLocation(){
        return mLocation;
    }

    public void setIsHistory(boolean isHistory){
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory(){
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mLocation.toString();
    }

    @Override
    public Creator getCreator() {
        return CREATOR;
    }

    ///////

    public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
        @Override
        public Suggestion createFromParcel(Parcel in) {
            return new Suggestion(in);
        }

        @Override
        public Suggestion[] newArray(int size) {
            return new Suggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationName);
    }
}
