package com.fakenews.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * InputUtil - Utility for safe console input reading.
 */
public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Reads a non-empty string from the console. */
    public static String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input;
    }

    /** Reads a non-empty string, re-prompts if empty. */
    public static String readRequiredString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) System.out.println("  [!] This field cannot be empty. Please try again.");
        } while (input.isEmpty());
        return input;
    }

    /** Reads a multi-line string until the user types END on a new line. */
    public static String readMultiLine(String prompt) {
        System.out.println(prompt);
        System.out.println("  (Type your content below. Type 'END' on a new line when done)");
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equalsIgnoreCase("END")) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }

    /** Reads an integer, re-prompts on invalid input. */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number. Please enter a valid integer.");
            }
        }
    }

    /** Reads an integer within a given range. */
    public static int readIntInRange(String prompt, int min, int max) {
        int value;
        do {
            value = readInt(prompt);
            if (value < min || value > max)
                System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
        } while (value < min || value > max);
        return value;
    }

    /** Reads a date in dd-MM-yyyy format. */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd-MM-yyyy): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date format. Please use dd-MM-yyyy.");
            }
        }
    }

    /** Reads a password (console does not mask in standard Java, but prompt is clear). */
    public static String readPassword(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Reads a yes/no confirmation. */
    public static boolean readConfirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    /** Pauses and waits for user to press Enter. */
    public static void pause() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

    /** Prints a section divider. */
    public static void printDivider() {
        System.out.println("─".repeat(65));
    }

    /** Prints a thick section header divider. */
    public static void printHeader(String title) {
        System.out.println("\n" + "═".repeat(65));
        System.out.printf("  %s%n", title.toUpperCase());
        System.out.println("═".repeat(65));
    }
}
