package test;

public class FixMessage {
    String[] subStrings;
    private String id;
    private String message;
    private String market;
    private String instrument;
    private String quantity;
    private String price;

    public FixMessage(String fix){
        subStrings = fix.split(",");
        id = subStrings[0];
        message = subStrings[1];
        market = subStrings[2];
        instrument = subStrings[3];
        quantity = subStrings[4];
        price = subStrings[5];
    }
    public String toString(){
        return String.join(",", subStrings);
    }
}
