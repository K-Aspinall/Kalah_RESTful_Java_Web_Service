package Kalah;

public class Kalah {

    private final long id;
    private final String content;
    private final int[14] pits;
    private final int[2] houses = [7,14]; //Off by one errors?

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int[] getPits() {
        return pits;
    }
}