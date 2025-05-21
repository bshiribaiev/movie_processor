// Bekbol Shiribaiev

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class Movie implements Comparable<Movie> {
    private String title;
    private int year;
    private String genre;
    private double avgRating;

    public Movie(String title, int year, String genre, int rating1, int rating2, int rating3) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        // Compute the average rating as a double with one decimal precision later when printing.
        this.avgRating = (rating1 + rating2 + rating3) / 3.0;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public double getAvgRating() {
        return avgRating;
    }

    // Compare movies by average rating in descending order.
    @Override
    public int compareTo(Movie other) {
        // For descending order, reverse the comparison.
        return Double.compare(other.avgRating, this.avgRating);
    }
}

public class MovieProcessor {

    // Checks if a given year is in the 20th or 21st century.
    private static boolean isValidYear(String yearStr) {
        try {
            int year = Integer.parseInt(yearStr);
            return (year >= 1900 && year < 2100);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if a rating is an integer between 1 and 100.
    private static boolean isValidRating(String ratingStr) {
        try {
            int rating = Integer.parseInt(ratingStr);
            return (rating >= 1 && rating <= 100);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validates a CSV row. Expects an array of 6 items.
    private static boolean isValidRow(String[] items, int lineNum) {
        if (items.length != 6) {
            System.err.println("Invalid row at line " + lineNum + ": Incorrect number of fields.");
            return false;
        }
        if (!isValidYear(items[1].trim())) {
            System.err.println("Invalid row at line " + lineNum + ": Year (" + items[1].trim() + ") is not in the 20th or 21st century.");
            return false;
        }
        if (!isValidRating(items[3].trim()) ||
            !isValidRating(items[4].trim()) ||
            !isValidRating(items[5].trim())) {
            System.err.println("Invalid row at line " + lineNum + ": One or more ratings are not between 1 and 100.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        // CSV file path (change this as needed or pass as command line argument)
        String csvFile = "movies.csv";
        if (args.length > 0) {
            csvFile = args[0];
        }

        int validCount = 0;
        int lineNum = 0;
        String headerLine = null;

        // First pass: Count the valid rows and validate the header.
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            headerLine = br.readLine();
            lineNum++;
            if (headerLine == null) {
                System.err.println("CSV file is empty.");
                return;
            }
            // Validate header row (assuming header is: Title,Year,Genre,Rating1,Rating2,Rating3)
            String[] headers = headerLine.split(",");
            if (headers.length != 6 ||
                !headers[0].trim().equalsIgnoreCase("Title") ||
                !headers[1].trim().equalsIgnoreCase("Year") ||
                !headers[2].trim().equalsIgnoreCase("Genre") ||
                !headers[3].trim().equalsIgnoreCase("Rating1") ||
                !headers[4].trim().equalsIgnoreCase("Rating2") ||
                !headers[5].trim().equalsIgnoreCase("Rating3")) {
                System.err.println("CSV header is invalid.");
                return;
            }
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String[] tokens = line.split(",");
                if (isValidRow(tokens, lineNum)) {
                    validCount++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        // Create an array for the valid movies
        Movie[] movies = new Movie[validCount];
        int index = 0;
        lineNum = 0;

        // Second pass: Read the file again and populate the Movie array.
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // skip header
            lineNum++;
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String[] tokens = line.split(",");
                if (isValidRow(tokens, lineNum)) {
                    // Trim tokens to remove extraneous spaces.
                    String title = tokens[0].trim();
                    int year = Integer.parseInt(tokens[1].trim());
                    String genre = tokens[2].trim();
                    int rating1 = Integer.parseInt(tokens[3].trim());
                    int rating2 = Integer.parseInt(tokens[4].trim());
                    int rating3 = Integer.parseInt(tokens[5].trim());
                    movies[index++] = new Movie(title, year, genre, rating1, rating2, rating3);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        // Sort the movies in descending order by average rating.
        Arrays.sort(movies);

        // Determine the width of the Title column.
        int titleWidth = "Title".length();
        for (Movie movie : movies) {
            if (movie.getTitle().length() > titleWidth) {
                titleWidth = movie.getTitle().length();
            }
        }
        // Fixed width for Genre column.
        int genreWidth = 10;

        // Print header.
        String headerFormat = "%-" + titleWidth + "s | %4s | %-" + genreWidth + "s | %10s%n";
        System.out.printf(headerFormat, "Title", "Year", "Genre", "Avg Rating");
        
        // Print a separator line.
        int totalWidth = titleWidth + 3 + 4 + 3 + genreWidth + 3 + 10;
        for (int i = 0; i < totalWidth; i++) {
            System.out.print("-");
        }
        System.out.println();

        // Print each movie with the required format.
        // Avg Rating is printed with one decimal place.
        String rowFormat = "%-" + titleWidth + "s | %4d | %-" + genreWidth + "s | %10.1f%n";
        for (Movie movie : movies) {
            System.out.printf(rowFormat, movie.getTitle(), movie.getYear(), movie.getGenre(), movie.getAvgRating());
        }
    }
}