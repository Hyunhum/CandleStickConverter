// 인풋 값을 저장하는 클래스입니다. Input 클래스의 배열을 함수의 파라미터로 넣어줍니다.

public class Input {

    public long timestamp;
    public long price;
    public double size;

    public Input(long timestamp, long price, double size) {
        this.timestamp = timestamp;
        this.price = price;
        this.size = size;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public long getPrice() {
        return this.price;
    }

    public double getSize() {
        return this.size;
    }

}
