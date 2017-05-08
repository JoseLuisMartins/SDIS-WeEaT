package src.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    //Root path for pgsql folder
    public static String bin_path = "C:\\PostgreSQL\\pg96\\bin";

    public static void setRoot_path(String root_path) {
        DatabaseManager.bin_path = root_path;
    }

    public static void database_create(){

        List<String> commands = new ArrayList<String>();

        commands.add(bin_path + File.separator + "createdb");
        commands.add("-h");
        commands.add("localhost");
        commands.add("-p");
        commands.add("5432");
        commands.add("-U");
        commands.add("postgres");
        commands.add("weeat");

        ProcessBuilder pb = new ProcessBuilder(commands);

        launch_process(pb);

        System.out.println("Create Successfull");
    };

    public static void database_delete(){
        List<String> commands = new ArrayList<String>();

        commands.add(bin_path + File.separator + "dropdb");
        commands.add("-h");
        commands.add("localhost");
        commands.add("-p");
        commands.add("5432");
        commands.add("-U");
        commands.add("postgres");
        commands.add("weeat");

        ProcessBuilder pb = new ProcessBuilder(commands);

        launch_process(pb);

        System.out.println("Delete Successfull");
    };

    public static void database_backup(){

        List<String> commands = new ArrayList<String>();

        commands.add(bin_path + File.separator + "pg_dump");
        commands.add("-h");
        commands.add("localhost");
        commands.add("-p");
        commands.add("5432");
        commands.add("-U");
        commands.add("postgres");
        commands.add("-F");
        commands.add("c");
        commands.add("-v");
        commands.add("-f");
        commands.add('.' + File.separator + "db.backup");
        commands.add("weeat");

        ProcessBuilder pb = new ProcessBuilder(commands);

        launch_process(pb);

        System.out.println("Backup Successfull");
    };

    public static void database_restore(){

        List<String> commands = new ArrayList<String>();

        commands.add(bin_path + File.separator + "pg_restore");
        commands.add("-h");
        commands.add("localhost");
        commands.add("-p");
        commands.add("5432");
        commands.add("-U");
        commands.add("postgres");    // username do postgres
        commands.add("-d");
        commands.add("weeat");		// nome da database a fazer restore
        commands.add("-v");
        commands.add('.' + File.separator + "db.backup"); // local do file output após backup

        ProcessBuilder pb = new ProcessBuilder(commands);

        launch_process(pb);

        System.out.println("Restore Successfull");
    };

    private static void launch_process(ProcessBuilder pb) {

        pb.environment().put("PGPASSWORD", "q1w2e3r4t5");

        try {
            Process p = pb.start();

            /*
            // Handle de erros
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = buf.readLine();

            while (line != null) {
                System.out.println(line);
                line = buf.readLine();
            }
            buf.close();
*/
            p.waitFor();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
