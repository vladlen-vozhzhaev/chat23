public class MultiThread {
    public static void main(String[] args) {
        Thread task1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Мыть посуду завершена на "+i+"%");
                }
            }
        });
        Thread task2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Пылесосить завершена на "+i+"%");
                }
            }
        });
        Thread task3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Стирка завершена на "+i+"%");
                }
            }
        });
        task1.start();
        task2.start();
        task3.start();
        /*
        Способ №2
        Thread task1 = new Thread(new MyRunnableClass("Мыть посуду"));
        Thread task2 = new Thread(new MyRunnableClass("Пылесосить"));
        Thread task3 = new Thread(new MyRunnableClass("Стирка"));
        task1.start();
        task2.start();
        task3.start();*/
        /*
        Способ #1
        MyThread task1 = new MyThread("Мыть посуду");
        MyThread task2 = new MyThread("Пылесосить");
        MyThread task3 = new MyThread("Стирка");
        task1.start();
        task2.start();
        task3.start();*/
    }
}

class MyRunnableClass implements Runnable{
    String taskName;

    public MyRunnableClass(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(taskName+" завершена на "+i+"%");
        }
    }
}

class MyThread extends Thread{
    String taskName;
    public MyThread(String taskName){
        this.taskName = taskName;
    }
    @Override
    public void run(){
        super.run();
        for (int i = 0; i < 100; i++) {
            System.out.println(taskName+" завершена на "+i+"%");
        }
    }
}