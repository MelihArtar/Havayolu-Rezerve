import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Reservation {
    String seat;
    boolean isReserved;

    public Reservation(String seat) {
        this.seat = seat;
        this.isReserved = false;
    }

    public void makeReservation() {
        if (!isReserved) {
            isReserved = true;
            System.out.println(Thread.currentThread().getName() + " koltuğu rezerve etti: " + seat);
        } else {
            System.out.println(Thread.currentThread().getName() + " koltuğu rezerve etmeye çalıştı: " + seat + " (Zaten rezerve edilmiş)");
        }
    }

    public void cancelReservation() {
        if (isReserved) {
            isReserved = false;
            System.out.println(Thread.currentThread().getName() + " rezervasyonu iptal etti: " + seat);
        } else {
            System.out.println(Thread.currentThread().getName() + " koltuğu iptal etmeye çalıştı: " + seat + " (Zaten rezerve edilmemiş)");
        }
    }

    public void queryReservation() {
        System.out.println(Thread.currentThread().getName() + " koltuğun durumunu sorguladı: " + seat + ". Rezerve: " + isReserved);
    }
}

class Reader extends Thread {
    private final Reservation reservation;

    public Reader(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public void run() {
        reservation.queryReservation();
    }
}

class Writer extends Thread {
    private final Reservation reservation;
    private final boolean makeReservation;

    public Writer(Reservation reservation, boolean makeReservation) {
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

public class AsynchronousReservationSystem {
    public static void main(String[] args) {
        Reservation reservation = new Reservation("1A");

        List<Thread> threads = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            if (random.nextBoolean()) {
                threads.add(new Writer(reservation, random.nextBoolean()));
            } else {
                threads.add(new Reader(reservation));
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

        reservation.queryReservation();
    }
}
