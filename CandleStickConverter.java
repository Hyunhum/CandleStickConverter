import java.util.ArrayList;

/* 
 assumption
 1. csv의 입력들은 이미 파싱되어 array로 입력 순서대로 준비되어 있다고 가정하고,
    해당 array를 메서드의 파라미터 인풋으로 받습니다.
 2. 객체 형식으로 필드의 자료형을 모두 int 혹은 double을 쓰지만, 지정한 자료형대로 추후
    아웃풋 리스트에 입력 시 변환하여 넣습니다. 다만, 테스트 확인을 위해 로그를 출력하는데,
    객체 주소가 아닌 JSON 형식으로 로그를 출력하기 위해 toString()을 사용하여 배열에 넣어줬습니다(단순히 테스트를 위한 설정).
 3. Input의 timestamp, price를 int보다 메모리가 큰 long으로 지정한 것은 성능을 낮출 수 있지만,
    2038년 이후 timestamp가 overflow되는 점, 장기적으로 비트코인 원화 가격 역시 int의 최대값을 초과할 수 있는 점을 감안했습니다.
 4. 필드가 null로 채워지는 객체로 인해 for 문 중첩이 있고,
    시간복잡도가 증가하여 이를 해결해야하지만 제 역량 부족으로 거기까지는 해결하지 못했습니다.
*/

public class CandleStickConverter {

