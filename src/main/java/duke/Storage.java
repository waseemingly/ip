package duke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Represents a storage system to handle reading and writing tasks to a file.
 */
public class Storage {
    private String directoryPath;
    private String fileName;
    private String filePath;

    /**
     * Initializes a new instance of the Storage class.
     *
     * @param filePath The path to the file used for storing tasks.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        this.directoryPath = file.getParent();
        this.fileName = file.getName();
    }

    /**
     * Saves the tasks into the file.
     *
     * @param tasks The list of tasks to be saved.
     * @throws IOException If there's an issue writing to the file.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        // Ensure the directory exists
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create file instance
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks) {
                writer.write(task.toFileString() + "\n");
            }
        }
    }

    /**
     * Loads tasks from the file.
     *
     * @return An ArrayList of tasks loaded from the file.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    public ArrayList<Task> loadTasks() throws FileNotFoundException {
        File file = new File(filePath);
        ArrayList<Task> tasks = new ArrayList<>();

        if (!file.exists()) {
            return tasks; // return empty list if file doesn't exist
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" \\| ");

                if (parts[0].equals("T")) {
                    Todo todo = new Todo(parts[2]);
                    if (parts[1].equals("1")) {
                        todo.markAsDone();
                    }
                    tasks.add(todo);
                } else if (parts[0].equals("D")) {
                    LocalDateTime dateTime;
                    DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
                    DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    try {
                        dateTime = LocalDateTime.parse(parts[3], defaultFormatter);
                    } catch (DateTimeParseException e1) {
                        try {
                            dateTime = LocalDateTime.parse(parts[3], isoFormatter);
                        } catch (DateTimeParseException e2) {
                            System.out.println("Error parsing date-time from saved data: " + parts[3]);
                            continue; // Skip to the next loop iteration if date parsing fails
                        }
                    }
                    Deadline deadline = new Deadline(parts[2], dateTime);
                    if (parts[1].equals("1")) {
                        deadline.markAsDone();
                    }
                    tasks.add(deadline);
                } else if (parts[0].equals("E")) {
                    LocalDateTime dateTimeFrom;
                    LocalDateTime dateTimeTo;
                    DateTimeFormatter defaultFormat = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
                    DateTimeFormatter isoFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                    try {
                        dateTimeFrom = LocalDateTime.parse(parts[3], defaultFormat);
                    } catch (DateTimeParseException e1) {
                        try {
                            dateTimeFrom = LocalDateTime.parse(parts[3], isoFormat);
                        } catch (DateTimeParseException e2) {
                            System.out.println("Error parsing start date-time from saved data: " + parts[3]);
                            continue; // Skip to the next loop iteration if date parsing fails
                        }
                    }

                    try {
                        dateTimeTo = LocalDateTime.parse(parts[4], defaultFormat);
                    } catch (DateTimeParseException e1) {
                        try {
                            dateTimeTo = LocalDateTime.parse(parts[4], isoFormat);
                        } catch (DateTimeParseException e2) {
                            System.out.println("Error parsing end date-time from saved data: " + parts[4]);
                            continue; // Skip to the next loop iteration if date parsing fails
                        }
                    }

                    Event event = new Event(parts[2], dateTimeFrom, dateTimeTo);
                    if (parts[1].equals("1")) {
                        event.markAsDone();
                    }
                    tasks.add(event);
                }
            }
        }

        return tasks;
    }
}
