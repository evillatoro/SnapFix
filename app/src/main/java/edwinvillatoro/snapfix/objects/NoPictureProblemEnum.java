package edwinvillatoro.snapfix.objects;

/**
 * Created by Allen on 9/10/2017.
 * Enum for NoPictureActivity Spinner Population
 */

public enum NoPictureProblemEnum {
    KITCHEN ("Kitchen"),
    PLUMBING ("Plumbing"),
    ACHEAT ("A/C and Heating");

    private String value;

    NoPictureProblemEnum(String value){
        this.value = value;
    }

    @Override public String toString(){
        return value;
    }
}
