import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //создание атомика, который позволит собирать данные, поступающие от разных потоков(магазины)
        LongAdder stat = new LongAdder();

        //создание потока на каждый магазин
        ExecutorService shop1 = Executors.newSingleThreadExecutor();
        ExecutorService shop2 = Executors.newSingleThreadExecutor();
        ExecutorService shop3 = Executors.newSingleThreadExecutor();

        //каждый поток передаёт данные атомику,
        // чтобы потом сформировался общий отчёт,
        // который можно будет отослать в налоговую
        submitReport(generateArray(), stat, shop1);
        submitReport(generateArray(), stat, shop2);
        submitReport(generateArray(), stat, shop3);

        //время на выполнение
        shop1.awaitTermination(2, TimeUnit.SECONDS);

        //суммируются поступившие данные
        System.out.println("Результат: " + stat.sum());

        //магазины закрываются после подачи отчётности
        shop1.shutdown();
        shop2.shutdown();
        shop3.shutdown();
        //отчёт готов к отправке
    }

    //формирование списка целочисленных данных(сумм чеков определённого магазина),
    // который нужен для формирования общей отчётности(сумма продаж с 3х магазинов)
    // о продажах сети магазинов
    public static List<Integer> generateArray() {
        int size = 10;
        List<Integer> list = new ArrayList<>();
        int fixedNumber = 10;
        for (int i = 0; i < size; i++) {
            list.add(fixedNumber);
        }
        return list;
    }

    //данные из переданного списка целочисленных данных list
    // добавляются потоком executorService
    //в LongAdder для дальнейшего сложения
    public static void submitReport(List<Integer> list, LongAdder stat, ExecutorService executorService) {
        //добавление данных из переданного списка в LongAdder для дальнейшего сложения
        list.stream()
                .forEach(i -> executorService.submit(() -> stat.add(i)));
    }
}
