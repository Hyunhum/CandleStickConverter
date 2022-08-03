/* 
    Output 값을 저장하는 CandleStick 클래스입니다.
    오버라이드한 toString() 메서드를 통해 json 형식으로 로그를 출력할 수 있습니다 
*/

public class CandleStick {

    public Long start;
    
    public Long end;
    
    public String open;
    
    public String close;

    public String high;

    public String low;
    
    public String average;

    public String weightedAverage;

    public String volume;

    public CandleStick(Long start, Long end, String open, String close, String high, String low, String average, String weightedAverage, String volume) {

        this.start = start;
        this.end = end;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.average = average;
        this.weightedAverage = weightedAverage;
        this.volume = volume;

    }

    @Override
    public String toString() {

        return "{" + System.lineSeparator()
        + "start: " + this.start + "," + System.lineSeparator() 
        + "end: " + this.end + "," + System.lineSeparator()
        + "open: " + this.open + "," + System.lineSeparator()
        + "close: " + this.close + "," + System.lineSeparator()
        + "high: " + this.high + "," + System.lineSeparator()
        + "low: " + this.low + "," + System.lineSeparator()
        + "average: " + this.average + "," + System.lineSeparator()
        + "weightedAverage: " + this.weightedAverage + "," + System.lineSeparator()
        + "volume: " + this.volume + "," + System.lineSeparator()
        + "}";

    }
    
}