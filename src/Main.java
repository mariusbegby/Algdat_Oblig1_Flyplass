import java.util.*;

class Airport {
    private final int timeUnits;
    private final double averageArrivals, averageDepartures;
    private int arrivalAmount, departureAmount, rejectedAmount, freeTime, landingWaitTime, departureWaitTime;
    private final Queue<Flight> landingQueue;
    private final Queue<Flight> departureQueue;

    // Method getPoissonRandom from assignment text
    private static int getPoissonRandom(double mean) {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }

    public Airport(int timeUnits, double avg_arrivals, double avg_departures) {
        this.timeUnits = timeUnits;
        this.averageArrivals = avg_arrivals;
        this.averageDepartures = avg_departures;
        this.landingQueue = new LinkedList<>();
        this.departureQueue = new LinkedList<>();
    }

    public void printReport() {
        System.out.println("Simuleringen ferdig etter " + timeUnits + " tidsenheter.");
        System.out.println("Totalt antall fly behandlet: " + timeUnits);
        System.out.println("Antall fly landet : " + arrivalAmount);
        System.out.println("Antall fly tatt av: " + departureAmount);
        System.out.println("Antall fly avvist: " + rejectedAmount);
        System.out.println("Antall fly klare for landing: " + landingQueue.size());
        System.out.println("Antall fly klare til å ta av: " + departureQueue.size());
        double percentageFreeTime = ((double)freeTime / (double)timeUnits) * 100;
        System.out.println("Prosent ledig tid: " + percentageFreeTime);
        double averageLandingWaitTime = (((double)landingWaitTime / (double)(landingWaitTime + departureWaitTime)) * 100);
        double averageDepartureWaitTime = (((double)departureWaitTime / (double)(landingWaitTime + departureWaitTime)) * 100);
        System.out.println("Gj.snitt. ventetid, landing: " + averageLandingWaitTime + " tidsenheter.");
        System.out.println("Gj.snitt. ventetid, avgang: " + averageDepartureWaitTime + " tidsenheter.");
    }

    public void simulate() {
        for (int i = 0; i < timeUnits; i++) {
            int randomLandingAmount = getPoissonRandom(averageArrivals);
            for (int j = 0; j < randomLandingAmount; j++) {
                if (landingQueue.size() == 10) {
                    System.out.println("Landingskø er full.");
                    rejectedAmount++;
                } else {
                    landingQueue.add(new Flight());
                }
            }

            int randomDepartureAmount = getPoissonRandom(averageDepartures);
            for (int j = 0; j < randomDepartureAmount; j++) {
                if (departureQueue.size() == 10) {
                    System.out.println("Avgangskø er full.");
                    rejectedAmount++;
                } else {
                    departureQueue.add(new Flight());
                }
            }

            System.out.print((i + 1) + ": ");
            for (Flight flight : landingQueue) {
                flight.incrementWaitTime();
                System.out.println(flight.getMessage("landing"));
            }
            for (Flight flight : departureQueue) {
                flight.incrementWaitTime();
                System.out.println(flight.getMessage("avgang"));
            }

            if (!landingQueue.isEmpty()) {
                Flight f = landingQueue.poll();
                arrivalAmount++;
                landingWaitTime+=f.getWaitTime()-1;
                System.out.println("Fly " + f.getFlightNumber() + " landet, ventetid " + (f.getWaitTime() - 1) + " enheter");
            } else if (!departureQueue.isEmpty()) {
                Flight f = departureQueue.poll();
                departureAmount++;
                departureWaitTime+=f.getWaitTime()-1;
                System.out.println("Fly " + f.getFlightNumber() + " tatt av, ventetid " + (f.getWaitTime() - 1) + " enheter");
            } else {
                System.out.println("Flyplassen er tom.");
                freeTime++;
            }
        }
    }
}

class Flight {
    private static int flightCount = 0;
    private final int flightNumber;
    private int waitTime = 0;

    public void incrementWaitTime() {
        this.waitTime++;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    Flight() {
        // increase flight count every time new object is initialized, while setting flight number for current object
        this.flightNumber = flightCount++;
    }

    public String getMessage(String action)
    {
        return "Fly " + flightNumber + " klar for " + action;
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Velkommen til Halden Airport, tax-free butikken er dessverre stengt.");

        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.println("Hvor mange tidsenheter skal simuleringen gå?: ");
        int timeUnits = scanner.nextInt();

        System.out.println("Forventet antall ankomster pr. tidsenhet?: ");
        double averageArrivals = scanner.nextDouble();

        System.out.println("Forventet antall avganger pr. tidsenhet?: ");
        double averageDepartures = scanner.nextDouble();

        Airport airport = new Airport(timeUnits, averageArrivals, averageDepartures);
        airport.simulate();
        airport.printReport();
    }
}