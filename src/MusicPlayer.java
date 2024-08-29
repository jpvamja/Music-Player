import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

// .wav audio file downalod and add in database mannunally
// Main class
public class MusicPlayer {
    // create static scanner and method to take input by exception handling.
    static Scanner sc = new Scanner(System.in);
    static Connection con = null;

    public static int getInt() {
        while (true) {
            try {
                int s = sc.nextInt();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                sc.nextLine();
            }
        }
    }

    public static String getStringLine() {
        while (true) {
            try {
                String s = sc.nextLine();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                sc.nextLine();
            }
        }
    }

    public static String getString() {
        while (true) {
            try {
                String s = sc.next();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                sc.nextLine();
            }
        }
    }

    public static double getDouble() {
        while (true) {
            try {
                double s = sc.nextDouble();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                sc.nextLine();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        StackUsingLinkedList stack = new StackUsingLinkedList();
        // Connection code for connect with databse
        String dburl = "jdbc:mysql://localhost:3306/Musicify";
        String dbuser = "root";
        String dbpass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        con = DriverManager.getConnection(dburl, dbuser, dbpass);
        // System.out.println((con != null) ? "Connection Done" : "Connection Fail");

        System.out.println("============= Welcome To Musicify Station ===========");

        // Declare variables for loops
        boolean verify = true;
        boolean login = false;

        // Main Menu Loop
        while (verify) {

            // Main Menu Choices
            System.out.println("========================== Main Menu ============================");
            System.out.println("Enter Your Choice");
            System.out.println("1.SignUp");
            System.out.println("2.LogIn");
            System.out.println("3.Get Premium Membership");
            System.out.println("4.Delete Account");
            System.out.println("5.Exit From Main Menu");
            int choice1 = sc.nextInt();

            switch (choice1) {
                case 1: {
                    // 1.For SignUp

                    // Declare variables for loops
                    boolean bmainloop = true;
                    boolean bemaildone = true;
                    boolean bpassworddone = true;

                    // Declare variables for get data
                    String username = null;
                    String emailid = null;
                    String password = null;

                    // Sign Up code
                    while (bmainloop) {

                        System.out.println("Enter Username");
                        username = getString();

                        // Loop for get valid emailid and check already exist or not
                        while (bemaildone) {

                            System.out.print("Enter Email Address : ");
                            emailid = getString();

                            int atcheck = emailid.indexOf('@');
                            int dotcheck = emailid.lastIndexOf('.');
                            if (atcheck != -1 && dotcheck != -1) {
                                String domain = emailid.substring(atcheck);
                                if (domain.equalsIgnoreCase("@gmail.com")) {
                                    String localPart = emailid.substring(0, atcheck);
                                    if (localPart.length() > 4) {
                                        String checkemailsql = "select * from user where emailid=?";
                                        PreparedStatement checkmailpst = con.prepareStatement(checkemailsql);
                                        checkmailpst.setString(1, emailid);
                                        ResultSet rsemail = checkmailpst.executeQuery();
                                        if (rsemail.next()) {
                                            System.out.println("Email address is already taken.");
                                        } else {
                                            bemaildone = false;
                                        }
                                    } else {
                                        System.out.println(
                                                "Username should be atleast 5 characters.");
                                    }
                                } else {
                                    System.out
                                            .println("only @gmail.com available.");
                                }
                            } else {
                                System.out.println("Please enter valid email address.");
                            }
                        }

                        // Loop for get valid password
                        while (bpassworddone) {

                            System.out.print(
                                    "Enter Password (atleast 8 characters, one uppercase, one lowercase, and one special character) : ");
                            password = getString();

                            if (password.length() < 8) {
                                System.out.println("Password must be atleast 8 characters long.");
                                continue;
                            }
                            boolean hasUppercase = false;
                            for (char c : password.toCharArray()) {
                                if (Character.isUpperCase(c)) {
                                    hasUppercase = true;
                                    break;
                                }
                            }
                            if (!hasUppercase) {
                                System.out.println("Password must contain atleast one uppercase letter.");
                                continue;
                            }
                            boolean hasLowercase = false;
                            for (char c : password.toCharArray()) {
                                if (Character.isLowerCase(c)) {
                                    hasLowercase = true;
                                    break;
                                }
                            }
                            if (!hasLowercase) {
                                System.out.println("Password must contain atleast one lowercase letter.");
                                continue;
                            }
                            boolean hasSpecialChar = false;
                            String specialChars = "!@#$%^&*()_+~`|}{[]:;?><,./-=";
                            for (char c : password.toCharArray()) {
                                if (specialChars.indexOf(c) != -1) {
                                    hasSpecialChar = true;
                                    break;
                                }
                            }
                            if (!hasSpecialChar) {
                                System.out.println("Password must contain atleast one special character.");
                                continue;
                            }
                            bpassworddone = false;
                        }

                        // Add emailid and password in database
                        String storedatasql = "insert into user (username,emailid,password,premium) values(?,?,?,?)";
                        PreparedStatement pststore = con.prepareStatement(storedatasql);
                        pststore.setString(1, username);
                        pststore.setString(2, emailid);
                        pststore.setString(3, password);
                        pststore.setInt(4, 0);
                        pststore.executeUpdate();

                        System.out.println("Sign Up Successfully.");

                        bmainloop = false;
                    }

                    verify = true;
                    break;
                }

                case 2: {
                    // 2.For Login

                    // Declare variables for loops

                    boolean bmainloop = true;
                    boolean bemaildone = true;
                    boolean bpassworddone = true;
                    boolean emailfound = true;

                    // Declare variables for get data
                    String emailid = null;
                    String password = null;
                    String temppassword = null;

                    // Log In code
                    mainloop: while (bmainloop) {

                        // Loop for get valid emailid and check exist or not
                        while (bemaildone) {
                            System.out.print("Enter Email Address : ");
                            emailid = getString();

                            int atcheck = emailid.indexOf('@');
                            int dotcheck = emailid.lastIndexOf('.');
                            if (atcheck != -1 && dotcheck != -1) {
                                String domain = emailid.substring(atcheck);
                                if (domain.equalsIgnoreCase("@gmail.com")) {
                                    String localPart = emailid.substring(0, atcheck);
                                    if (localPart.length() > 4) {
                                        String checkemailsql = "select * from user where emailid=?";
                                        PreparedStatement checkmailpst = con.prepareStatement(checkemailsql);
                                        checkmailpst.setString(1, emailid);
                                        ResultSet rsemail = checkmailpst.executeQuery();
                                        if (rsemail.next()) {
                                            bemaildone = false;
                                        } else {
                                            System.out.println("First sign up then login.");
                                            emailfound = false;
                                            break mainloop;
                                        }
                                    } else {
                                        System.out.println(
                                                "Username should be atleast 5 characters.");
                                    }
                                } else {
                                    System.out
                                            .println("Only @gmail.com available.");
                                }
                            } else {
                                System.out.println("Please enter valid email address.");
                            }
                        }

                        // if email found
                        if (emailfound) {

                            // Loop for get valid password
                            String sql1 = "select password from user where emailid = ? ";
                            PreparedStatement checkemail = con.prepareStatement(sql1);
                            checkemail.setString(1, emailid);
                            ResultSet r = checkemail.executeQuery();
                            if (r.next()) {
                                temppassword = r.getString(1);
                            }

                            while (bpassworddone) {
                                System.out.print("Enter Password : ");
                                password = getString();
                                if (temppassword.equals(password)) {
                                    System.out.println("Login Successfully.");
                                    bpassworddone = false;
                                    bmainloop = false;
                                    login = true;
                                } else {
                                    System.out.println("Invalid password.");
                                    bpassworddone = false;
                                    bmainloop = false;
                                }
                            }
                        } else {
                            verify = true;
                            bmainloop = false;
                        }

                        while (login) {
                            System.out.println("===========================Welcome Menu============================");
                            System.out.println("Enter Your Choice");
                            System.out.println("1.Explore All Songs Details");
                            System.out.println("2.Explore Songs By Types");
                            System.out.println("3.Listen Music");
                            System.out.println("4.Explore All Playlists");
                            System.out.println("5.Create Playlist");
                            System.out.println("6.Add Song In Playlist");
                            System.out.println("7.Remove Song From Playlist");
                            System.out.println("8.Delete Playlist");
                            System.out.println("9.Show Playing History");
                            System.out.println("10.Logout");
                            int choice2 = sc.nextInt();

                            switch (choice2) {

                                case 1: {
                                    AllSong show = new AllSong(con);
                                    List<Song> songList = new ArrayList<>();
                                    show.showList(songList);
                                    break;
                                }
                                case 2: {
                                    category ct = new category(con);
                                    System.out.println("Which Type Of Song Would you Like To Explore");
                                    System.out.println("1.Bollywood");
                                    System.out.println("2.Gujarati");
                                    System.out.println("3.Punjabi");
                                    System.out.println("4.Hollywood");
                                    int ch1 = getInt();
                                    switch (ch1) {
                                        case 1: {
                                            ct.bollywood();
                                            break;
                                        }
                                        case 2: {
                                            ct.gujarati();
                                            break;
                                        }
                                        case 3: {
                                            ct.punjabi();
                                            break;
                                        }
                                        case 4: {
                                            ct.hollywood();
                                            break;
                                        }
                                        default: {
                                            System.out.println("Invalid Input");
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 3: {
                                    player ply = new player(con);

                                    String checkemailsql = "select * from user where emailid=?";
                                    PreparedStatement checkmailpst = con.prepareStatement(checkemailsql);
                                    checkmailpst.setString(1, emailid);
                                    ResultSet rsemail = checkmailpst.executeQuery();
                                    int premium = 0;
                                    if (rsemail.next()) {
                                        premium = rsemail.getInt(4);
                                    }
                                    boolean check = false;
                                    if (premium == 1) {
                                        check = true;
                                    }

                                    System.out.println("Enter id");
                                    int id = getInt();
                                    String sqlget = "select nname from nsong where nid = ?";
                                    PreparedStatement pstget = con.prepareStatement(sqlget);
                                    pstget.setInt(1, id);
                                    ResultSet rsget = pstget.executeQuery();
                                    String song = null;
                                    if (rsget.next()) {
                                        song = rsget.getString(1);
                                    }

                                    String filepath = null;
                                    if (!check) {
                                        // add Your song path normal song
                                        filepath = "";
                                    } else {
                                        // add Your song path premium song
                                        filepath = "";
                                    }

                                    stack.push(song);
                                    ply.PlayMusic(filepath);

                                    break;
                                }
                                case 4: {
                                    PlatlistOperation po = new PlatlistOperation(con, emailid);
                                    po.displayplaylist();
                                    break;
                                }
                                case 5: {
                                    PlatlistOperation po = new PlatlistOperation(con, emailid);
                                    po.createPlaylist();
                                    break;

                                }
                                case 6: {
                                    PlatlistOperation po = new PlatlistOperation(con, emailid);
                                    po.addsongpl();
                                    break;

                                }
                                case 7: {
                                    PlatlistOperation po = new PlatlistOperation(con, emailid);
                                    po.removesongpl();
                                    break;

                                }
                                case 8: {
                                    PlatlistOperation po = new PlatlistOperation(con, emailid);
                                    po.deletepl();
                                    break;

                                }

                                case 9: {
                                    stack.display();
                                    break;

                                }
                                case 10: {
                                    login = false;
                                    break;

                                }
                                default: {
                                    System.out.println("Invalid Choice");
                                    break;
                                }
                            }
                        }

                    }
                    break;
                }
                case 3: {
                    // 3.get purchase

                    // Declare variables for loops
                    boolean bmainloop = true;
                    boolean bemaildone = true;
                    boolean bpassworddone = true;
                    boolean emailfound = true;
                    boolean bupidone = true;

                    // Declare variables for get data
                    String emailid = null;
                    String password = null;
                    String temppassword = null;
                    String upiid = null;
                    int userotp = 0;
                    int otp = 0;

                    // Set autocommit for transaction code
                    con.setAutoCommit(false);

                    // Premium purchase code
                    mainloop: while (bmainloop) {

                        // Loop for get valid emailid and check already premium or not
                        while (bemaildone) {
                            System.out.print("Enter Email Address : ");
                            emailid = getString();

                            int atcheck = emailid.indexOf('@');
                            int dotcheck = emailid.lastIndexOf('.');
                            if (atcheck != -1 && dotcheck != -1) {
                                String domain = emailid.substring(atcheck);
                                if (domain.equalsIgnoreCase("@gmail.com")) {
                                    String localPart = emailid.substring(0, atcheck);
                                    if (localPart.length() > 4) {
                                        String checkemailsql = "select * from user where emailid=?";
                                        PreparedStatement checkmailpst = con.prepareStatement(checkemailsql);
                                        checkmailpst.setString(1, emailid);
                                        ResultSet rsemail = checkmailpst.executeQuery();
                                        if (rsemail.next()) {
                                            int premium1 = rsemail.getInt(4);
                                            if (premium1 != 1) {
                                                bemaildone = false;
                                            } else {
                                                System.out.println("You have already premium account.");
                                                emailfound = false;
                                                bemaildone = false;
                                                break mainloop;
                                            }
                                        } else {
                                            System.out.println("You have no account in musicify.");
                                            emailfound = false;
                                            break mainloop;
                                        }
                                    } else {
                                        System.out.println(
                                                "Username should be at least 5 characters.");
                                    }
                                } else {
                                    System.out
                                            .println("Only @gmail.com available.");
                                }
                            } else {
                                System.out.println("Please enter valid email address.");
                            }
                        }

                        // Email Available
                        if (emailfound) {

                            // Store password for verification
                            String sql1 = "select password from user where emailid = ? ";
                            PreparedStatement checkemail = con.prepareStatement(sql1);
                            checkemail.setString(1, emailid);
                            ResultSet r = checkemail.executeQuery();
                            if (r.next()) {
                                temppassword = r.getString(1);
                            }

                            // Loop for match password
                            while (bpassworddone) {
                                System.out.print("Enter Password : ");
                                password = getString();

                                // Password Match Then Payment Code
                                if (temppassword.equals(password)) {
                                    System.out.println("Login Successfully.");

                                    while (bupidone) {
                                        System.out.print("Enter UPI Id : ");
                                        upiid = getString();

                                        // UPI Id validation
                                        int atcheck = upiid.indexOf('@');
                                        if (atcheck != -1) {
                                            String domain = upiid.substring(atcheck, (atcheck + 3));
                                            if (domain.equalsIgnoreCase("@ok")) {
                                                String localPart = upiid.substring(0, atcheck);
                                                if (localPart.length() > 4) {
                                                    Random random = new Random();
                                                    otp = 100000 + random.nextInt(900000);
                                                    String otpString = String.valueOf(otp);
                                                    JOptionPane.showMessageDialog(null, "Your OTP is: " + otpString,
                                                            "OTP",
                                                            JOptionPane.INFORMATION_MESSAGE);
                                                    System.out.println("Enter OTP");
                                                    userotp = getInt();
                                                    if (userotp == otp) {
                                                        System.out.println("Do you want to pay? (yes/no)");
                                                        String answer = getString();
                                                        if (answer.equalsIgnoreCase("yes")) {
                                                            String premiumsql = "update user set premium = 1 where emailid = ?";
                                                            PreparedStatement premiuumpst = con
                                                                    .prepareStatement(premiumsql);
                                                            premiuumpst.setString(1, emailid);
                                                            int checkdel = premiuumpst.executeUpdate();
                                                            if (checkdel > 0) {
                                                                System.out.println("Payment Successfully.");
                                                                System.out.println("You Are Premium Member");
                                                                con.commit();
                                                                bupidone = false;
                                                                bpassworddone = false;
                                                                bmainloop = false;
                                                            }

                                                        } else if (answer.equalsIgnoreCase("no")) {
                                                            System.out.println("Payment cancelled.");
                                                            con.rollback();
                                                            bupidone = false;
                                                            bpassworddone = false;
                                                            bmainloop = false;

                                                        } else {
                                                            System.out.println(
                                                                    "Invalid answer. Please enter 'yes' or 'no'.");
                                                        }
                                                    } else {
                                                        System.out.println("OTP Not Match");
                                                        bupidone = false;
                                                        bpassworddone = false;
                                                        bmainloop = false;
                                                    }

                                                } else {
                                                    System.out.println(
                                                            "Please Enter Valid UPI Id (username should be at least 5 characters).");
                                                }
                                            } else {
                                                System.out
                                                        .println(
                                                                "Please Enter Valid Gmail Address (only @okbankname is allowed).");
                                            }
                                        } else {
                                            System.out.println("Please Enter Valid UPI Id.");
                                        }
                                    }
                                    bpassworddone = false;
                                } else {
                                    System.out.println("Invalid Password.");
                                    bpassworddone = false;
                                }
                            }

                        } else {
                            verify = true;
                            bmainloop = false;
                        }

                    }

                    con.setAutoCommit(true);
                    break;

                }

                case 4: {
                    // 4.For Delete Account

                    // Declare variables for loops
                    boolean bmainloop = true;
                    boolean bemaildone = true;
                    boolean bpassworddone = true;
                    boolean emailfound = true;

                    // Declare variables for get data
                    String emailid = null;
                    String password = null;
                    String temppassword = null;

                    // Set autocommit for deletion code
                    con.setAutoCommit(false);

                    mainloop: while (bmainloop) {

                        // Loop for get valid emailid and check already exist or not
                        while (bemaildone) {
                            System.out.print("Enter Email Address : ");
                            emailid = getString();

                            int atcheck = emailid.indexOf('@');
                            int dotcheck = emailid.lastIndexOf('.');
                            if (atcheck != -1 && dotcheck != -1) {
                                String domain = emailid.substring(atcheck);
                                if (domain.equalsIgnoreCase("@gmail.com")) {
                                    String localPart = emailid.substring(0, atcheck);
                                    if (localPart.length() > 4) {
                                        String checkemailsql = "select * from user where emailid=?";
                                        PreparedStatement checkmailpst = con.prepareStatement(checkemailsql);
                                        checkmailpst.setString(1, emailid);
                                        ResultSet rsemail = checkmailpst.executeQuery();
                                        if (rsemail.next()) {
                                            bemaildone = false;
                                        } else {
                                            System.out.println("You Have No Account In Musicify.");
                                            emailfound = false;
                                            break mainloop;
                                        }
                                    } else {
                                        System.out.println(
                                                "Username should be at least 5 characters.");
                                    }
                                } else {
                                    System.out
                                            .println("Only @gmail.com available.");
                                }
                            } else {
                                System.out.println("Please enter valid email address.");
                            }
                        }

                        // Email Available
                        if (emailfound) {

                            // Store password for verification
                            String sql1 = "select password from user where emailid = ? ";
                            PreparedStatement checkemail = con.prepareStatement(sql1);
                            checkemail.setString(1, emailid);
                            ResultSet r = checkemail.executeQuery();
                            if (r.next()) {
                                temppassword = r.getString(1);
                            }

                            // Loop for match password
                            while (bpassworddone) {
                                System.out.print("Enter Password : ");
                                password = getString();

                                // Password Match Then Delete Code
                                if (temppassword.equals(password)) {
                                    System.out.println("Do you want to delete your account? (yes/no)");
                                    String answer = getString().trim();
                                    if (answer.equalsIgnoreCase("yes")) {
                                        try {
                                            String deleteaccsql1 = "delete from user where emailid = ?";
                                            PreparedStatement deleteaccpst1 = con.prepareStatement(deleteaccsql1);
                                            deleteaccpst1.setString(1, emailid);
                                            int checkdel1 = deleteaccpst1.executeUpdate();

                                            String deleteaccsql3 = "select playlistname from userplaylists where username =?";
                                            PreparedStatement deleteaccpst3 = con.prepareStatement(deleteaccsql3);
                                            deleteaccpst3.setString(1, emailid);
                                            ResultSet rs3 = deleteaccpst3.executeQuery();

                                            while (rs3.next()) {
                                                String playlistname = rs3.getString(1);
                                                String deleteaccsql2 = "drop table " + playlistname + "";
                                                PreparedStatement deleteaccpst2 = con.prepareStatement(deleteaccsql2);
                                                deleteaccpst2.executeUpdate();

                                            }
                                            try {
                                                String deleteaccsql4 = "delete from userplaylists where username = ?";
                                                PreparedStatement deleteaccpst4 = con.prepareStatement(deleteaccsql4);
                                                deleteaccpst4.setString(1, emailid);
                                                deleteaccpst4.executeUpdate();
                                            } catch (Exception e) {

                                            }
                                            if (checkdel1 > 0) {
                                                System.out.println("Your Account Deleted Successfully.");
                                                con.commit();
                                                bpassworddone = false;
                                                bmainloop = false;
                                            }
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }

                                    } else if (answer.equalsIgnoreCase("no")) {
                                        System.out.println("Account deletion cancelled.");
                                        con.rollback();
                                        bpassworddone = false;
                                        bmainloop = false;

                                    } else {
                                        System.out.println("Invalid answer. Please enter 'yes' or 'no'.");
                                    }
                                } else {
                                    System.out.println("Invalid Password.");
                                    bpassworddone = false;
                                    bmainloop = false;
                                }
                            }
                        } else {
                            verify = true;
                            bmainloop = false;
                        }

                    }

                    con.setAutoCommit(true);
                    break;
                }

                case 5: {
                    // 5.For Exit From Main Menu
                    System.exit(1);
                    break;
                }

                default: {
                    System.out.println("Invalid Choice");
                    break;
                }
            }
        }
    }
}

// Class player to paly song
class player {

    private Connection connection;

    public player(Connection con) {
        this.connection = con;
    }

    static Scanner inputsScanner = new Scanner(System.in);

    public static String getString() {
        while (true) {
            try {
                String s = inputsScanner.next();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                inputsScanner.nextLine();
            }
        }
    }

    // Method to play song
    public void PlayMusic(String filepath) {

        try {
            System.out.println("Playing Music....");
            // COde for read audio file from pc
            File file = new File(filepath);
            if (file.exists()) {
                // Inbuild classes for read audio file
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                String response = "";

                while (!response.equals("Q")) {
                    System.out.println("***************************************************");
                    System.out.println("P = Play, S = Pause, R = Reset, Q = Quit");
                    System.out.print("Enter your choice: ");

                    response = getString();
                    response = response.toUpperCase();

                    switch (response) {
                        case ("P"):
                            clip.start();
                            break;
                        case ("S"):
                            clip.stop();
                            break;
                        case ("R"):
                            clip.setMicrosecondPosition(0);
                            break;
                        case ("Q"):
                            clip.close();
                            break;
                        default:
                            System.out.println("Not a valid response");
                    }

                }
                System.out.println("Byeeee!");
            } else {
                System.out.println("File Not Found: " + filepath);
            }
        } catch (LineUnavailableException e) {
            System.out.println("LineUnavailableException: Unable to play audio.");
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            System.out.println("UnsupportedAudioFileException: The audio file format is not supported.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException: An error occurred while accessing the audio file.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}

// Class Song to give a identity
class Song {

    int id;
    String name;
    String artist;
    String type;

    // constructor
    public Song(int id, String name, String artist, String type) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.type = type;

    }
}

// Class AllSong for get song details
class AllSong {

    private Connection connection;

    public AllSong(Connection con) {
        this.connection = con;
    }

    // method print all songs available in database
    public void showList(List<Song> songList) {
        try {
            if (songList.isEmpty()) {
                String sql = "Select * from nsong";
                PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    int nid = rs.getInt(1);
                    String nname = rs.getString(2);
                    String nartist = rs.getString(3);
                    String ntype = rs.getString(4);
                    Song addSong = new Song(nid, nname, nartist, ntype);
                    songList.add(addSong);
                }
            }
            System.out.println("******************************************************");
            for (Song x : songList) {
                System.out.println(x.id + "." + x.name + " by " + x.artist);
            }
            System.out.println("******************************************************");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Class category to get song by types
class category {
    private Connection connection;

    public category(Connection con) {
        this.connection = con;
    }

    // Print bollywood songs
    public void bollywood() throws SQLException {
        try {
            String type1 = "Bollywood";
            String sqlbw = "select nid,nname,nartist,ntype from nsong where ntype=?";
            PreparedStatement pstbw = connection.prepareStatement(sqlbw);
            pstbw.setString(1, type1);
            ResultSet rsbw = pstbw.executeQuery();
            System.out.println("******************************************************");
            while (rsbw.next()) {
                System.out.println(rsbw.getInt(1) + "." + rsbw.getString(2) + " By " + rsbw.getString(3));
            }
            System.out.println("******************************************************");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Print gujarati songs
    public void gujarati() {
        try {
            String type2 = "Gujarati";
            String sqlgj = "select nid,nname,nartist,ntype from nsong where ntype=?";
            PreparedStatement pstgj = connection.prepareStatement(sqlgj);
            pstgj.setString(1, type2);
            ResultSet rsgj = pstgj.executeQuery();
            System.out.println("******************************************************");
            while (rsgj.next()) {
                System.out.println(rsgj.getInt(1) + "." + rsgj.getString(2) + " By " + rsgj.getString(3));
            }
            System.out.println("******************************************************");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Print punjabi songs
    public void punjabi() {
        try {
            String type3 = "Punjabi";
            String sqlpb = "select nid,nname,nartist,ntype from nsong where ntype=?";
            PreparedStatement pstpb = connection.prepareStatement(sqlpb);
            pstpb.setString(1, type3);
            ResultSet rspb = pstpb.executeQuery();
            System.out.println("******************************************************");
            while (rspb.next()) {
                System.out.println(rspb.getInt(1) + "." + rspb.getString(2) + " By " + rspb.getString(3));
            }
            System.out.println("******************************************************");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Print hollywood songs
    public void hollywood() {
        try {
            String type4 = "Hollywood";
            String sqlhw = "select nid,nname,nartist,ntype from nsong where ntype=?";
            PreparedStatement psthw = connection.prepareStatement(sqlhw);
            psthw.setString(1, type4);
            ResultSet rshw = psthw.executeQuery();
            System.out.println("******************************************************");
            while (rshw.next()) {
                System.out.println(rshw.getInt(1) + "." + rshw.getString(2) + " By " + rshw.getString(3));
            }
            System.out.println("******************************************************");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

// Class for all playlists operation
class PlatlistOperation {

    static Scanner inputScanner = new Scanner(System.in);
    private Connection connection;
    private String emailidoperation;

    public static String getString() {
        while (true) {
            try {
                String s = inputScanner.next();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                inputScanner.nextLine();
            }
        }
    }

    public static int getInt() {
        while (true) {
            try {
                int s = inputScanner.nextInt();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                inputScanner.nextLine();
            }
        }
    }

    public static String getStringLine() {
        while (true) {
            try {
                String s = inputScanner.nextLine();
                return s;
            } catch (Exception e) {
                System.out.println("INVALID INPUT.");
                inputScanner.nextLine();
            }
        }
    }

    public PlatlistOperation(Connection con, String emailid) {
        this.connection = con;
        this.emailidoperation = emailid;
    }

    // Method to print palylists
    public void displayplaylist() {

        while (true) {
            System.out.println("We Have Already This Playlists Available.");
            System.out.println("Hindi");
            System.out.println("English");
            System.out.println("Gujarati");
            System.out.println("Punjabi");
            try {
                // get user palylist from database
                String sql99 = "select playlistname from userplaylists where username = ?";
                PreparedStatement ps99 = connection.prepareStatement(sql99);
                ps99.setString(1, emailidoperation);
                ResultSet rs99 = ps99.executeQuery();
                ResultSet rstemp = rs99;
                if (rstemp.next()) {
                    System.out.println("Your Owm Playlist");
                    while (rs99.next()) {
                        System.out.println(rs99.getString(1));
                    }
                }

            } catch (Exception e) {
                System.out.println("You Don't Own Any Playlist");
            }

            System.out.println("Enter The Playlist Name.");
            String playlistname = getString();
            playlistname = playlistname.toLowerCase();
            // print palylsit details
            try {
                String sqlprint = "select pid,pname,partist from " + playlistname + "";
                PreparedStatement psprint = connection.prepareStatement(sqlprint);
                ResultSet rsprint = psprint.executeQuery();
                System.out.println("******************************************************");
                while (rsprint.next()) {
                    System.out.println(rsprint.getInt(1) + "." + rsprint.getString(2) + " By " + rsprint.getString(3));
                }
                break;
            } catch (Exception e) {
                System.out.println("wrong input.");
            }
        }

    }

    // Method to create playlist
    public void createPlaylist() {
        try {
            String playlist = null;

            boolean checkplyalist = true;
            // Check for playlsit exist or not
            while (checkplyalist) {
                System.out.println("Enter Playlist Name");
                playlist = getString();
                String checkplaylistsql = "select count(*) from information_schema.tables WHERE table_schema = 'musicify' AND table_name = ?";
                PreparedStatement playlistpst = connection.prepareStatement(checkplaylistsql);
                playlistpst.setString(1, playlist);
                ResultSet rs1 = playlistpst.executeQuery();
                if (rs1.next() && rs1.getInt(1) > 0) {
                    System.out.println("Playlist Is Already Created.");
                } else {
                    checkplyalist = false;
                }
            }

            // Create palylist in database
            String createTable = "create table " + playlist
                    + " (pid int NOT NULL PRIMARY KEY, pname varchar(30), partist varchar(30), ptype varchar(30))";
            PreparedStatement pst2 = connection.prepareStatement(createTable);
            pst2.executeUpdate();

            String insertplaylist = "insert into userplaylists values(?,?)";
            PreparedStatement pstinsertpl = connection.prepareStatement(insertplaylist);
            pstinsertpl.setString(1, emailidoperation);
            pstinsertpl.setString(2, playlist);
            pstinsertpl.executeUpdate();

            boolean checkid = true;
            while (checkid) {
                String sql = "select nid,nname,nartist,ntype from nsong where nid=?";
                PreparedStatement pst = connection.prepareStatement(sql);

                String sql1 = "insert into " + playlist + " values(?,?,?,?)";

                // take song from user
                while (true) {
                    System.out.println("Enter Id Of Song");
                    System.out.println("You Want To Stop Adding Enter 0.");
                    int id = getInt();
                    if (id == 0) {
                        checkid = false;
                        break;
                    }
                    if (id > 0 && id < 13) {
                        pst.setInt(1, id);
                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            int pid = rs.getInt(1);
                            String pname = rs.getString(2);
                            String partist = rs.getString(3);
                            String ptype = rs.getString(4);

                            PreparedStatement pst1 = connection.prepareStatement(sql1);
                            pst1.setInt(1, pid);
                            pst1.setString(2, pname);
                            pst1.setString(3, partist);
                            pst1.setString(4, ptype);
                            int r = pst1.executeUpdate();
                            if (r > 0) {
                                System.out.println("Song Added To Playlist");
                            }
                        }
                        break;
                    } else {
                        System.out.println("Enter Valid Id.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Method to addsong in playlist
    public void addsongpl() {
        try {
            String playlist = null;

            // Check for palylist name exist or not
            boolean checkplyalist = true;
            while (checkplyalist) {
                System.out.println("Enter Playlist Name");
                playlist = getString();
                String checkplaylistsql = "select count(*) from information_schema.tables WHERE table_schema = 'musicify' AND table_name = ?";
                PreparedStatement playlistpst = connection.prepareStatement(checkplaylistsql);
                playlistpst.setString(1, playlist);
                ResultSet rs1 = playlistpst.executeQuery();
                if (rs1.next() && rs1.getInt(1) > 0) {
                    checkplyalist = false;
                } else {
                    System.out.println("No playlist Exist For This Name.");
                }
            }

            // Take song id
            boolean checkid = true;

            try {
                while (checkid) {
                    String sql = "select nid,nname,nartist,ntype from nsong where nid=?";
                    PreparedStatement pst = connection.prepareStatement(sql);

                    String sql1 = "insert into " + playlist + " values(?,?,?,?)";
                    System.out.println("Enter Id Of Song");
                    int id = getInt();
                    if (id == 0) {
                        checkid = false;
                        break;
                    }
                    if (id > 0 && id < 13) {
                        pst.setInt(1, id);
                        ResultSet rs = pst.executeQuery();
                        while (rs.next()) {
                            int pid = rs.getInt(1);
                            String pname = rs.getString(2);
                            String partist = rs.getString(3);
                            String ptype = rs.getString(4);

                            PreparedStatement pst1 = connection.prepareStatement(sql1);
                            pst1.setInt(1, pid);
                            pst1.setString(2, pname);
                            pst1.setString(3, partist);
                            pst1.setString(4, ptype);
                            int r = pst1.executeUpdate();
                            if (r > 0) {
                                System.out.println("Song Added To Playlist");
                            }
                        }
                        break;
                    } else {
                        System.out.println("Enter Valid Id.");
                    }
                }
            } catch (Exception e)

            {
                System.out.println("Song Already in Playlist");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Method to remove song from playlist
    public void removesongpl() {
        // check for palylist exist or not
        try {
            String playlistName = null;
            boolean checkplyalist = true;
            while (checkplyalist) {
                System.out.println("Enter Playlist Name");
                playlistName = getString().trim();
                String checkplaylistsql = "select count(*) from information_schema.tables WHERE table_schema = 'musicify' AND table_name = ?";
                PreparedStatement playlistpst = connection.prepareStatement(checkplaylistsql);
                playlistpst.setString(1, playlistName);
                ResultSet rs1 = playlistpst.executeQuery();
                if (rs1.next() && rs1.getInt(1) > 0) {
                    checkplyalist = false;
                } else {
                    System.out.println("No playlist Exist For This Name.");
                }
            }

            // take song id and delete in database
            System.out.println("Enter Song Id Which You Want To Remove");
            int idj = getInt();
            String sqlz = "delete from " + playlistName + " where pid= ? ";
            PreparedStatement st1 = connection.prepareStatement(sqlz);
            st1.setInt(1, idj);
            int r = st1.executeUpdate();
            if (r > 0) {
                System.out.println("Deleted Succesfully");
            } else {
                System.out.println("This song not in playlist");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Method to delete playlist
    public void deletepl() throws SQLException {
        // playlist print
        String sql99 = "select playlistname from userplaylists where username = ?";
        PreparedStatement ps99 = connection.prepareStatement(sql99);
        ps99.setString(1, emailidoperation);
        ResultSet rs99 = ps99.executeQuery();
        ResultSet rstemp = rs99;
        System.out.println("Your Own playlists");
        boolean check = false;
        if (rstemp.next()) {
            while (rs99.next()) {
                System.out.println(rs99.getString(1));
            }
            check = true;
        } else {
            System.out.println("No Playlist Own");
        }
        // code for delete playlist
        if (check) {
            System.out.println("Enter Playlist Name");
            String playlistname = getString();

            String deleteaccsql2 = "drop table " + playlistname + "";
            PreparedStatement deleteaccpst2 = connection.prepareStatement(deleteaccsql2);
            int r2 = deleteaccpst2.executeUpdate();

            String deleteaccsql3 = "delete from userplaylists where playlistname = ?";
            PreparedStatement deleteaccpst3 = connection.prepareStatement(deleteaccsql3);
            deleteaccpst3.setString(1, playlistname);
            int r3 = deleteaccpst3.executeUpdate();

            if (r2 > 0 && r3 > 0) {
                System.out.println("Playlist Delete Successfully.");
            }
        }

    }

}

class StackUsingLinkedList {

    class Node {
        String data;
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node top;

    public StackUsingLinkedList() {
        this.top = null;
    }

    public void push(String data) {
        Node newNode = new Node(data);
        newNode.next = top;
        top = newNode;
        System.out.println(data + " pushed to stack");
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void display() {
        if (top == null) {
            System.out.println("You Dont Play Song");
            return;
        }
        Node current = top;
        while (current != null) {
            System.out.println(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }
}
