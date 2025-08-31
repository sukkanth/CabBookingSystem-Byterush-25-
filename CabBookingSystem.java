import java.util.*;

class Ride {
    int id;
    String userPhone;
    String pickup;
    String drop;
    String cabType;
    int fare;
    String status;
    String driverName;
    boolean primeDiscount;

    Ride(int id, String userPhone, String pickup, String drop, String cabType, int fare, String driverName, boolean primeDiscount) {
        this.id = id;
        this.userPhone = userPhone;
        this.pickup = pickup;
        this.drop = drop;
        this.cabType = cabType;
        this.fare = fare;
        this.status = "Confirmed";
        this.driverName = driverName;
        this.primeDiscount = primeDiscount;
    }

    public String toString() {
        return "[RideID=" + id + ", User=" + userPhone + ", Driver=" + driverName +
                ", Cab=" + cabType + ", Fare=" + fare +
                ", Pickup=" + pickup + ", Drop=" + drop +
                ", Status=" + status +
                (primeDiscount ? " (Prime Discount Applied 🎉)" : "") + "]";
    }
}

class User {
    String name, phone, email, password;
    ArrayList<Ride> rides = new ArrayList<>();

    User(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }
}

class Driver {
    int id;
    String name;
    String cabType;
    boolean available;
    int earnings;

    Driver(int id, String name, String cabType) {
        this.id = id;
        this.name = name;
        this.cabType = cabType;
        this.available = true;
        this.earnings = 0;
    }

    public String toString() {
        return "[DriverID=" + id + ", Name=" + name + ", Cab=" + cabType +
                ", Available=" + available + ", Earnings=" + earnings + "]";
    }
}

public class CabBookingSystem {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Ride> rides = new ArrayList<>();
    static ArrayList<Driver> drivers = new ArrayList<>();

    static final String ADMIN_USER = "sukkanth";
    static final String ADMIN_PASS = "sukkanth07";

    static HashMap<String, Integer> placeDistances = new HashMap<>() {{
        put("Bus Stand", 5);
        put("Kongu College", 15);
        put("Theatre", 10);
        put("Train Station", 12);
        put("Park", 3);
        put("Hospital", 8);
    }};

    static int calculateFare(int distance, String cabType) {
        if (cabType.equalsIgnoreCase("sedan")) {
            return distance * 45;
        } else if (cabType.equalsIgnoreCase("suv")) {
            return distance * 30;
        } else if (cabType.equalsIgnoreCase("mini")) {
            return distance * 25;
        } else {
            return distance * 20;
        }
    }

    static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    static void signUp() {
        System.out.println("\n -- Sign Up --");
        System.out.print("Name : "); String name = sc.nextLine();
        System.out.print("Phone : "); String phone = sc.nextLine();
        System.out.print("Email : "); String email = sc.nextLine();
        System.out.print("Password : "); String password = sc.nextLine();

        if (users.containsKey(phone)) {
            System.out.println("User already exists with this phone number ! :( :(");
            return;
        }
        users.put(phone, new User(name, phone, email, password));
        System.out.println("Sign-up successful!  :) :)");
    }

    static void userLogin() {
        System.out.println("\n -- User Login --");
        System.out.print("Phone: "); String phone = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();

        if (users.containsKey(phone) && users.get(phone).password.equals(pass)) {
            System.out.println("Welcome " + users.get(phone).name + "!");
            userDashboard(phone);
        } else {
            System.out.println("Invalid Credentials!");
        }
    }

    static void userDashboard(String phone) {
        while (true) {
            System.out.println("\n-- User Dashboard --");
            System.out.println("1) Book Cab\n2) My Rides\n3) LogOut");
            System.out.print("Enter Choice: ");
            String ch = sc.nextLine();

            if (ch.equals("1")) {
                bookCab(phone);
            } else if (ch.equals("2")) {
                users.get(phone).rides.forEach(System.out::println);
            } else if (ch.equals("3")) {
                return;
            } else {
                System.out.println("Invalid Choice");
            }
        }
    }