    public static ArrayList<String> candleStickConverter(Input[] arr, int period) {
        
        /*
           openIndex는 아웃풋의 시간 단위에 포함되는 첫번째 인풋의 인덱스입니다.
           1. openIndex를 통해 아웃풋 시간 내에 인풋이 하나만 포함되는지 체크할 수 있습니다.
           2. openIndex를 통해 아웃풋 시간 내에 여러 인풋이 있을 경우 average할 분모 range를 구할 수 있습니다. 
        */
        int openIndex = 0;
        // 주요 필드 선언 및 초기화
        long start = arr[0].getTimestamp();
        long high = arr[0].getPrice();
        long low = arr[0].getPrice();
        long sum = 0L; // 추후 average를 구하기 위함
        long weightedAverage = 0L;
        double volume = 0.00000000;
        /* 
            ArrayList도 내부적으로는 Array를 사용하기 때문에, 사이즈를 초기화하지 않으면 디폴트 크기로 10을 가진 배열을 만듭니다.
            추후 새로운 원소가 추가될 경우, 내부적으로 grow()를 사용하여 배열의 메모리를 추가 할당하는 과정을 거칩니다.
            사전 과제의 내용과 위 전제로는 아웃풋으로 나올 배열의 사이즈가 고정되어있기 때문에 초기화 시 구하여 할당해줍니다.
            데이터 사이즈가 커질수록 사이즈를 초기화한 것과 아닌 것 간의 시간 차이가 커집니다.
        */ 
        ArrayList<String> candleStickList = new ArrayList<>((int)(
            (arr[arr.length-1].getTimestamp() - start)/period + 1));

        // 인풋 배열 순회
        for (int i = 0; i < arr.length; i++) {
            /* 
                아웃풋 시간 내에 여러 인풋이 있을 경우를 체크, 
                아웃풋 시간 내의 인풋이 모두 담길 때까지 high, low, sum, volume, weightedAvg를 중첩하여 계산해줍니다.
                최종적으로 aggreation되어 아웃풋으로 들어 갈 마지막 인풋 전까지 for문을 순회하고 else로 넘어가서 case2를 통해 최종 아웃풋을 구합니다. 
            */
            if (i != arr.length - 1 // ArrayIndexOutOfBoundsException 방지
            && start + period - 1 >= arr[i+1].getTimestamp()) {
                high = Math.max(high,  arr[i+1].getPrice());
                low = Math.min(low,  arr[i+1].getPrice());
                sum += arr[i].getPrice(); 
                volume += arr[i].getSize();
                weightedAverage += arr[i].getPrice()*arr[i].getSize();
            }
            /* 
                크게 3가지 case입니다.
                case1. 아웃풋 구간 내에 인풋이 하나만 포함되는 경우
                case2. 아웃풋 구간 내에 인풋이 여러개 포함되어 위의 if 문을 순회하면서 타고 온 경우
                case3. 아웃풋 구간 내에 인풋이 하나도 포함되지 않아 계속 NULL이 포함된 값을 넣어줘야 하는 경우
            */
            else {
                // case1. openIndex를 통해 확인하고, start, end를 제외한 필드들에 인풋의 값을 아웃풋에 넣어줍니다.
                if (i == openIndex) {
                    candleStickList.add(
                        new CandleStick(
                            start,
                            start + period - 1,
                            String.valueOf(arr[i].getPrice()),
                            String.valueOf(arr[i].getPrice()),
                            String.valueOf(arr[i].getPrice()),
                            String.valueOf(arr[i].getPrice()),
                            String.valueOf(arr[i].getPrice()),
                            String.valueOf(arr[i].getPrice()),
                            String.format("%.8f", arr[i].getSize())
                        ).toString());
                // case2. 위의 순회를 통해 구한 최대, 최소, 평균, 가중평균, 거래량 등을 최종 계산 후 넣어줍니다.
                } else {
                    volume += arr[i].getSize();
                    candleStickList.add(
                        new CandleStick(
                            start,
                            start + period - 1,
                            String.valueOf(arr[openIndex].getPrice()),
                            String.valueOf(arr[i].getPrice()),
                            String.valueOf(high),
                            String.valueOf(low),
                            String.valueOf((sum + arr[i].getPrice())/(i - openIndex + 1)),
                            String.valueOf((int)Math.round(
                                (weightedAverage += arr[i].getPrice()*arr[i].getSize())/volume)),
                            String.format("%.8f",  volume)
                        ).toString());
                        // 다음 순회를 위해 초기화 해줍니다.
                        sum = weightedAverage = 0;
                        volume = 0;
                }
                // 최종까지 했을 경우 break
                if (i == arr.length - 1) {break;}
                // start, openIndex, high, low를 다음 차례에 맞게 증가시켜줍니다
                start += period;
                openIndex = i+1;
                high = low = arr[openIndex].getPrice();
                /* 
                    case3. open, close 등에서 null값을 가진 아웃풋 candleStick을 넣어줍니다.
                    아웃풋 캔들스틱에 인풋이 포함되는 직전까지 별개의 중첩 loop를 돕니다(성능저하... 최적화해야할 필요성...).
                */
                if (start > arr[i].getTimestamp()
                && start + period - 1 < arr[i+1].getTimestamp()) {
                    // 순회를 몇 번 돌지 다음 인풋의 타임스탬프 - 첫 아웃풋의 시작 시간를 period로 나누어준 몫으로 구합니다.
                    int iter = (int)(arr[i+1].getTimestamp() - start)/period;
                    for (int j = 0; j < iter; j++) {
                        // case3에 알맞은 아웃풋 값을 배열에 넣어줍니다.
                        candleStickList.add(
                            new CandleStick(
                                start,
                                start + period - 1,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                "0.00000000"
                            ).toString());
                        // 순회하면서 다음 아웃풋을 위해 계속 start를 period만큼 증가시켜줍니다
                        start += period;
                    } 
                }
            }
        }
        // 최종 아웃풋 json array 반환
        return candleStickList;
    }
    // main 메서드에서 candleStickConverter() 메서드를 테스트합니다. 
    public static void main(String[] args) {

        // candleStickConverter의 첫번째 파라미터인 Input 배열입니다.
        Input[] arr = {/*
            new Input(10L,100L,1.00000000),
            new Input(10L,100L,1.00000000),
            new Input(20L,200L,2.00000000),
            new Input(30L,300L,3.00000000),
            new Input(39L,300L,3.00000000),
            new Input(40L,400L,4.00000000),
            new Input(40L,400L,4.00000000),
            new Input(41L,400L,4.00000000),
            new Input(69L,400L,4.00000000),
            new Input(70L,500L,5.00000000),
            new Input(71L,500L,5.00000000),
            new Input(99L,500L,5.00000000),
            new Input(140L,600L,6.00000000),
            new Input(150L,700L,7.00000000),
            new Input(160L,800L,8.00000000),
            new Input(170L,900L,9.00000000),
            new Input(210L,1000L,10.00000000),
            new Input(279L,1000L,10.00000000),
            new Input(279L,1000L,10.00000000),
            new Input(279L,1000L,10.00000000),
            new Input(280L,1000L,10.00000000),
            new Input(280L,1000L,10.00000000),
            new Input(309L,1000L,10.00000000),
            new Input(310L,1100L,11.00000000),
            new Input(337L,1100L,11.00000000),
            new Input(339L,1100L,11.00000000),
            /*
            new Input(1383038122,250000,2.00000000),
            new Input(1383038169,254000,0.09700000),
            new Input(1383038169,259000,1.90300000),
            new Input(1383038233,251000,1.39100000)
            */
            new Input(1383037954,227000,0.30000000),
            new Input(1383038122,245000,1.19300000),
            new Input(1383038122,250000,0.30020000),
            new Input(1383038122,250000,2.00000000),
            new Input(1383038169,259000,0.09700000),
            new Input(1383038169,259000,1.90300000),
            new Input(1383059294,230000,0.69000000),
            new Input(1383059458,230000,0.31000000),
            new Input(1383059491,259000,0.09700000),
            new Input(1383059658,259500,3.00000000),
            new Input(1383059737,260000,5.00000000),
            new Input(1383067046,220000,0.15000000),
            new Input(1383089545,269500,1.00000000),
            new Input(1383089648,269500,1.00000000),
            new Input(1383091563,269500,1.00000000),
            new Input(1383091580,269500,0.76000000),
            new Input(1383091581,270000,4.24000000),
            new Input(1383091594,270000,0.76000000),
            new Input(1383091621,279000,1.80000000),
            new Input(1383092762,279000,0.20000000),
            new Input(1383092780,280000,0.20000000),
            new Input(1383092780,280000,0.80000000),
            new Input(1383094787,279000,6.60000000),
            new Input(1383102079,245000,1.02000000),
            new Input(1383102211,245000,0.98000000),
            new Input(1383102509,250000,2.00000000),
            new Input(1383104598,230000,0.10000000)
        };
        // candleStickConverter의 두번째 파라미터인 window size, 기간입니다.
        int period = 30;
        // 성능 테스트 시작 시간
        long startTime = System.currentTimeMillis();
        // Json 형식으로 출력되는 배열들을 로그로 확인할 수 있습니다.
        System.out.println(candleStickConverter(arr, period));
        // 구한 배열 값의 크기를 로그로 확인할 수 있습니다.
        System.out.println(candleStickConverter(arr, period).size());
        // 소요 시간 출력
        System.out.println("소요시간(초): " + (System.currentTimeMillis() - startTime)/1000.0);
    }
    
}
