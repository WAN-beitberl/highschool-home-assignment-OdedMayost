import java.sql.*;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        //InitializeTables(); - This function is responsible for normalizing the information
        //                      from the CSV files and inserting it into the tables
        //                      (this function needs to run once, so it is in the comment that after one run it changes the database)

        Scanner sc = new Scanner(System.in);
        int num;
        String result;
        String query;

        System.out.println("We are finally in the 21st century, after all the suffering we went through, \n" +
                "computers came to save our lives. \n" +
                "It's time to leave behind the page and the pen, and let the computer do the work for us \n" +
                "(and we all know it will do it much better than us).\n" +
                "So let's go to a new and efficient way ->\n");
        System.out.println();

        boolean IsOver = false;
        while (!IsOver) {
            System.out.println("Main menu - please enter the number of the operation you wish to perform:\n" +
                    "1 - Receiving the school grade point average\n" +
                    "2 - Receiving the grade point average of the male students at the school\n" +
                    "3 - Receiving the grade point average of the female students at the school\n" +
                    "4 - Receiving the average height of students over 2 meters tall, who own a purple car\n" +
                    "5 – Receiving the ID of the friends of a particular student, and the ID of the friends of his friends\n" +
                    "6 - The social situation at school: the percentage of popular students, the percentage of ordinary students, the percentage of lonely students.\n" +
                    "7 - Receiving the grade point average of a particular student\n" +
                    "8 - Closing the system");
            System.out.print("Number of operation: ");
            num = sc.nextInt();
            System.out.println();

            switch (num) {
                case 1:
                    query = "SELECT SUM(GradeAVG)/COUNT(*) as school_average FROM StudentGradesView;";
                    result = String.valueOf(SQLString(query, 1,2));
                    System.out.println("Average school grades: " + result);
                    break;
                case 2:
                    query = "SELECT SUM(GradeAVG)/COUNT(*) as boys_average_grade FROM Students WHERE Gender = 'Male';";
                    result = String.valueOf(SQLString(query, 1,2));
                    System.out.println("The average grades of the boys at school: " + result);
                    break;
                case 3:
                    query = "SELECT SUM(GradeAVG)/COUNT(*) as girls_average_grade FROM Students WHERE Gender = 'Female';";
                    result = String.valueOf(SQLString(query, 1,2));
                    System.out.println("The average grades of the girls at school: " + result);
                    break;
                case 4:
                    query = "SELECT SUM(cmHeight)/count(*) as average_height FROM Students WHERE cmHeight > 200 AND HasCar = yes AND CarColor = 'purple';";
                    result = String.valueOf(SQLString(query, 1,2));
                    System.out.println("The average height of students over 2 meters tall, who own a purple car: " + result);
                    break;
                case 5:
                    System.out.print("Enter ID: ");
                    num = sc.nextInt();
                    System.out.println();
                    query = "SELECT DISTINCT FriendID, OtherFriendID FROM StudentsFriendships WHERE " +
                            "FriendID IN (SELECT DISTINCT FriendID FROM StudentsFriendships " +
                            "WHERE FriendID = " + Integer.toString(num) + " OR OtherFriendID = " + Integer.toString(num) + ")" +
                            "OR OtherFriendID IN (SELECT DISTINCT FriendID FROM StudentsFriendships " +
                            "WHERE FriendID = " + Integer.toString(num) + " OR OtherFriendID = " + Integer.toString(num) + ");";
                    result = String.valueOf(SQLString(query, 2,3));
                    System.out.println("The friends of a student whose ID is " + Integer.toString(num) + " " +
                            "and the friends of his friends are: " + result);
                    break;
                case 6:
                    SocialSituation();
                    break;
                case 7:
                    System.out.print("Enter ID: ");
                    num = sc.nextInt();
                    System.out.println();
                    query = "SELECT GradeAVG FROM StudentGradesView WHERE Identification = " + Integer.toString(num) + ";";
                    result = String.valueOf(SQLString(query, 1,2));
                    System.out.println("The average of the student whose ID is " + Integer.toString(num) + " is - " + result);
                    break;
                case 8:
                    IsOver = true;
                    break;
                default:
                    System.out.println("----- Error: Invalid input -----");
            }
            System.out.println();
        }
    }

    public static StringBuilder SQLString(String query, int StartingColumn, int EndColumn) throws SQLException {
        StringBuilder data = new StringBuilder();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
        }

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sima", "root", "oded97531");
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.execute();
            ResultSet result = statement.getResultSet();

            if (result != null) {
                while (result.next()) {
                    for (int i = StartingColumn; i < EndColumn; i++) {
                        data.append(result.getString(i));
                        data.append(" ");
                    }
                }
            }
            con.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return data;
    }

    public static void SocialSituation() throws SQLException {
        int[] FriendsCounter = new int[1001];
        Arrays.fill(FriendsCounter, 0);

        float popular = 0;
        float regular = 0;
        float lonely = 0;

        String query = "SELECT FriendID, OtherFriendID FROM StudentsFriendships;";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
        }

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sima", "root", "oded97531");
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.execute();
            ResultSet result = statement.getResultSet();

            if (result != null) {
                while (result.next()) {
                    FriendsCounter[result.getInt(1)]++;
                    FriendsCounter[result.getInt(2)]++;
                }
            }
            con.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        for (int index = 1; index <= 1000; index++) {
            if (FriendsCounter[index] >= 2) {
                popular++;
            } else if (FriendsCounter[index] == 1) {
                regular++;
            } else {
                lonely++;
            }
        }

        popular = popular / 1000 * 100;
        regular = regular / 1000 * 100;
        lonely = lonely / 1000 * 100;

        System.out.println("Percentage of popular students: " + Float.toString(popular));
        System.out.println("Percentage of regular students: " + Float.toString(regular));
        System.out.println("Percentage of lonely students: " + Float.toString(lonely));
    }

    public static void InitializeTables() throws SQLException {
        String highschool_path = "D:\\Beit Barel College\\Java\\Teacher Sima\\CSVfiles\\highschool.csv";
        InitDatabase(highschool_path, "Students");

        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println();

        String highschool_friendships_path = "D:\\Beit Barel College\\Java\\Teacher Sima\\CSVfiles\\highschool_friendships.csv";
        InitDatabase(highschool_friendships_path, "StudentsFriendships");
    }

    public static void InitDatabase(String file_path, String table_name) throws SQLException {
        ArrayList<String[]> data = readCSV(file_path);

        for (String[] values : data) {
            StringBuilder query = new StringBuilder("INSERT INTO " + table_name + " VALUES (");
            if (values.length == 13) {
                for (int i = 0; i < values.length; i++) {
                    if ((i == 0) || (i == 6) || (i == 7) || (i == 8) || (i == 10) ||
                            (i == 11) || (i == 12) || (values[i] == "NULL")) {
                        query.append(values[i]);
                    } else {
                        query.append("'");
                        query.append(values[i]);
                        query.append("'");
                    }
                    if(values.length - i != 1) {
                        query.append(", ");
                    }
                }
            } else if (values.length == 3) {
                for (int i = 0; i < values.length; i++) {
                    query.append(values[i]);
                    if(values.length - i != 1) {
                        query.append(", ");
                    }
                }
            } else {
                System.out.println("Error: " + query.toString());
            }
            query.append(");");
            SQLTables(query.toString());
        }
    }

    public static void SQLTables(String query) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found ...");
        }

        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sima", "root", "oded97531");
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.execute();
            con.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }

    public static ArrayList<String[]> readCSV(String file) {
        ArrayList<String[]> data = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = DatabaseNormalization(line.split(","));
                if (values[0] != "error") {
                    data.add(values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String[] DatabaseNormalization(String[] line) {
        String[] data = new String[line.length];
        String[] error = {"error"};

        if ((line.length == 3) || (line.length == 13)) {
            for (int i = 0; i < line.length; i++) {
                if ((line[i] == "")){
                    if((i == 0) || (i == 1) || (i == 2) || (i == 5) || (i == 6) || (i == 7) || (i == 12)) {
                        return error;
                    } else if (i == 8) {
                        data[8] = "FALSE";
                    } else {
                        data[i] = "NULL";
                    }
                } else if(i == 9) {
                    if (data[8] == "FALSE") {
                        data[9] = "NULL";
                    } else if ((data[8] == "TRUE") && (line[9] == "")) {
                        data[9] = "Unknown";
                    } else {
                        data[9] = line[9];
                    }
                } else if (line[i].indexOf('\'') != -1) {
                    data[i] = (line[i].split("'"))[1];
                } else {
                    data[i] = line[i];
                }
            }
            return data;
        } else {
            return error;
        }
    }

    public static float AverageSchoolGrades() throws SQLException {
        String query = "SELECT SUM(GradeAVG)/COUNT(*) as school_average FROM  StudentGradesView;";
        float num = -1;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
        }

        // Connect to MySql.
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sima", "root", "oded97531");
        try {
            // Enter Query.
            PreparedStatement statement = con.prepareStatement(query);

            // Execute Query.
            statement.execute();
            ResultSet result = statement.getResultSet();

            // Print Output.
            if (result != null) {
                while (result.next()) {
                    String data = result.getString(1);
                    num = Float.parseFloat(data);
                }
            }
            con.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return num;
    }
}

