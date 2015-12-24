package test.akka.application.messages;
/*
* Item message represent one line in text file
*/
public final class Item {

    private final Integer id;
    private final Integer count;

    public Item(Integer id, Integer count) {
        this.id = id;
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCount() {
        return count;
    }
}