    static void bookCab(String phone) {
        if (drivers.isEmpty()) {
            System.out.println("No drivers available ! :( :(");
            return;
        }

        System.out.println("\n---Select The Drop Location ----");
        placeDistances.keySet().forEach(System.out::println);

        System.out.print("Choose Drop Location: ");
        String drop = sc.nextLine();
        if (!placeDistances.containsKey(drop)) {
            System.out.println("Invalid place chosen!");
            return;
        }

        System.out.print("Pickup Location : ");
        String pickup = sc.nextLine();
        System.out.print("Cab Type (Mini/Suv/Sedan): ");
        String cabType = sc.nextLine();

        Driver assignedDriver = null;
        for (Driver d : drivers) {
            if (d.available && d.cabType.equalsIgnoreCase(cabType)) {
                assignedDriver = d;
                break;
            }
        }

        if (assignedDriver == null) {
            System.out.println("No driver available for this cab type!  :( :(");
            return;
        }

        int distance = placeDistances.get(drop);
        int fare = calculateFare(distance, cabType);

        int rideNumber = users.get(phone).rides.size() + 1;
        boolean primeDiscount = false;

        if (isPrime(rideNumber)) {
            int discount = fare / 2;
            fare -= discount;
            primeDiscount = true;
            System.out.println("🎉 Ride #" + rideNumber + " is PRIME → 50% Discount Applied!");
            System.out.println("Original Fare: " + (fare + discount) + " | Discount: -" + discount + " | Final Fare: " + fare);
        }

        System.out.println("Estimated Fare: " + fare + " for " + distance + " km");
        System.out.println("Confirm Booking? [yes/no]");

        if (!sc.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("Booking canceled! :(  :(");
            return;
        }

        int rideId = rides.size() + 1;
        Ride ride = new Ride(rideId, phone, pickup, drop, cabType, fare, assignedDriver.name, primeDiscount);
        rides.add(ride);
        users.get(phone).rides.add(ride);

        assignedDriver.available = false;
        assignedDriver.earnings += fare;

        System.out.println("Booking confirmed! :) :)" + ride);
    }

    static void adminLogin() {
        System.out.println("\n -- Admin Login -- ");
        System.out.print("Username: "); String user = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();

        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            System.out.println("Admin Login Successful");
            adminDashboard();
        } else {
            System.out.println("Invalid Admin credentials :( :(");
        }
    }

    static void adminDashboard() {
        while (true) {
            System.out.println("\n-- ADMIN DASHBOARD --");
            System.out.println("1) View Rides\n2) Reports\n3) Manage Drivers\n4) Logout");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            if (choice.equals("1")) {
                rides.forEach(System.out::println);
            } else if (choice.equals("2")) {
                generateReports();
            } else if (choice.equals("3")) {
                manageDrivers();
            } else if (choice.equals("4")) {
                return;
            } else {
                System.out.println("Invalid!!!!!!!");
            }
        }
    }

    static void manageDrivers() {
        while (true) {
            System.out.println("\n-- DRIVER MANAGEMENT --");
            System.out.println("1) Add Driver\n2) Update Driver\n3) Delete Driver\n4) View Drivers\n5) Back");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine();

            if (ch.equals("1")) {
                System.out.print("Driver Name: "); String name = sc.nextLine();
                System.out.print("Cab Type: "); String cabType = sc.nextLine();
                int id = drivers.size() + 1;
                drivers.add(new Driver(id, name, cabType));
                System.out.println("Driver added!");
            } else if (ch.equals("2")) {
                System.out.print("Enter Driver ID: ");
                int id = Integer.parseInt(sc.nextLine());
                for (Driver d : drivers) {
                    if (d.id == id) {
                        System.out.print("New Cab Type: "); d.cabType = sc.nextLine();
                        System.out.print("Available? (true/false): "); d.available = Boolean.parseBoolean(sc.nextLine());
                        System.out.println("Driver updated!");
                        break;
                    }
                }
            } else if (ch.equals("3")) {
                System.out.print("Enter Driver ID: ");
                int id = Integer.parseInt(sc.nextLine());
                drivers.removeIf(d -> d.id == id);
                System.out.println("Driver removed!");
            } else if (ch.equals("4")) {
                drivers.forEach(System.out::println);
            } else if (ch.equals("5")) {
                return;
            } else {
                System.out.println("Invalid");
            }
        }
    }

    static void generateReports() {
        int completed = (int) rides.stream().filter(r -> r.status.equals("Confirmed")).count();
        int cancelled = (int) rides.stream().filter(r -> r.status.equals("Cancelled")).count();
        int totalEarnings = drivers.stream().mapToInt(d -> d.earnings).sum();

        System.out.println("\n---- REPORTS ----");
        System.out.println("Total rides: " + rides.size());
        System.out.println("Completed rides: " + completed);
        System.out.println("Cancelled rides: " + cancelled);
        System.out.println("Total Earnings: " + totalEarnings);
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n -- CAB BOOKING SYSTEM --");
            System.out.println("1) SIGN UP\n2) USER LOGIN\n3) ADMIN LOGIN\n4) EXIT");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine();

            if (ch.equals("1")) {
                signUp();
            } else if (ch.equals("2")) {
                userLogin();
            } else if (ch.equals("3")) {
                adminLogin();
            } else if (ch.equals("4")) {
                System.out.println("Bye Bye !");
                return;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }
}
