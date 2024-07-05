import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SynchronizedReservation {
    private final String seat;
    private boolean isReserved;
    private final ReentrantReadWriteLock lock;

    public SynchronizedReservation(String seat) {
        this.seat = seat;
        this.isReserved = false;
        this.lock = new ReentrantReadWriteLock();
    }

    public void makeReservation() {
        lock.writeLock().lock();
        try {
            if (!isReserved) {
                isReserved = true;
                System.out.println(Thread.currentThread().getName() + " koltuğu rezerve etti: " + seat);
            } else {
                System.out.println(Thread.currentThread().getName() + " koltuğu rezerve etmeye çalıştı: " + seat + " (Zaten rezerve edilmiş)");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void cancelReservation() {
        lock.writeLock().lock();
        try {
            if (isReserved) {
                isReserved = false;
                System.out.println(Thread.currentThread().getName() + " rezervasyonu iptal etti: " + seat);
            } else {
                System.out.println(Thread.currentThread().getName() + " koltuğu iptal etmeye çalıştı: " + seat + " (Zaten rezerve edilmemiş)");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void queryReservation() {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " koltuğun durumunu sorguladı: " + seat + ". Rezerve: " + isReserved);
        } finally {
            lock.readLock().unlock();
        }
    }
}

class SynchronizedReader extends Thread {
    private final SynchronizedReservation reservation;

    public SynchronizedReader(SynchronizedReservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public void run() {
        reservation.queryReservation();
    }
}

class SynchronizedWriter extends Thread {
    private final SynchronizedReservation reservation;
    private final boolean makeReservation;

    public SynchronizedWriter(SynchronizedReservation reservation, boolean makeReservation) {
        this.reservation = reservation;
        this.makeReservation = makeReservation;
    }

    @Override
    public void run() {
        if (makeReservation) {
            reservation.makeReservation();
        } else {
            reservation.cancelReservation();
        }
    }
}

public class SynchronizedReservationSystem {
    public static void main(String[] args) {
        SynchronizedReservation reservation = new SynchronizedReservation("1A");

        List<Thread> threads = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            if (random.nextBoolean()) {
                threads.add(new SynchronizedWriter(reservation, random.nextBoolean()));
            } else {
                threads.add(new SynchronizedReader(reservation));
            }
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
